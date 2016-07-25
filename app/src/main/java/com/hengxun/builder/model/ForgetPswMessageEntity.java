package com.hengxun.builder.model;

/**
 * 忘记密码 发送验证码  服务器返回数据的实体类
 * Created by ge on 2016/3/29.
 */
public class ForgetPswMessageEntity {


    /**
     * code : 0
     * time : 0
     * message : null
     * dataMap : {"vaildCode":"783662"}
     */

    private int code;
    private int time;
    private Object message;
    /**
     * vaildCode : 783662
     */

    private DataMapBean dataMap;

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

    public DataMapBean getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMapBean dataMap) {
        this.dataMap = dataMap;
    }

    public static class DataMapBean {
        private String vaildCode;

        public String getVaildCode() {
            return vaildCode;
        }

        public void setVaildCode(String vaildCode) {
            this.vaildCode = vaildCode;
        }
    }
}
