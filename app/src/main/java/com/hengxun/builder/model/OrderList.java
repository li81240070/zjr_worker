package com.hengxun.builder.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZY on 2016/3/29.
 */
public class OrderList implements Serializable {

    /**
     * code : 200
     * time : 1459868996733
     * message : null
     * dataMap : {"order_num":2,"orders":[{"order_id":"290","mobile":"13664266902","post_code":"210202","address":" 辽宁省 大连市 中山区Sdad","comment":"Datadsads","service":"水龙头","order_status":"1","order_time":1459864349000,"adjustable":"0","appoint_time":1459864800000,"images":[],"price":"119.00","timestamp":null,"customerName":"super","x":"38.90043600","y":"121.67796600"},{"order_id":"288","mobile":"13664266902","post_code":"210321","address":"还好吧","comment":"哈哈哈","service":"器具安装","order_status":"1","order_time":1459862369000,"adjustable":"1","appoint_time":1459862400000,"images":["/resource/ORDER/2016/4/5/314/a1aedd038db75b64"],"price":"0.00","timestamp":null,"customerName":"super","x":"41.34709850","y":"122.44367625"}]}
     */

    private int code;
    private long time;
    private Object message;
    /**
     * order_num : 2
     * orders : [{"order_id":"290","mobile":"13664266902","post_code":"210202","address":" 辽宁省 大连市 中山区Sdad","comment":"Datadsads","service":"水龙头","order_status":"1","order_time":1459864349000,"adjustable":"0","appoint_time":1459864800000,"images":[],"price":"119.00","timestamp":null,"customerName":"super","x":"38.90043600","y":"121.67796600"},{"order_id":"288","mobile":"13664266902","post_code":"210321","address":"还好吧","comment":"哈哈哈","service":"器具安装","order_status":"1","order_time":1459862369000,"adjustable":"1","appoint_time":1459862400000,"images":["/resource/ORDER/2016/4/5/314/a1aedd038db75b64"],"price":"0.00","timestamp":null,"customerName":"super","x":"41.34709850","y":"122.44367625"}]
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

    public static class DataMapEntity implements Serializable {
        private int order_num;
        /**
         * order_id : 290
         * mobile : 13664266902
         * post_code : 210202
         * address :  辽宁省 大连市 中山区Sdad
         * comment : Datadsads
         * service : 水龙头
         * order_status : 1
         * order_time : 1459864349000
         * adjustable : 0
         * appoint_time : 1459864800000
         * images : []
         * price : 119.00
         * timestamp : null
         * customerName : super
         * x : 38.90043600
         * y : 121.67796600
         */

        private List<OrdersEntity> orders;

        public int getOrder_num() {
            return order_num;
        }

        public void setOrder_num(int order_num) {
            this.order_num = order_num;
        }

        public List<OrdersEntity> getOrders() {
            return orders;
        }

        public void setOrders(List<OrdersEntity> orders) {
            this.orders = orders;
        }

        public static class OrdersEntity implements Serializable {
            private String order_id;
            private String mobile;
            private String post_code;
            private String address;
            private String comment;
            private String service;
            private String order_status;
            private long order_time;
            private String adjustable;
            private long appoint_time;
            private String price;
            private Object timestamp;
            private String customerName;
            private String x;
            private String y;
            private List<String> images;
            private int model;

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

            public String getPost_code() {
                return post_code;
            }

            public void setPost_code(String post_code) {
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

            public int getModel() {
                return model;
            }

            public void setModel(int model) {
                this.model = model;
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

            public String getAdjustable() {
                return adjustable;
            }

            public void setAdjustable(String adjustable) {
                this.adjustable = adjustable;
            }

            public long getAppoint_time() {
                return appoint_time;
            }

            public void setAppoint_time(long appoint_time) {
                this.appoint_time = appoint_time;
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

            public List<String> getImages() {
                return images;
            }

            public void setImages(List<String> images) {
                this.images = images;
            }
        }
    }
}
