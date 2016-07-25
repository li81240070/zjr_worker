package com.hengxun.builder.model;

import java.util.List;

/**
 * Created by ZY on 2016/4/2.
 * 账户列表
 */
public class PersonalAccount {

    /**
     * code : 200
     * time : 1459562265434
     * message : null
     * dataMap : {"incomes":[{"update_id":0,"account_type":1,"amount":500,"create_time":1459501329000,"worker_account_id":1,"content":"sd","point":50,"worker_id":1,"update_time":1459506988000,"create_id":0,"order_id":56,"draw_amount":500,"status":1},{"update_id":0,"account_type":1,"amount":500,"create_time":1459499806000,"worker_account_id":6,"content":"sd","point":50,"worker_id":1,"update_time":1459506989000,"create_id":0,"order_id":56,"draw_amount":500,"status":1},{"update_id":0,"account_type":2,"amount":500,"create_time":1459499806000,"worker_account_id":7,"content":"sd","point":50,"worker_id":1,"update_time":1459507013000,"create_id":0,"order_id":56,"draw_amount":500,"status":1},{"update_id":0,"account_type":2,"amount":500,"create_time":1459499806000,"worker_account_id":8,"content":"sd","point":50,"worker_id":1,"update_time":1459328005000,"create_id":0,"order_id":56,"draw_amount":500,"status":1}]}
     */

    private int code;
    private long time;
    private Object message;
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
        /**
         * update_id : 0
         * account_type : 1
         * amount : 500.0
         * create_time : 1459501329000
         * worker_account_id : 1
         * content : sd
         * point : 50
         * worker_id : 1
         * update_time : 1459506988000
         * create_id : 0
         * order_id : 56
         * draw_amount : 500.0
         * status : 1
         */

        private List<IncomesEntity> incomes;

        public List<IncomesEntity> getIncomes() {
            return incomes;
        }

        public void setIncomes(List<IncomesEntity> incomes) {
            this.incomes = incomes;
        }

        public static class IncomesEntity {
            private int update_id;
            private int account_type;
            private double amount;
            private long create_time;
            private int worker_account_id;
            private String content;
            private int point;
            private int worker_id;
            private long update_time;
            private int create_id;
            private int order_id;
            private double draw_amount;
            private int status;

            public int getUpdate_id() {
                return update_id;
            }

            public void setUpdate_id(int update_id) {
                this.update_id = update_id;
            }

            public int getAccount_type() {
                return account_type;
            }

            public void setAccount_type(int account_type) {
                this.account_type = account_type;
            }

            public double getAmount() {
                return amount;
            }

            public void setAmount(double amount) {
                this.amount = amount;
            }

            public long getCreate_time() {
                return create_time;
            }

            public void setCreate_time(long create_time) {
                this.create_time = create_time;
            }

            public int getWorker_account_id() {
                return worker_account_id;
            }

            public void setWorker_account_id(int worker_account_id) {
                this.worker_account_id = worker_account_id;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getPoint() {
                return point;
            }

            public void setPoint(int point) {
                this.point = point;
            }

            public int getWorker_id() {
                return worker_id;
            }

            public void setWorker_id(int worker_id) {
                this.worker_id = worker_id;
            }

            public long getUpdate_time() {
                return update_time;
            }

            public void setUpdate_time(long update_time) {
                this.update_time = update_time;
            }

            public int getCreate_id() {
                return create_id;
            }

            public void setCreate_id(int create_id) {
                this.create_id = create_id;
            }

            public int getOrder_id() {
                return order_id;
            }

            public void setOrder_id(int order_id) {
                this.order_id = order_id;
            }

            public double getDraw_amount() {
                return draw_amount;
            }

            public void setDraw_amount(double draw_amount) {
                this.draw_amount = draw_amount;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }
    }
}
