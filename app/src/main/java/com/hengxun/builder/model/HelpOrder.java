package com.hengxun.builder.model;

import java.io.Serializable;

/**
 * Created by ZY on 2016/4/19.
 */
public class HelpOrder implements Serializable {

    /**
     * code : 200
     * time : 1461012798784
     * message : null
     * dataMap : {"detail":{"model":2,"order_id":"115","mobile":"15998434810","post_code":null,"address":"辽宁省大连市甘井子区火炬路15号","comment":"","service":"抢险抢修","order_status":"110","order_time":1461010709000,"adjustable":null,"appoint_time":1461012781000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"}}
     */

    private int code;
    private long time;
    private Object message;
    private int notifactionId;
    /**
     * detail : {"model":2,"order_id":"115","mobile":"15998434810","post_code":null,"address":"辽宁省大连市甘井子区火炬路15号","comment":"","service":"抢险抢修","order_status":"110","order_time":1461010709000,"adjustable":null,"appoint_time":1461012781000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"}
     */

    private DataMapEntity dataMap;

    public int getNotifactionId() {
        return notifactionId;
    }

    public void setNotifactionId(int notifactionId) {
        this.notifactionId = notifactionId;
    }

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

    public static class DataMapEntity implements Serializable {
        /**
         * model : 2
         * order_id : 115
         * mobile : 15998434810
         * post_code : null
         * address : 辽宁省大连市甘井子区火炬路15号
         * comment :
         * service : 抢险抢修
         * order_status : 110
         * order_time : 1461010709000
         * adjustable : null
         * appoint_time : 1461012781000
         * images : null
         * price : 0
         * timestamp : null
         * customerName : 未知客户
         * x : 0
         * y : 0
         */

        private DetailEntity detail;

        public DetailEntity getDetail() {
            return detail;
        }

        public void setDetail(DetailEntity detail) {
            this.detail = detail;
        }

        public static class DetailEntity implements Serializable {
            private int model;
            private String order_id;
            private String mobile;
            private Object post_code;
            private String address;
            private String comment;
            private String service;
            private String order_status;
            private long order_time;
            private Object adjustable;
            private long appoint_time;
            private Object images;
            private String price;
            private Object timestamp;
            private String customerName;
            private String x;
            private String y;

            public int getModel() {
                return model;
            }

            public void setModel(int model) {
                this.model = model;
            }

            public String getOrder_id() {
                return order_id;
            }

            public void setOrder_id(String order_id) {
                this.order_id = order_id;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public Object getPost_code() {
                return post_code;
            }

            public void setPost_code(Object post_code) {
                this.post_code = post_code;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public String getService() {
                return service;
            }

            public void setService(String service) {
                this.service = service;
            }

            public String getOrder_status() {
                return order_status;
            }

            public void setOrder_status(String order_status) {
                this.order_status = order_status;
            }

            public long getOrder_time() {
                return order_time;
            }

            public void setOrder_time(long order_time) {
                this.order_time = order_time;
            }

            public Object getAdjustable() {
                return adjustable;
            }

            public void setAdjustable(Object adjustable) {
                this.adjustable = adjustable;
            }

            public long getAppoint_time() {
                return appoint_time;
            }

            public void setAppoint_time(long appoint_time) {
                this.appoint_time = appoint_time;
            }

            public Object getImages() {
                return images;
            }

            public void setImages(Object images) {
                this.images = images;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public Object getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(Object timestamp) {
                this.timestamp = timestamp;
            }

            public String getCustomerName() {
                return customerName;
            }

            public void setCustomerName(String customerName) {
                this.customerName = customerName;
            }

            public String getX() {
                return x;
            }

            public void setX(String x) {
                this.x = x;
            }

            public String getY() {
                return y;
            }

            public void setY(String y) {
                this.y = y;
            }
        }
    }
}
