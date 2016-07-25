package com.hengxun.builder.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZY on 2016/4/5.
 */
public class OrderPush implements Serializable {

    /**
     * code : 200
     * time : 1459799930342
     * message : null
     * dataMap : {"income_total":0,"service_id":1057,"vip_level":0,"order_id":190,"adjustable":0,"constomer_create_id":52,"constomer_update_time":1459672752000,"nick_name":"haha","server_comment":"","appoint_time":1459773600000,"constomer_status":1,"constomer_gender":0,"create_id":0,"order_time":1459773224000,"pay_type":0,"constomer_post_code":0,"income_worker":0,"server_id":0,"customer_comment":"基地物色物色","custom_sign":"进行计算机","alias":"20344999a28d493a8a70eb54e1457ca6","status":1,"post_code":210202,"order_comment":"","star":0,"avatar":"/2016/4/5/345/046e2bf8caacb6c4","total_price":2690,"order_status":1,"update_id":0,"constomer_update_id":0,"constomer_create_time":1459690817000,"update_time":1459773224000,"address":"鸡儿男朋友","service":{"group_name":"暖气管检测","group_code":1057,"children":[{"service_code":1058,"num":10,"service_name":"暖气、地热管道打压检测","unit_price":0}]},"images":["/resource/ORDER/2016/4/4/673/da5c85601298d485"],"worker_id":0,"y":121.67796063,"customer_id":52,"mobile":"13664266902","order_type":1001,"x":38.90043355}
     */

    private int code;
    private long time;
    private Object message;
    private int notifactionId;
    /**
     * income_total : 0.0
     * service_id : 1057
     * vip_level : 0
     * order_id : 190
     * adjustable : 0
     * constomer_create_id : 52
     * constomer_update_time : 1459672752000
     * nick_name : haha
     * server_comment :
     * appoint_time : 1459773600000
     * constomer_status : 1
     * constomer_gender : 0
     * create_id : 0
     * order_time : 1459773224000
     * pay_type : 0
     * constomer_post_code : 0
     * income_worker : 0.0
     * server_id : 0
     * customer_comment : 基地物色物色
     * custom_sign : 进行计算机
     * alias : 20344999a28d493a8a70eb54e1457ca6
     * status : 1
     * post_code : 210202
     * order_comment :
     * star : 0
     * avatar : /2016/4/5/345/046e2bf8caacb6c4
     * total_price : 2690.0
     * order_status : 1
     * update_id : 0
     * constomer_update_id : 0
     * constomer_create_time : 1459690817000
     * update_time : 1459773224000
     * address : 鸡儿男朋友
     * service : {"group_name":"暖气管检测","group_code":1057,"children":[{"service_code":1058,"num":10,"service_name":"暖气、地热管道打压检测","unit_price":0}]}
     * images : ["/resource/ORDER/2016/4/4/673/da5c85601298d485"]
     * worker_id : 0
     * "customerName":"super",
     * y : 121.67796063
     * customer_id : 52
     * mobile : 13664266902
     * order_type : 1001
     * x : 38.90043355
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
        private double income_total;
        private int service_id;
        private int vip_level;
        private int order_id;
        private int adjustable;
        private int constomer_create_id;
        private long constomer_update_time;
        private String nick_name;
        private String server_comment;
        private long appoint_time;
        private int constomer_status;
        private int constomer_gender;
        private int create_id;
        private long order_time;
        private int pay_type;
        private int constomer_post_code;
        private double income_worker;
        private int server_id;
        private String customer_comment;
        private String custom_sign;
        private String alias;
        private int status;
        private int post_code;
        private String order_comment;
        private int star;
        private String avatar;
        private double total_price;
        private int order_status;
        private int update_id;
        private int constomer_update_id;
        private long constomer_create_time;
        private long update_time;
        private String address;
        private String customerName;

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        /**
         * group_name : 暖气管检测
         * group_code : 1057
         * children : [{"service_code":1058,"num":10,"service_name":"暖气、地热管道打压检测","unit_price":0}]
         */

        private ServiceEntity service;
        private int worker_id;
        private double y;
        private double order_x;
        private double order_y;
        private int customer_id;
        private String mobile;
        private int order_type;
        private double x;
        private List<String> images;

        public double getOrder_x() {
            return order_x;
        }

        public void setOrder_x(double order_x) {
            this.order_x = order_x;
        }

        public double getOrder_y() {
            return order_y;
        }

        public void setOrder_y(double order_y) {
            this.order_y = order_y;
        }

        public double getIncome_total() {
            return income_total;
        }

        public void setIncome_total(double income_total) {
            this.income_total = income_total;
        }

        public int getService_id() {
            return service_id;
        }

        public void setService_id(int service_id) {
            this.service_id = service_id;
        }

        public int getVip_level() {
            return vip_level;
        }

        public void setVip_level(int vip_level) {
            this.vip_level = vip_level;
        }

        public int getOrder_id() {
            return order_id;
        }

        public void setOrder_id(int order_id) {
            this.order_id = order_id;
        }

        public int getAdjustable() {
            return adjustable;
        }

        public void setAdjustable(int adjustable) {
            this.adjustable = adjustable;
        }

