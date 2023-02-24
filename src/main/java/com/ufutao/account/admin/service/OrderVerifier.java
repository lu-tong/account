package com.ufutao.account.admin.service;


import com.ufutao.account.admin.data.AccountRecord;
import org.springframework.stereotype.Service;

/**
 * @author lutong
 */
@Service
public class OrderVerifier {
    public boolean verify(AccountRecord record, String sign) {
        return true;
    }
}
