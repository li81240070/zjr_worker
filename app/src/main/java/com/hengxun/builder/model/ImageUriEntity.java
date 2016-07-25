package com.hengxun.builder.model;


import java.io.Serializable;

/**
 * 添加照片路径
 * Created by ge on 2016/3/7.
 */
public class ImageUriEntity implements Serializable{

    String imageUri;
    String largImgPath;

    public ImageUriEntity() {
        super();
    }

    public String getLargImgPath() {
        return largImgPath;
    }

    public void setLargImgPath(String largImgPath) {
        this.largImgPath = largImgPath;
    }

    public ImageUriEntity(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