        public int getConstomer_create_id() {
            return constomer_create_id;
        }

        public void setConstomer_create_id(int constomer_create_id) {
            this.constomer_create_id = constomer_create_id;
        }

        public long getConstomer_update_time() {
            return constomer_update_time;
        }

        public void setConstomer_update_time(long constomer_update_time) {
            this.constomer_update_time = constomer_update_time;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getServer_comment() {
            return server_comment;
        }

        public void setServer_comment(String server_comment) {
            this.server_comment = server_comment;
        }

        public long getAppoint_time() {
            return appoint_time;
        }

        public void setAppoint_time(long appoint_time) {
            this.appoint_time = appoint_time;
        }

        public int getConstomer_status() {
            return constomer_status;
        }

        public void setConstomer_status(int constomer_status) {
            this.constomer_status = constomer_status;
        }

        public int getConstomer_gender() {
            return constomer_gender;
        }

        public void setConstomer_gender(int constomer_gender) {
            this.constomer_gender = constomer_gender;
        }

        public int getCreate_id() {
            return create_id;
        }

        public void setCreate_id(int create_id) {
            this.create_id = create_id;
        }

        public long getOrder_time() {
            return order_time;
        }

        public void setOrder_time(long order_time) {
            this.order_time = order_time;
        }

        public int getPay_type() {
            return pay_type;
        }

        public void setPay_type(int pay_type) {
            this.pay_type = pay_type;
        }

        public int getConstomer_post_code() {
            return constomer_post_code;
        }

        public void setConstomer_post_code(int constomer_post_code) {
            this.constomer_post_code = constomer_post_code;
        }

        public double getIncome_worker() {
            return income_worker;
        }

        public void setIncome_worker(double income_worker) {
            this.income_worker = income_worker;
        }

        public int getServer_id() {
            return server_id;
        }

        public void setServer_id(int server_id) {
            this.server_id = server_id;
        }

        public String getCustomer_comment() {
            return customer_comment;
        }

        public void setCustomer_comment(String customer_comment) {
            this.customer_comment = customer_comment;
        }

        public String getCustom_sign() {
            return custom_sign;
        }

        public void setCustom_sign(String custom_sign) {
            this.custom_sign = custom_sign;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getPost_code() {
            return post_code;
        }

        public void setPost_code(int post_code) {
            this.post_code = post_code;
        }

        public String getOrder_comment() {
            return order_comment;
        }

        public void setOrder_comment(String order_comment) {
            this.order_comment = order_comment;
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

        public double getTotal_price() {
            return total_price;
        }

        public void setTotal_price(double total_price) {
            this.total_price = total_price;
        }

        public int getOrder_status() {
            return order_status;
        }

        public void setOrder_status(int order_status) {
            this.order_status = order_status;
        }

        public int getUpdate_id() {
            return update_id;
        }

        public void setUpdate_id(int update_id) {
            this.update_id = update_id;
        }

        public int getConstomer_update_id() {
            return constomer_update_id;
        }

        public void setConstomer_update_id(int constomer_update_id) {
            this.constomer_update_id = constomer_update_id;
        }

        public long getConstomer_create_time() {
            return constomer_create_time;
        }

        public void setConstomer_create_time(long constomer_create_time) {
            this.constomer_create_time = constomer_create_time;
        }

        public long getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(long update_time) {
            this.update_time = update_time;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public ServiceEntity getService() {
            return service;
        }

        public void setService(ServiceEntity service) {
            this.service = service;
        }

        public int getWorker_id() {
            return worker_id;
        }

        public void setWorker_id(int worker_id) {
            this.worker_id = worker_id;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public int getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(int customer_id) {
            this.customer_id = customer_id;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getOrder_type() {
            return order_type;
        }

        public void setOrder_type(int order_type) {
            this.order_type = order_type;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public static class ServiceEntity implements Serializable {
            private String group_name;
            private int group_code;
            /**
             * service_code : 1058
             * num : 10
             * service_name : 暖气、地热管道打压检测
             * unit_price : 0.0
             */

            private List<ChildrenEntity> children;

            public String getGroup_name() {
                return group_name;
            }

            public void setGroup_name(String group_name) {
                this.group_name = group_name;
            }

            public int getGroup_code() {
                return group_code;
            }

            public void setGroup_code(int group_code) {
                this.group_code = group_code;
            }

            public List<ChildrenEntity> getChildren() {
                return children;
            }

            public void setChildren(List<ChildrenEntity> children) {
                this.children = children;
            }

            public static class ChildrenEntity implements Serializable {
                private int service_code;
                private int num;
                private String service_name;
                private double unit_price;

                public int getService_code() {
                    return service_code;
                }

                public void setService_code(int service_code) {
                    this.service_code = service_code;
                }

                public int getNum() {
                    return num;
                }

                public void setNum(int num) {
                    this.num = num;
                }

                public String getService_name() {
                    return service_name;
                }

                public void setService_name(String service_name) {
                    this.service_name = service_name;
                }

                public double getUnit_price() {
                    return unit_price;
                }

                public void setUnit_price(double unit_price) {
                    this.unit_price = unit_price;
                }
            }
        }
    }
}
