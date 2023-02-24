package com.ufutao.account.admin.data;

import java.util.Date;

/**
 * @author lutong
 */
public class AccountRecord {
    /** 操作流水号 */
    private long uid;
    private String channel;
    private String orderId;
    private long account;
    /** 消费，退款，充值，... */
    private String action;
    private long number;
    private Date createTime;
    /** 0 未执行 1 成功 2 失败  */
    private int status;
    private Date modifyTime;

    public long getUid() {
        return uid;
    }

    /**
     * 1. 该接口仅提供给 格式化或初始化 的工具调用
     * 2. uid由系统自动生成，请不要修改
     */
    @Deprecated
    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getAccount() {
        return account;
    }

    public void setAccount(long account) {
        this.account = account;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date date) {
        this.createTime = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
