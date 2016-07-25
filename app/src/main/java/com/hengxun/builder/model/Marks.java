package com.hengxun.builder.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZY on 2016/3/29.
 */
public class Marks implements Serializable {

    /**
     * code : 200
     * time : 1460091847552
     * message : null
     * dataMap : {"points":[{"orderId":382,"service":"水阀","point":49,"finishTime":null},{"orderId":371,"service":"电话、网络","point":118,"finishTime":null},{"orderId":366,"service":"水阀","point":109,"finishTime":null},{"orderId":383,"service":"洁具维修","point":188,"finishTime":null}],"totalPoints":464}
     */

    private int code;
    private long time;
    private Object message;
    /**
     * points : [{"orderId":382,"service":"水阀","point":49,"finishTime":null},{"orderId":371,"service":"电话、网络","point":118,"finishTime":null},{"orderId":366,"service":"水阀","point":109,"finishTime":null},{"orderId":383,"service":"洁具维修","point":188,"finishTime":null}]
     * totalPoints : 464
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
        private int totalPoints;
        /**
         * orderId : 382
         * service : 水阀
         * point : 49
         * finishTime : null
         */

        private List<PointsEntity> points;

        public int getTotalPoints() {
            return totalPoints;
        }

        public void setTotalPoints(int totalPoints) {
            this.totalPoints = totalPoints;
        }

        public List<PointsEntity> getPoints() {
            return points;
        }

        public void setPoints(List<PointsEntity> points) {
            this.points = points;
        }

        public static class PointsEntity {
            private int orderId;
            private String service;
            private int point;
            private long finishTime;

            public int getOrderId() {
                return orderId;
            }

            public void setOrderId(int orderId) {
                this.orderId = orderId;
            }

            public String getService() {
                return service;
            }

            public void setService(String service) {
                this.service = service;
            }

            public int getPoint() {
                return point;
            }

            public void setPoint(int point) {
                this.point = point;
            }

            public long getFinishTime() {
                return finishTime;
            }

            public void setFinishTime(long finishTime) {
                this.finishTime = finishTime;
            }
        }
    }
}
