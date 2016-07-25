package com.hengxun.builder.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZY on 2016/4/17.
 */
public class BacklogOrder implements Serializable {

    /**
     * code : 200
     * time : 1461018288120
     * message : null
     * dataMap : {"toPayOrders":[{"model":null,"order_id":"2845","mobile":"13664266902","post_code":"0","address":"辽宁省大连市甘井子区高能街26号","comment":"","service":"水龙头","order_status":"6","order_time":1460995200000,"adjustable":"0","appoint_time":1460995200000,"images":[],"price":"119.00","timestamp":null,"customerName":"哈利波特大","x":"38.86719100","y":"121.52989600"}],"offerEmergencys":[{"model":2,"order_id":"101","mobile":"13664266902","post_code":null,"address":"辽宁省大连市甘井子区高能街26号","comment":"","service":"抢险抢修","order_status":"110","order_time":1461000776000,"adjustable":null,"appoint_time":1461001733000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"},{"model":2,"order_id":"102","mobile":"13664266902","post_code":null,"address":"辽宁省大连市甘井子区高能街26号","comment":"","service":"抢险抢修","order_status":"110","order_time":1461000778000,"adjustable":null,"appoint_time":1461002726000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"},{"model":2,"order_id":"93","mobile":"18698604094","post_code":null,"address":"辽宁省大连市甘井子区火炬路","comment":"","service":"抢险抢修","order_status":"110","order_time":1460997484000,"adjustable":null,"appoint_time":1461002899000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"},{"model":2,"order_id":"116","mobile":"15998434810","post_code":null,"address":"辽宁省大连市甘井子区火炬路15号","comment":"","service":"抢险抢修","order_status":"110","order_time":1461012067000,"adjustable":null,"appoint_time":1461012599000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"},{"model":2,"order_id":"115","mobile":"15998434810","post_code":null,"address":"辽宁省大连市甘井子区火炬路15号","comment":"","service":"抢险抢修","order_status":"110","order_time":1461010709000,"adjustable":null,"appoint_time":1461012781000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"},{"model":2,"order_id":"92","mobile":"18698604094","post_code":null,"address":"辽宁省大连市甘井子区火炬路","comment":"","service":"抢险抢修","order_status":"110","order_time":1460997424000,"adjustable":null,"appoint_time":1461015323000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"}],"todayOrders":8,"offerOrders":[{"model":null,"order_id":"2837","mobile":"15998434810","post_code":"210200","address":"辽宁省大连市甘井子区火炬路15号","comment":"","service":"地板维修","order_status":"22","order_time":1460995200000,"adjustable":"0","appoint_time":1460995200000,"images":[],"price":"358.00","timestamp":null,"customerName":"小张张","x":"38.86712700","y":"121.52968800"}]}
     */

