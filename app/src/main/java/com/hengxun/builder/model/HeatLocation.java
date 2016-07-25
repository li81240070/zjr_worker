package com.hengxun.builder.model;

import java.util.List;

/**
 * Created by ZY on 2016/3/29.
 * 热力图分布
 */
public class HeatLocation {

    /**
     * code : 200
     * time : 1460903842133
     * message : null
     * dataMap : {"hotData":[{"count":47,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.961727,"latitude":121.52937,"heatmap_id":1},{"count":23,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.962627,"latitude":121.530815,"heatmap_id":2},{"count":49,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.958363,"latitude":121.53426,"heatmap_id":3},{"count":69,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.957687,"latitude":121.5288,"heatmap_id":4},{"count":4,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.964195,"latitude":121.522766,"heatmap_id":5},{"count":57,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.957462,"latitude":121.525635,"heatmap_id":6},{"count":28,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.958588,"latitude":121.517586,"heatmap_id":7},{"count":81,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.965767,"latitude":121.52046,"heatmap_id":8},{"count":20,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.961727,"latitude":121.517586,"heatmap_id":9},{"count":47,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.954098,"latitude":121.53196,"heatmap_id":10},{"count":5,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.95926,"latitude":121.53168,"heatmap_id":11},{"count":19,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.958588,"latitude":121.52305,"heatmap_id":12}],"hotnum":12}
     */

    private int code;
    private long time;
    private Object message;
    /**
     * hotData : [{"count":47,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.961727,"latitude":121.52937,"heatmap_id":1},{"count":23,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.962627,"latitude":121.530815,"heatmap_id":2},{"count":49,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.958363,"latitude":121.53426,"heatmap_id":3},{"count":69,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.957687,"latitude":121.5288,"heatmap_id":4},{"count":4,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.964195,"latitude":121.522766,"heatmap_id":5},{"count":57,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.957462,"latitude":121.525635,"heatmap_id":6},{"count":28,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.958588,"latitude":121.517586,"heatmap_id":7},{"count":81,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.965767,"latitude":121.52046,"heatmap_id":8},{"count":20,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.961727,"latitude":121.517586,"heatmap_id":9},{"count":47,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.954098,"latitude":121.53196,"heatmap_id":10},{"count":5,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.95926,"latitude":121.53168,"heatmap_id":11},{"count":19,"create_id":4,"status":1,"create_time":1460899169000,"longtitude":38.958588,"latitude":121.52305,"heatmap_id":12}]
     * hotnum : 12
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
        private int hotnum;
        /**
         * count : 47
         * create_id : 4
         * status : 1
         * create_time : 1460899169000
         * longtitude : 38.961727
         * latitude : 121.52937
         * heatmap_id : 1
         */

        private List<HotDataEntity> hotData;

        public int getHotnum() {
            return hotnum;
        }

        public void setHotnum(int hotnum) {
            this.hotnum = hotnum;
        }

        public List<HotDataEntity> getHotData() {
            return hotData;
        }

        public void setHotData(List<HotDataEntity> hotData) {
            this.hotData = hotData;
        }

        public static class HotDataEntity {
            private int count;
            private int create_id;
            private int status;
            private long create_time;
            private double longtitude;
            private double latitude;
            private int heatmap_id;

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public int getCreate_id() {
                return create_id;
            }

            public void setCreate_id(int create_id) {
                this.create_id = create_id;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public long getCreate_time() {
                return create_time;
            }

            public void setCreate_time(long create_time) {
                this.create_time = create_time;
            }

            public double getLongtitude() {
                return longtitude;
            }

            public void setLongtitude(double longtitude) {
                this.longtitude = longtitude;
            }

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public int getHeatmap_id() {
                return heatmap_id;
            }

            public void setHeatmap_id(int heatmap_id) {
                this.heatmap_id = heatmap_id;
            }
        }
    }
}
