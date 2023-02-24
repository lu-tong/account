package com.ufutao.account.admin.entity;

import com.ufutao.account.admin.data.Pdu;

/**
 * @author lutong
 */
public class PduUtils {
    public static <TO, FROM> Pdu<TO> from(Pdu<FROM> origin) {
        if (PduUtils.isSuccess(origin)) {
            return PduUtils.success(null);
        }
        return new Pdu<>(origin.getCode(), origin.getMsg(), null);
    }

    public static <T> Pdu<T> fail(String msg) {
        return new Pdu<>(0, msg, null);
    }

    public static <T> Pdu<T> success(T data) {
        return new Pdu<>(1, "成功", data);
    }

    public static <T> Pdu<T> error(String msg) {
        return new Pdu<>(-10, msg, null);
    }

    public static <T> boolean isFail(Pdu<T> pdu) {
        return pdu.getCode() == 0;
    }

    public static <T> boolean isSuccess(Pdu<T> pdu) {
        return pdu.getCode() == 1;
    }

    public static <T> boolean isError(Pdu<T> pdu) {
        return pdu.getCode() < 0;
    }
}
