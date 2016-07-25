package com.hengxun.builder.model;

/**
 * Created by ZY on 2016/4/6.
 */
public class Version {

    /**
     * code : 200
     * time : 1459930875650
     * message : null
     * dataMap : {"path":"https://www.pgyer.com/zjrjr","version":"1.2"}
     */

    private int code;
    private long time;
    private Object message;
    /**
     * path : https://www.pgyer.com/zjrjr
     * version : 1.2
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
        private String path;
        private String version;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
