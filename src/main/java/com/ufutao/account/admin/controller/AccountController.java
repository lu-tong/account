package com.ufutao.account.admin.controller;

import com.ufutao.account.admin.data.Account;
import com.ufutao.account.admin.data.AccountRecord;
import com.ufutao.account.admin.data.Pdu;
import com.ufutao.account.admin.entity.PduUtils;
import com.ufutao.account.admin.service.AccountRecordService;
import com.ufutao.account.admin.service.AccountService;
import com.ufutao.account.admin.service.OrderVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author lutong
 */
@RestController
@RequestMapping("/account")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;
    private final AccountRecordService accountRecordService;
    private final OrderVerifier orderVerifier;

    @Autowired
    public AccountController(AccountService accountService, AccountRecordService accountRecordService, OrderVerifier orderVerifier) {
        this.accountService = accountService;
        this.accountRecordService = accountRecordService;
        this.orderVerifier = orderVerifier;
    }

    @GetMapping("/register")
    public Pdu<Long> register(long uid) {
        try {
            if (accountService.registerAccount(uid)) {
                return PduUtils.success(uid);
            }else {
                return PduUtils.fail("账户名已被注册");
            }
        }catch (Throwable th) {
            logger.error("系统繁忙，稍后注册", th);
            return PduUtils.error("系统繁忙，稍后注册");
        }
    }

    /**
     * 查询用户钱包余额
     *
     * @param uid 用户uid
     */
    @GetMapping("/queryMoney")
    public Pdu<Long> queryMoney(long uid) {
        try {
            Account account = accountService.query(uid);
            if (Objects.nonNull(account)) {
                return PduUtils.success(account.getMoney());
            }
            return PduUtils.fail("无权访问");
        } catch (Throwable th) {
            logger.error("查找失败", th);
            return PduUtils.error("系统异常");
        }
    }

    /**
     * 给用户退款20元接口
     */
    @PostMapping("/addMoney")
    public Pdu<Long> addMoney(String sign, @RequestBody AccountRecord record) {
        boolean valid = orderVerifier.verify(record, sign);
        if (!valid) {
            logger.warn("验证不通过: channel{} orderId{}", record.getChannel(), record.getOrderId());
            return PduUtils.fail("验证不通过");
        }
        return accountService.addMoney(record);
    }

    /**
     * 用户消费100元的接口
     */
    @PostMapping("/reduceMoney")
    public Pdu<Long> reduceMoney(String sign, @RequestBody AccountRecord record) {
        boolean valid = orderVerifier.verify(record, sign);
        if (!valid) {
            logger.warn("验证不通过: channel{} orderId{}", record.getChannel(), record.getOrderId());
            return PduUtils.fail("验证不通过");
        }
        return accountService.reduceMoney(record);
    }

    /**
     * 查询用户钱包金额变动明细的接口
     */
    @GetMapping("/queryHistory")
    public Pdu<List<AccountRecord>> queryHistory(long uid, Date start, Date end) {
        try {
            if (uid <= 0) {
                logger.warn("尝试访问非法用户 {{}}", uid);
                return PduUtils.fail("无权访问");
            }
            if (Objects.isNull(start)) {
                start = new Date();
            }
            if (Objects.isNull(end)) {
                end = new Date(start.getTime() - TimeUnit.DAYS.toMillis(30));
            }

            return PduUtils.success(accountRecordService.queryHistory(uid, start, end));
        } catch (Throwable th) {
            logger.error("查找失败", th);
            return PduUtils.error("系统异常");
        }
    }
}
