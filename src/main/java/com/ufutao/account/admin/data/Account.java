package com.ufutao.account.admin.data;

/**
 * @author lutong
 */
public class Account {
    long uid;
    /** 单位：分 */
    long money;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }
}
