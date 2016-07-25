package com.hengxun.builder.model;

/**
 * Created by ZY on 2016/4/13.
 * 支付宝
 */
public class PayAliInfo {

    /**
     * partner : 2088221352629802
     * sellerid : 2088221352629802
     * outtradeno : 2982ae9f77bc4fc38ed2222cce939b4c
     * subject : 真匠人-服务缴费
     * body : 真匠人-服务缴费
     * totalfee : 0.01
     * notifyurl : http://123.57.185.198:8080/ZhenjiangrenManagement/alipay/notify
     * service : mobile.securitypay.pay
     * paymenttype : 1
     * inputcharset : utf-8
     * itbpay : 30m
     * showurl : m.alipay.com
     * sign : olazx7dl4SCOZhjxDu7KCtURmmlBj9Npm2lYRSH9QKfEJeEsNkhvZnAUG8zuSyXBPa3wycOEKh6UCZa4Ej8clchQR/662BZmYW8+6Wsvxb/J3NesXrVplBwvDJtBKF9XDYnnKLquoKaZ4RcjtgmPMBFB7jLtbOtjkB5jqTaV2YM=
     * signType : RSA
     */

    private String partner;
    private String sellerid;
    private String outtradeno;
    private String subject;
    private String body;
    private double totalfee;
    private String notifyurl;
    private String service;
    private int paymenttype;
    private String inputcharset;
    private String itbpay;
    private String showurl;
    private String sign;
    private String signType;

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSellerid() {
        return sellerid;
    }

    public void setSellerid(String sellerid) {
        this.sellerid = sellerid;
    }

    public String getOuttradeno() {
        return outtradeno;
    }

    public void setOuttradeno(String outtradeno) {
        this.outtradeno = outtradeno;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public double getTotalfee() {
        return totalfee;
    }

    public void setTotalfee(double totalfee) {
        this.totalfee = totalfee;
    }

    public String getNotifyurl() {
        return notifyurl;
    }

    public void setNotifyurl(String notifyurl) {
        this.notifyurl = notifyurl;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getPaymenttype() {
        return paymenttype;
    }

    public void setPaymenttype(int paymenttype) {
        this.paymenttype = paymenttype;
    }

    public String getInputcharset() {
        return inputcharset;
    }

    public void setInputcharset(String inputcharset) {
        this.inputcharset = inputcharset;
    }

    public String getItbpay() {
        return itbpay;
    }

    public void setItbpay(String itbpay) {
        this.itbpay = itbpay;
    }

    public String getShowurl() {
        return showurl;
    }

    public void setShowurl(String showurl) {
        this.showurl = showurl;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }
}
