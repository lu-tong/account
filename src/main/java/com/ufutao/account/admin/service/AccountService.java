package com.ufutao.account.admin.service;

import com.ufutao.account.admin.data.Account;
import com.ufutao.account.admin.data.AccountRecord;
import com.ufutao.account.admin.data.Pdu;
import com.ufutao.account.admin.entity.PduUtils;
import com.ufutao.account.admin.entity.ResultSetExtractors;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author lutong
 */
@Service
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final JdbcTemplate accountDb;
    private final RedissonClient redissonClient;
    private final AccountRecordService accountRecordService;

    @Autowired
    public AccountService(JdbcTemplate accountDb, RedissonClient redissonClient, AccountRecordService accountRecordService) {
        this.accountDb = accountDb;
        this.redissonClient = redissonClient;
        this.accountRecordService = accountRecordService;
    }

    public boolean registerAccount(long uid) {
        Account old = query(uid);
        if (Objects.nonNull(old)) {
            return false;
        }
        accountDb.update("insert into account.account(uid, money) VALUES (?, 0)", uid);
        return true;
    }

    public Account query(long uid) {
        return this.accountDb.query("select uid, money from account where uid=?", ResultSetExtractors.ACCOUNT, uid);
    }

    private Pdu<Long> modMoney(AccountRecord record, AccountRecordService.Callback<AccountRecord, Pdu<Long>> modAction) {
        Account account = query(record.getAccount());
        if (Objects.isNull(account)) {
            logger.warn("账户{{}}不存在", record.getUid());
            return PduUtils.fail("账户不存在");
        }
        RLock rLock = redissonClient.getLock(String.format("account-money-%d", account.getUid()));
        try {
            if (!rLock.tryLock(10, TimeUnit.SECONDS)) {
                logger.warn("加锁超时 {{{}}}", rLock.getName());
                return PduUtils.fail("系统繁忙");
            }
            return accountRecordService.doRecord(record, modAction);
        }catch (Throwable th) {
            logger.error("", th);
            return PduUtils.error("系统异常");
        }finally {
            if (Objects.nonNull(rLock)) {
                rLock.unlock();
            }
        }
    }

    public Pdu<Long> addMoney(AccountRecord record) {
        Pdu<Long> res = this.modMoney(record, r -> {
            try {
                this.accountDb.update("update account.account set money=money+? where uid=?", r.getNumber(), r.getAccount());
                return PduUtils.success(r.getNumber());
            }catch (Throwable th) {
                logger.error("", th);
                return PduUtils.error("系统异常，稍后重试");
            }
        });

        // notice remote service or do some other things
        // like
        // if (PduUtils.isSuccess(res)) do xxxx;

        return res;
    }

    public Pdu<Long> reduceMoney(AccountRecord record) {
        Pdu<Long> res = this.modMoney(record, r -> {
            try {
                Account account = query(r.getAccount());
                if (account.getMoney() < r.getNumber()) {
                    logger.warn("账户{{}} 余额不足{{}} / {{}}", account.getUid(), account.getMoney(), r.getNumber());
                    return PduUtils.fail("账户余额不足");
                }

                this.accountDb.update(
                        "update account.account set money=money-? where uid=? and money=?",
                        r.getNumber(), r.getAccount(), account.getMoney()
                );
                return PduUtils.success(r.getNumber());
            }catch (Throwable th) {
                logger.error("", th);
                return PduUtils.error("系统异常，稍后重试");
            }
        });

        // notice remote service or do some other things
        // like
        // if (PduUtils.isSuccess(res)) do xxxx;

        return res;
    }
}
