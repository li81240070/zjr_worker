package com.hengxun.builder.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZY on 2016/3/26.
 */
public class UserInfo implements Serializable {
    /**
     * code : 200
     * time : 1460612876196
     * message : null
     * dataMap : {"tags":[],"gift_num":10,"scope":["1001","1002","1008","1011","1015","1019","1023","1027","1033","1040","1051","1055","1057"],"month_order":1,"status":2,"star":1,"avatar":"d:/resource/AVATAR/2016/4/5/814/","amount":135.2,"worker_no":"201502031103","token":"1e58624c0c7742a1b58775839d4049a2","userId":3,"real_name":"姚东野2","waitPay":true,"gender":"男","points":169,"total_order":45,"gift_val":["20","50","100"],"mobile":"13940235698"}
     */

    private int code;
    private long time;
    private String message;
    /**
     * tags : []
     * gift_num : 10
     * scope : ["1001","1002","1008","1011","1015","1019","1023","1027","1033","1040","1051","1055","1057"]
     * month_order : 1
     * status : 2
     * star : 1
     * avatar : d:/resource/AVATAR/2016/4/5/814/s8das4d1qw6f1ds3gf
     * amount : 135.2
     * worker_no : 201502031103
     * token : 1e58624c0c7742a1b58775839d4049a2
     * userId : 3
     * real_name : 姚东野2
     * waitPay : true
     * gender : 男
     * points : 169
     * total_order : 45
     * gift_val : ["20","50","100"]
     * mobile : 13940235698
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
        private int gift_num;
        private int month_order;
        private int status;
        private int star;
        private String avatar;
        private double amount;
        private String worker_no;
        private String token;
        private int userId;
        private String real_name;
        private boolean waitPay;
        private String gender;
        private int points;
        private int total_order;
        private String mobile;
        private List<String> tags;
        private List<String> scope;
        private List<String> gift_val;

        public int getGift_num() {
            return gift_num;
        }

        public void setGift_num(int gift_num) {
            this.gift_num = gift_num;
        }

        public int getMonth_order() {
            return month_order;
        }

        public void setMonth_order(int month_order) {
            this.month_order = month_order;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getStar() {
            return star;
        }

        public void setStar(int star) {
            this.star = star;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getWorker_no() {
            return worker_no;
        }

        public void setWorker_no(String worker_no) {
            this.worker_no = worker_no;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public boolean isWaitPay() {
            return waitPay;
        }

        public void setWaitPay(boolean waitPay) {
            this.waitPay = waitPay;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public int getTotal_order() {
            return total_order;
        }

        public void setTotal_order(int total_order) {
            this.total_order = total_order;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public List<String> getScope() {
            return scope;
        }

        public void setScope(List<String> scope) {
            this.scope = scope;
        }

        public List<String> getGift_val() {
            return gift_val;
        }

        public void setGift_val(List<String> gift_val) {
            this.gift_val = gift_val;
        }
    }
}
