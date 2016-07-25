package com.hengxun.builder.common;

/**
 * Created by ZY on 2016/3/23.
 */
public final class AppApi {
    // 正式环境API
    public static final String ROOT_API = "https://123.57.185.198/ZhenjiangrenManagement";

//     TestAPI
//    public static final String ROOT_API = "http://192.168.0.128:8088/ZhenjiangrenManagement";

    // 登录
    public static final String LOGIN = ROOT_API + "/api/v1/worker/login";

    // 匠人获取所有订单列表
    // type 1 自己的订单 2 所有订单
    public static final String ORDERLIST = ROOT_API + "/api/v1/worker/orderlist";

    // 获取热力图位置
    public static final String GETHOT = ROOT_API + "/api/v1/worker/hotmap";

    // 上传匠人位置
    public static final String LOCATE = ROOT_API + "/api/v1/worker/locate";

    // 账户列表
    public static final String INCOMES = ROOT_API + "/api/v1/worker/income";

    // 提现
    public static final String DEPOSIT = ROOT_API + "/api/v1/worker/deposit";

    // 找回密码获取验证码
    public static final String FORGETCODE = ROOT_API + "/api/v1/user/forgetCodeworker";

//    // 服务项目
//    public static final String SERVICE = ROOT_API + "/api/v1/common/services";

//    // 服务流程
//    public static final String FLOW = ROOT_API + "/api/v1/common/flow";

//    // 维修标准
//    public static final String STANDARD = ROOT_API + "/api/v1/common/standard";

//    // 关于我们
//    public static final String ABOUT = ROOT_API + "/api/v1/common/about";

//    // 地区列表
//    public static final String POSTCODES = ROOT_API + "/api/v1/common/postcodes";

    // 积分列表
    public static final String POINTS = ROOT_API + "/api/v1/worker/points";
    
    // 获取提现金额
    public static final String GETAMOUNT = ROOT_API + "/api/v1/worker/getAmount";

    // 获取所有匠人信息位置
    public static final String MASTERS = ROOT_API + "/api/v1/customer/masters";

    // 匠人开工完工状态修改
    public static final String DUTYSTATUS = ROOT_API + "/api/v1/worker/dutystatus";

    // 匠人注销
    public static final String LOGOUT = ROOT_API + "/api/v1/worker/logout";

//    // 匠人忘记密码
    public static final String FORGETPWD = ROOT_API + "/api/v1/worker/forgetPwd";

    // 匠人修改密码
    public static final String CHANGEPWD = ROOT_API + "/api/v1/worker/pwd";

    // 修改订单状态
    public static final String ORDERSTATUS = ROOT_API + "/api/v1/order/status";

    // 订单图片img
    public static final String ORDER_IMG = "https://123.57.185.198";

    // 上传完工图片
    public static final String UPLOADIMAGE = ROOT_API + "/api/v1/worker/uploadImage";

    // 工人接单的订单详情
    public static final String DETAIL = ROOT_API + "/api/v1/common/detail";

    // 自动升级
    public static final String VERSION = ROOT_API + "/api/v1/common/version";

    // 验证定位地质是不是在开放区域内
    public static final String VALIDATE = ROOT_API +"/api/v1/common/validateAddress";

    // 支付完成  type 1 微信  2 支付宝
    public static final String PAY = ROOT_API + "/api/v1/common/pay";

    // 准备支付前  支付的参数信息
    public static final String TEMP = ROOT_API + "/api/v1/payment/temp";

//    public static final String TEMP = "http://123.57.185.198:8080/ZhenjiangrenManagement/api/v1/payment/temp";

    // 获取服务器支付状态信息
    public static final String ORDERPAYSTATUS = ROOT_API + "/api/v1/payment/orderPayStatus";

    // 删除错误支付
    public static final String PAYSTATUS = ROOT_API + "/api/v1/payment/payStatus";

    // 极光注册 registrationId
    public static final String REGISTIONID = ROOT_API + "/api/v1/msg/registration";

    // 未付款 派单 get post
    public static final String BACKLOG = ROOT_API + "/api/v1/worker/backlog";

    // 获取抢险抢修订单
    public static final String EMERGENCY = ROOT_API + "/api/v1/order/emergency";
}
