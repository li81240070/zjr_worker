package com.hengxun.builder.model;

import java.util.List;

/**
 * Created by ZY on 2016/3/29.
 * 匠人积分列表
 */
public class OrderMarks {

    /**
     * code : 200
     * time : 1459246522538
     * message : null
     * dataMap : {"totalPoints":0,"points":[{"orderId":1,"service":"水安装维修","point":7,"finishTime":2016}]}
     */

    private int code;
    private long time;
    private Object message;
    /**
     * totalPoints : 0
     * points : [{"orderId":1,"service":"水安装维修","point":7,"finishTime":2016}]
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
         * orderId : 1
         * service : 水安装维修
         * point : 7
         * finishTime : 2016
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
            private int finishTime;

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

            public int getFinishTime() {
                return finishTime;
            }

            public void setFinishTime(int finishTime) {
                this.finishTime = finishTime;
            }
        }
    }
}
