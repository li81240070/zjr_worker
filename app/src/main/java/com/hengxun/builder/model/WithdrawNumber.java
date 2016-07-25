package com.hengxun.builder.model;

import java.io.Serializable;

/**
 * Created by ZY on 2016/4/4.
 */
public class WithdrawNumber implements Serializable{

    /**
     * code : 200
     * time : 1459764959358
     * message : null
     * dataMap : {"amount":625}
     */

    private int code;
    private long time;
    private String message;
    /**
     * amount : 625.0
     */

    private DataMapEntity dataMap;

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

    public DataMapEntity getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMapEntity dataMap) {
        this.dataMap = dataMap;
    }

    public static class DataMapEntity implements Serializable{
        private double amount;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }
}
