package com.hengxun.builder.model;

/**
 * Created by ZY on 2016/3/28.
 * 工人账户
 */
public class UserAccount {

    /**
     * code : 0
     * time : 0
     * message : null
     * dataMap : {"draw_amount":900}
     */

    private int code;
    private int time;
    private Object message;
    /**
     * draw_amount : 900.0
     */

    private DataMapEntity dataMap;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public DataMapEntity getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMapEntity dataMap) {
        this.dataMap = dataMap;
    }

    public static class DataMapEntity {
        private double draw_amount;

        public double getDraw_amount() {
            return draw_amount;
        }

        public void setDraw_amount(double draw_amount) {
            this.draw_amount = draw_amount;
        }
    }
}
