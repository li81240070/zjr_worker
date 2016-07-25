package com.hengxun.builder.model;

/**
 * Created by ZY on 2016/4/13.
 */
public class RegionInfo {

    /**
     * code : 200
     * time : 1460530157938
     * message : null
     * dataMap : {"validateFlag":false}
     */

    private int code;
    private long time;
    private Object message;
    /**
     * validateFlag : false
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
        private boolean validateFlag;

        public boolean isValidateFlag() {
            return validateFlag;
        }

        public void setValidateFlag(boolean validateFlag) {
            this.validateFlag = validateFlag;
        }
    }
}
