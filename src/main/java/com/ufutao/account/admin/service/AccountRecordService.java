package com.ufutao.account.admin.service;

import com.ufutao.account.admin.data.AccountRecord;
import com.ufutao.account.admin.data.Pdu;
import com.ufutao.account.admin.entity.AccountRecordStatus;
import com.ufutao.account.admin.entity.IdBuilder;
import com.ufutao.account.admin.entity.PduUtils;
import com.ufutao.account.admin.entity.ResultSetExtractors;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author lutong
 */
@Service
public class AccountRecordService {
    private static final Logger logger = LoggerFactory.getLogger(AccountRecordService.class);
    private final JdbcTemplate accountDb;
    private final IdBuilder idBuilder;
    private final RedissonClient redissonClient;

    @Autowired
    public AccountRecordService(JdbcTemplate accountDb, OrderVerifier orderVerifier, RedissonClient redissonClient) {
        this.accountDb = accountDb;
        this.redissonClient = redissonClient;
        this.idBuilder = new IdBuilder(0);
    }

    interface Callback<P, R> {
        R call(P p);
    }

    public Pdu<Long> doRecord(AccountRecord record, Callback<AccountRecord, Pdu<Long>> action) {
        if (record.getUid() == 0) {
            long id = idBuilder.createId();
            if (id == -1) {
                logger.error("id 生成失败，请检查当前系统的时间是否正确 now={}, last={}", System.currentTimeMillis(), idBuilder.getLastTime());
                return PduUtils.error("系统异常");
            }
            record.setUid(id);
            record.setCreateTime(new Date());
        }

        RLock rLock = redissonClient.getLock(String.format("%s-%s", record.getChannel(), record.getOrderId()));
        try {
            if (!rLock.tryLock(10, TimeUnit.SECONDS)) {
                logger.warn("加锁超时 {{{}}}", rLock.getName());
                return PduUtils.fail("系统繁忙");
            }

            Pdu<Void> doBefore = beforeRecord(record);
            if (!PduUtils.isSuccess(doBefore)) {
                return PduUtils.from(doBefore);
            }

            Pdu<Long> res = action.call(record);

            if (PduUtils.isSuccess(res)) {
                // 已成功
                record.setStatus(AccountRecordStatus.Success.toInt());
            }else if (PduUtils.isFail(res)) {
                // 已失败
                record.setStatus(AccountRecordStatus.Fail.toInt());
            }else {
                // 系统内部发生异常，允许调用方重试
                record.setStatus(AccountRecordStatus.Undo.toInt());
            }
            record.setModifyTime(new Date());
            accountDb.update(
                    "update account.account_record set status=?, modify_time=? where uid=?",
                    record.getStatus(), record.getModifyTime(), record.getUid()
            );
            return res;
        } catch (Throwable th) {
            logger.error("添加记录失败", th);
        } finally {
            if (Objects.nonNull(rLock)) {
                rLock.unlock();
            }
        }
        return PduUtils.fail("系统异常");
    }

    private Pdu<Void> beforeRecord(AccountRecord record) {
        try {
            accountDb.update(
                    "insert into account.account_record(uid,channel,order_id,account,action,number,status,create_time) values (?,?, ?, ?, ?, ?, ?, ?)",
                    record.getUid(), record.getChannel(), record.getOrderId(), record.getAccount(), record.getAction(),
                    record.getNumber(), AccountRecordStatus.Undo.toInt(), record.getCreateTime()
            );
            return PduUtils.success(null);
        } catch (DuplicateKeyException duplicateKeyException) {
            if (Objects.isNull(queryRecord(record.getChannel(), record.getOrderId(), AccountRecordStatus.Undo.toInt()))) {
                logger.warn("重复的请求已被拦截 channel: {}, orderId: {}", record.getChannel(), record.getOrderId());
                return PduUtils.fail("重复的请求");
            }
        } catch (Throwable th) {
            logger.error("系统异常，添加记录失败", th);
        }
        return PduUtils.error("系统异常");
    }

    public List<AccountRecord> queryHistory(long account, Date begin, Date end) {
        return this.accountDb.query("select uid, channel, order_id, action, account, number, status, modify_time, create_time from account_record where account=? and create_time between ? and ?",
                (rs, rowNum) -> ResultSetExtractors.ACCOUNT_RECORD.extractData(rs),
                account, begin, end
        );
    }

    private AccountRecord queryRecord(String channel, String orderId, int stat) {
        return accountDb.query("select uid, channel, order_id, action, account, number, status, modify_time, create_time from account_record where channel=? and order_id=? and status=?",
                ResultSetExtractors.ACCOUNT_RECORD,
                channel, orderId, stat
        );
    }

}