    private int code;
    private long time;
    private Object message;
    /**
     * toPayOrders : [{"model":null,"order_id":"2845","mobile":"13664266902","post_code":"0","address":"辽宁省大连市甘井子区高能街26号","comment":"","service":"水龙头","order_status":"6","order_time":1460995200000,"adjustable":"0","appoint_time":1460995200000,"images":[],"price":"119.00","timestamp":null,"customerName":"哈利波特大","x":"38.86719100","y":"121.52989600"}]
     * offerEmergencys : [{"model":2,"order_id":"101","mobile":"13664266902","post_code":null,"address":"辽宁省大连市甘井子区高能街26号","comment":"","service":"抢险抢修","order_status":"110","order_time":1461000776000,"adjustable":null,"appoint_time":1461001733000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"},{"model":2,"order_id":"102","mobile":"13664266902","post_code":null,"address":"辽宁省大连市甘井子区高能街26号","comment":"","service":"抢险抢修","order_status":"110","order_time":1461000778000,"adjustable":null,"appoint_time":1461002726000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"},{"model":2,"order_id":"93","mobile":"18698604094","post_code":null,"address":"辽宁省大连市甘井子区火炬路","comment":"","service":"抢险抢修","order_status":"110","order_time":1460997484000,"adjustable":null,"appoint_time":1461002899000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"},{"model":2,"order_id":"116","mobile":"15998434810","post_code":null,"address":"辽宁省大连市甘井子区火炬路15号","comment":"","service":"抢险抢修","order_status":"110","order_time":1461012067000,"adjustable":null,"appoint_time":1461012599000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"},{"model":2,"order_id":"115","mobile":"15998434810","post_code":null,"address":"辽宁省大连市甘井子区火炬路15号","comment":"","service":"抢险抢修","order_status":"110","order_time":1461010709000,"adjustable":null,"appoint_time":1461012781000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"},{"model":2,"order_id":"92","mobile":"18698604094","post_code":null,"address":"辽宁省大连市甘井子区火炬路","comment":"","service":"抢险抢修","order_status":"110","order_time":1460997424000,"adjustable":null,"appoint_time":1461015323000,"images":null,"price":"0","timestamp":null,"customerName":"未知客户","x":"0","y":"0"}]
     * todayOrders : 8
     * offerOrders : [{"model":null,"order_id":"2837","mobile":"15998434810","post_code":"210200","address":"辽宁省大连市甘井子区火炬路15号","comment":"","service":"地板维修","order_status":"22","order_time":1460995200000,"adjustable":"0","appoint_time":1460995200000,"images":[],"price":"358.00","timestamp":null,"customerName":"小张张","x":"38.86712700","y":"121.52968800"}]
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
        private int todayOrders;
        /**
         * model : null
         * order_id : 2845
         * mobile : 13664266902
         * post_code : 0
         * address : 辽宁省大连市甘井子区高能街26号
         * comment :
         * service : 水龙头
         * order_status : 6
         * order_time : 1460995200000
         * adjustable : 0
         * appoint_time : 1460995200000
         * images : []
         * price : 119.00
         * timestamp : null
         * customerName : 哈利波特大
         * x : 38.86719100
         * y : 121.52989600
         */

        private List<ToPayOrdersEntity> toPayOrders;
        /**
         * model : 2
         * order_id : 101
         * mobile : 13664266902
         * post_code : null
         * address : 辽宁省大连市甘井子区高能街26号
         * comment :
         * service : 抢险抢修
         * order_status : 110
         * order_time : 1461000776000
         * adjustable : null
         * appoint_time : 1461001733000
         * images : null
         * price : 0
         * timestamp : null
         * customerName : 未知客户
         * x : 0
         * y : 0
         */

        private List<OfferEmergencysEntity> offerEmergencys;
        /**
         * model : null
         * order_id : 2837
         * mobile : 15998434810
         * post_code : 210200
         * address : 辽宁省大连市甘井子区火炬路15号
         * comment :
         * service : 地板维修
         * order_status : 22
         * order_time : 1460995200000
         * adjustable : 0
         * appoint_time : 1460995200000
         * images : []
         * price : 358.00
         * timestamp : null
         * customerName : 小张张
         * x : 38.86712700
         * y : 121.52968800
         */

        private List<OfferOrdersEntity> offerOrders;

        public int getTodayOrders() {
            return todayOrders;
        }

        public void setTodayOrders(int todayOrders) {
            this.todayOrders = todayOrders;
        }

        public List<ToPayOrdersEntity> getToPayOrders() {
            return toPayOrders;
        }

        public void setToPayOrders(List<ToPayOrdersEntity> toPayOrders) {
            this.toPayOrders = toPayOrders;
        }

        public List<OfferEmergencysEntity> getOfferEmergencys() {
            return offerEmergencys;
        }

        public void setOfferEmergencys(List<OfferEmergencysEntity> offerEmergencys) {
            this.offerEmergencys = offerEmergencys;
        }

        public List<OfferOrdersEntity> getOfferOrders() {
            return offerOrders;
        }

        public void setOfferOrders(List<OfferOrdersEntity> offerOrders) {
            this.offerOrders = offerOrders;
        }

        public static class ToPayOrdersEntity implements Serializable {
            private Object model;
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

            public Object getModel() {
                return model;
            }

            public void setModel(Object model) {
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

        public static class OfferEmergencysEntity implements Serializable {
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

        public static class OfferOrdersEntity implements Serializable {
            private Object model;
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

            public Object getModel() {
                return model;
            }

            public void setModel(Object model) {
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
