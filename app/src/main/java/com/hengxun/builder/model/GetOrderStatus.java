package com.hengxun.builder.model;

/**
 * Created by ZY on 2016/4/15.
 */
public class GetOrderStatus {

    /**
     * code : 500
     * time : 1460724083953
     * message : 订单已被其他匠人受理。
     * dataMap : {}
     */

    private int code;
    private long time;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
