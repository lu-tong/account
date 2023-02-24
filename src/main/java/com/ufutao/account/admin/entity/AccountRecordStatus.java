package com.ufutao.account.admin.entity;

/**
 * @author lutong
 */

public enum AccountRecordStatus {
    Undo("未执行/异常"),
    Success("成功"),
    Fail("失败");
    private final String note;

    AccountRecordStatus(String note) {
        this.note = note;
    }

    public int toInt() {
        return this.ordinal();
    }

    public String getNote() {
        return note;
    }
}
