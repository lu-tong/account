package com.ufutao.account.admin.entity;

import com.ufutao.account.admin.data.Account;
import com.ufutao.account.admin.data.AccountRecord;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * @author lutong
 */
public class ResultSetExtractors {
    public static final ResultSetExtractor<Account> ACCOUNT = rs -> {
        if (!rs.next()) {
            return null;
        }
        Account account = new Account();
        account.setUid(rs.getLong("uid"));
        account.setMoney(rs.getLong("money"));
        return account;
    };

    public static final ResultSetExtractor<AccountRecord> ACCOUNT_RECORD = rs -> {
        if (!rs.next()) {
            return null;
        }
        AccountRecord accountRecord = new AccountRecord();
        accountRecord.setUid(rs.getLong("uid"));
        accountRecord.setChannel(rs.getString("channel"));
        accountRecord.setOrderId(rs.getString("order_id"));
        accountRecord.setAction(rs.getString("action"));
        accountRecord.setAccount(rs.getLong("account"));
        accountRecord.setNumber(rs.getLong("number"));
        accountRecord.setStatus(rs.getInt("status"));
        accountRecord.setModifyTime(rs.getTimestamp("modify_time"));
        accountRecord.setCreateTime(rs.getTimestamp("create_time"));
        return accountRecord;
    };
}
