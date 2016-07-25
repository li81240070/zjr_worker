package com.hengxun.builder.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZY on 2016/3/29.
 * 服务项目列表
 */
public class ServicesEntity implements Serializable {
    /**
     * code : 0
     * time : 0
     * message : null
     * dataMap : {"services":[{"code":1003,"name":"安装/维修（普通）","minPrice":null,"maxPrice":null,"unit":null,"adjustable":null,"standard":null,"description":null,"sub":null},{"code":1005,"name":"感应水龙头安装/维修","minPrice":null,"maxPrice":null,"unit":null,"adjustable":null,"standard":null,"description":null,"sub":null},{"code":1004,"name":"安装/维修（混水）","minPrice":null,"maxPrice":null,"unit":null,"adjustable":null,"standard":null,"description":null,"sub":null},{"code":1007,"name":"即热式水龙头安装/维修","minPrice":null,"maxPrice":null,"unit":null,"adjustable":null,"standard":null,"description":null,"sub":null},{"code":1006,"name":"洗衣机龙头安装/维修","minPrice":null,"maxPrice":null,"unit":null,"adjustable":null,"standard":null,"description":null,"sub":null}]}
     */

    private int code;
    private int time;
    private Object message;
    private DataMapEntity dataMap;

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

    public DataMapEntity getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMapEntity dataMap) {
        this.dataMap = dataMap;
    }

    public static class DataMapEntity implements Serializable {
        /**
         * code : 1003
         * name : 安装/维修（普通）
         * minPrice : null
         * maxPrice : null
         * unit : null
         * adjustable : null
         * standard : null
         * description : null
         * sub : null
         */

        private List<ServicesEntity> services;

        public List<ServicesEntity> getServices() {
            return services;
        }

        public void setServices(List<ServicesEntity> services) {
            this.services = services;
        }

        public static class SubServicesEntity implements Serializable{
            private int code;
            private String name;
            private Object minPrice;
            private Object maxPrice;
            private Object unit;
            private Object adjustable;
            private Object standard;
            private Object description;
            private Object sub;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Object getMinPrice() {
                return minPrice;
            }

            public void setMinPrice(Object minPrice) {
                this.minPrice = minPrice;
            }

            public Object getMaxPrice() {
                return maxPrice;
            }

            public void setMaxPrice(Object maxPrice) {
                this.maxPrice = maxPrice;
            }

            public Object getUnit() {
                return unit;
            }

            public void setUnit(Object unit) {
                this.unit = unit;
            }

            public Object getAdjustable() {
                return adjustable;
            }

            public void setAdjustable(Object adjustable) {
                this.adjustable = adjustable;
            }

            public Object getStandard() {
                return standard;
            }

            public void setStandard(Object standard) {
                this.standard = standard;
            }

            public Object getDescription() {
                return description;
            }

            public void setDescription(Object description) {
                this.description = description;
            }

            public Object getSub() {
                return sub;
            }

            public void setSub(Object sub) {
                this.sub = sub;
            }
        }
    }
}
