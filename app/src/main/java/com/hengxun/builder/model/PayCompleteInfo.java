package com.hengxun.builder.model;

/**
 * Created by ZY on 2016/4/18.
 */
public class PayCompleteInfo {

    /**
     * code : 200
     * time : 1460990694458
     * message : null
     * dataMap : {"status":1}
     */

    private int code;
    private long time;
    private Object message;
    /**
     * status : 1
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
        private int status;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
