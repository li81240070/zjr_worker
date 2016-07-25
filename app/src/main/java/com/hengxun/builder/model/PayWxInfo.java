package com.hengxun.builder.model;

/**
 * Created by ZY on 2016/4/13.
 */
public class PayWxInfo {

    /**
     * appid : wxd848789615613a16
     * partnerid : 1325603301
     * prepayid : wx20160413180824eea337ca160962774777
     * package_ : Sign=WXPay
     * noncestr : f4b3880e37e1460eacbe6710a3eb65d9
     * timestamp : 1460542037
     * sign : F23E738F729E7F3B1EB01B939C78FF5E
     * outtradeno : 0ca54aa1d14c44f2a39d1bb0941f375c
     */

    private String appid;
    private String partnerid;
    private String prepayid;
    private String package_;
    private String noncestr;
    private String timestamp;
    private String sign;
    private String outtradeno;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getPackage_() {
        return package_;
    }

    public void setPackage_(String package_) {
        this.package_ = package_;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOuttradeno() {
        return outtradeno;
    }

    public void setOuttradeno(String outtradeno) {
        this.outtradeno = outtradeno;
    }
}
