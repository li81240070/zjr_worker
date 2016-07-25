package com.hengxun.builder.model;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * 本类是将地址转换为经纬度的存储经纬度的集合类
 * Created by ge on 2016/3/29.
 */
public class LangtueEntity implements Serializable{

    private LatLng latLng;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
