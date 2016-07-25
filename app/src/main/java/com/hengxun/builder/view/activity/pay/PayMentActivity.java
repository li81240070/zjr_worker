package com.hengxun.builder.view.activity.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.model.OrderList;
import com.hengxun.builder.model.OrderPush;
import com.hengxun.builder.utils.TwoTuple;
import com.hengxun.builder.utils.okhttp.HttpsController;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.payutils.payutils.ali.PayResult;
import com.hengxun.builder.utils.payutils.payutils.ali.SignUtils;
import com.hengxun.builder.utils.payutils.payutils.wx.MD5;
import com.hengxun.builder.utils.widget.DateUtil;
import com.hengxun.builder.utils.widget.MD5Util;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by ZY on 2016/3/11.
 * 支付页面的备份
 */
public class PayMentActivity extends BaseActivity implements View.OnClickListener {
    public static PayMentActivity finishActivity;

    private TextView pay_wx_Tv;  // 微信支付
    private TextView pay_zfb_Tv; // 支付宝支付
    private int orderId;         // 订单id
    private OrderPush orderPush; // 极光推送订单
    private OrderList.DataMapEntity.OrdersEntity entity; // 订单列表订单
    private int type;         // 跳转过来的类型 1 正常支付 2 未付款支付
    private boolean payComplete; // 微信付款是否完成
    private int payType;         // 支付方式 1 微信 2 支付宝

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        finishActivity = this;
        EventBus.getDefault().register(this);
        showToolBar(getResources().getString(R.string.pay_money), true, this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        pay_wx_Tv = (TextView) findViewById(R.id.pay_wx_Tv);
        pay_zfb_Tv = (TextView) findViewById(R.id.pay_al_Tv);
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getIntExtra("type", 1);
        if (type == 2) { // 如果是代支付状态
            Toast.makeText(this, "请立刻完成支付", Toast.LENGTH_LONG).show();
        }
        entity = (OrderList.DataMapEntity.OrdersEntity) getIntent().getSerializableExtra("order");

        if (entity == null) {
            orderPush = (OrderPush) getIntent().getSerializableExtra("orderPush");
        }

        // 如果是直接选择的代付状态  弹出提示框
        if (type == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("用户选择现金支付");
            builder.setMessage("用户选择了现金支付, 请立即付款");
            builder.setIcon(R.mipmap.applogo);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }
        pay_wx_Tv.setOnClickListener(this);
        pay_zfb_Tv.setOnClickListener(this);
    }

    @Override
    protected void showToolBar(String titleName, boolean isShow, Activity activity) {
        super.showToolBar(titleName, isShow, activity);
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.pay_tb));
        TextView toolbarTitle_Tv = (TextView) activity.findViewById(R.id.toolbarTitle_Tv);
        toolbarTitle_Tv.setTextColor(getResources().getColor(R.color.white));
        ImageView toolbar_left_Ib = (ImageView) activity.findViewById(R.id.toolbar_left_Ib);
        toolbar_left_Ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar_left_Ib.setImageResource(R.mipmap.white_narrow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_wx_Tv:
                payType = 1;
                Thread payThread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        payByWechat();
                    }
                };
                payThread.start();
                break;
            case R.id.pay_al_Tv:
                payType = 2;
                //支付宝
                pay(v);
                break;

        }
    }

//============================================================================================================//
//==============================================================微信支付start=================================//
//============================================================================================================//


    /**
     * TODO  待测试
     * 获取ip
     */
    private String getIP() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }

    private String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }


    //APPID
    private static final String APP_ID = "wxd848789615613a16";

    // 微信支付商户号
    private static final String MCH_ID = "1325603301";

    // API密钥，在商户平台设置
    private static final String API_KEY = "zhenjiangren20160410ZHENJIANGREN";

    //统一下单接口
    private static final String ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";


    /**
     * 向微信后台申请下单  统一下单
     */

//    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/xml; charset=utf-8");
    private static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
//    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");

    // 调用服务器接口获取相关数据后发起“统一下单”微信接口
    private void payByWechat() {
        // 构建产品参数, 结果为XML字符串
        String xmlParam = null;
        try {
            xmlParam = buildProductArgs();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 调用“统一下单”接口获取预支付ID

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, xmlParam);
        OkHttpClient okHttpClient = null;
        try {
            okHttpClient = HttpsController.setCertificates(PayMentActivity.this, PayMentActivity.this.getAssets().open("zhenjren.cer"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder().url(ORDER_URL).post(requestBody).build();
        Response response;

        //resultMap 中含有第二部所需的prepayId
        Map<String, String> resultMap = null;

        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {

                byte[] buf = response.body().bytes();

                String result = new String(buf);

                resultMap = decodeXml(result);

                Log.e("11111111111", resultMap + "");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String prepayId = null;

        prepayId = resultMap.get("prepay_id");
        Log.e("--------------->", "----prepayId---->" + prepayId);


        // 调用支付接口

        IWXAPI api = WXAPIFactory.createWXAPI(this, APP_ID, false);

        api.sendReq(buildWechatPayReq(prepayId));
//        onResp();
    }

//    public void onResp(BaseResp resp) {
//        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
////            Log.d(TAG,"onPayFinish,errCode="+resp.errCode);
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.app_tip);
//        }
//    }

    @Override
    public void onBackPressed() {
        if (payComplete) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "请先付款, 以免影响您正常使用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 构建产品参数, 结果为符合微信“统一下单”接口的XML串
     *
     * @return XML结构字符串
     */
    private String buildProductArgs() throws UnsupportedEncodingException {
        // 构建基本的参数列表  参数名ASCII码从小到大排序（字典序）；
        List<TwoTuple<String, String>> paramList = new ArrayList<>();
        //应用id 必填
        paramList.add(new TwoTuple<>("appid", APP_ID));
        //商品描述 必填
//        paramList.add(new TwoTuple<>("body", orderPush.getDataMap().getServer_comment()));
        if (orderPush != null) {
            paramList.add(new TwoTuple<>("body", orderPush.getDataMap().getService() != null
                    ? orderPush.getDataMap().getOrder_comment() : "测试商品"));
//            paramList.add(new TwoTuple<>("body", "测试商品"));
        } else {
//            paramList.add(new TwoTuple<>("body", "测试商品"));
            paramList.add(new TwoTuple<>("body", entity.getService() != null ? entity.getService() : "测试商品"));
        }
        //商户号 必填
        paramList.add(new TwoTuple<>("mch_id", MCH_ID));
        //随机字符串 必填
        paramList.add(new TwoTuple<>("nonce_str", UUID.randomUUID().toString().replace("-", "")));
        //通知地址 必填
        paramList.add(new TwoTuple<>("notify_url", "http://www.baidu.com"));

        //商品订单号 必填
        if (orderPush != null) {
//            paramList.add(new TwoTuple<>("out_trade_no", String.valueOf(orderPush.getDataMap().getOrder_id())));
            String str = DateUtil.getSystemDate() + orderPush.getDataMap().getOrder_id();
            Log.d("PayActivity", str);
            paramList.add(new TwoTuple<>("out_trade_no", str));
        } else {
//            paramList.add(new TwoTuple<>("out_trade_no", String.valueOf(entity.getOrder_id())));
            String str = DateUtil.getSystemDate() + entity.getOrder_id();
            paramList.add(new TwoTuple<>("out_trade_no", str));
        }
//        paramList.add(new TwoTuple<>("out_trade_no", "test2"));

        //手机实际ip 必填
        paramList.add(new TwoTuple<>("spbill_create_ip", getIP()));
        Log.d("getIP", getIP());
        // 总金额 必填
//        paramList.add(new TwoTuple<>("total_fee", String.valueOf(orderPush.getDataMap().getTotal_price())));
        paramList.add(new TwoTuple<>("total_fee", "1"));
        // 交易类型 必填
        paramList.add(new TwoTuple<>("trade_type", "APP"));

        // 获取MD5签名并追加到参数列表中
        String sign = generateWechatMD5Signature(paramList);
        //签名
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        paramList.add(new TwoTuple<>("sign", sign));

        // 构建XML参数
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<xml>");
        for (TwoTuple<String, String> paramTuple : paramList) {
            xmlBuilder.append("<").append(paramTuple.first).append(">");
            xmlBuilder.append(paramTuple.second);
            xmlBuilder.append("</").append(paramTuple.first).append(">");
        }
        xmlBuilder.append("</xml>");

        return xmlBuilder.toString();
    }

    /**
     * 构建微信吊起支付接口的参数
     *
     * @param preOrderId
     * @return
     */
    private PayReq buildWechatPayReq(String preOrderId) {
        PayReq payReq = new PayReq();
        payReq.appId = APP_ID;
        payReq.partnerId = MCH_ID;
        payReq.prepayId = preOrderId;
//        payReq.prepayId = "wx201604041848019226911b660061085158";
        payReq.packageValue = "Sign=WXPay";
        payReq.nonceStr = UUID.randomUUID().toString().replace("-", "");
        payReq.timeStamp = String.valueOf(System.currentTimeMillis() / 1000);

        // 构建基本的参数列表
        List<TwoTuple<String, String>> paramList = new ArrayList<>();
        paramList.add(new TwoTuple<>("appid", payReq.appId));
        paramList.add(new TwoTuple<>("noncestr", payReq.nonceStr));
        paramList.add(new TwoTuple<>("package", payReq.packageValue));
        paramList.add(new TwoTuple<>("partnerid", payReq.partnerId));
        paramList.add(new TwoTuple<>("prepayid", payReq.prepayId));
        paramList.add(new TwoTuple<>("timestamp", payReq.timeStamp));

        String sign = generateWechatMD5Signature(paramList);
        //签名
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        payReq.sign = sign;
        return payReq;
    }


    /**
     * 根据参数列表生成MD5签名
     *
     * @param paramList
     * @return
     */

    private String generateWechatMD5Signature(List<TwoTuple<String, String>> paramList) {
        StringBuilder sb = new StringBuilder();
        for (TwoTuple<String, String> paramTuple : paramList) {
            sb.append(paramTuple.first);
            sb.append('=');
            sb.append(paramTuple.second);
            sb.append('&');
        }
        sb.append("key=").append(API_KEY);


        return MD5Util.getMD5String(sb.toString()).toUpperCase();
    }

    // 解析XML
    public Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false) {

                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


//    private Map<String, String> decodeXml(String xml) throws Exception {
//        Map<String, String> resultMap = new HashMap<>();
//        XmlPullParser parser = Xml.newPullParser();
//        parser.setInput(new StringReader(xml));
//        int event = parser.getEventType();
//        while (event != XmlPullParser.END_DOCUMENT) {
//            String nodeName = parser.getName();
//            switch (event) {
//                case XmlPullParser.START_DOCUMENT:
//                    break;
//                case XmlPullParser.START_TAG:
//                    if (!"xml".equals(nodeName)) {
//                        resultMap.put(nodeName, parser.nextText());
//                    }
//                    break;
//                case XmlPullParser.END_TAG:
//                    break;
//            }
//            event = parser.next();
//        }
//        return resultMap;
//    }

    //============================================================================================================//
//==============================================================微信支付end=================================//
//============================================================================================================//

    //=======================================================================================//
    //====================================支付宝 statt=======================================//
    //=======================================================================================//

    /**
     * 支付宝部分
     */
    // 商户PID
    public static final String PARTNER = "2088221352629802";
    // 商户收款账号
    public static final String SELLER = "1553534807@qq.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANHyHbFEKVCis9/7\n" +
            "xn/d8bNY8A22+ompHCBXtW4VS/v8KR4ynqoOzFkyamHX1T55JDVKN3WK58J167q2\n" +
            "V+H4G4OsXtoehyvived+9JAFjDRJKStexMBnSuMnAhT57uzxNsyThradG66IT4ol\n" +
            "nh12p+Mkz9WufwyRMOPZEbZZ/hjrAgMBAAECgYASmkB1R5PdmD0V+KozBEh5WsUS\n" +
            "ggcbEYfzebISygJMlqBSE6wpZ1xF/wicGNLFUVia/DODz8YPXgGALs4EsMZHMEt3\n" +
            "cqiMSO8Ez77hJYMyBS9LSwKPKdPttq5tmdgcX6OS0Agg/yCVahSeSykxr+RmSnHl\n" +
            "sU//DassXEOxvZ2SeQJBAP2zQzgl7SUIpwI2HJHDJd+QpoHDxV3qnjPMe06Cr2Bw\n" +
            "31LSgkbUzCia2uNMzEeZiZKg32bx9oowK8h6UKmd0VcCQQDT2VEQb9bxwjlbHvUL\n" +
            "ShzmTC/xVVnjz9LriEpv9xkHwpm6xvBvbHb0RRb0wEj68a/Ov2Clja/l11J5bCZe\n" +
            "oRSNAkARFExmL0dzws1bfMEXnwehsV9ERSW4WN8lpZJ3ipy75V1jegCDPDgyU5qA\n" +
            "yt7FwzRbxrDDgW6ThwKrkHB1usYpAkAB3k7KuFHK/A2JcjJzEQpENbPOixQp4DFa\n" +
            "Bm+xoRpFaT/1179THD/IU7uqGPAL1onYZvOxQhilDEsb6wpDl2QZAkEAyFe8UEAc\n" +
            "gc7l30iBAiU7GUnESQuNU9KwxwdOLQ22ljE/sJikmxxS/L+bJQR8H8ek05BJ2LoW\n" +
            "kbLu3cRpH41Pcg==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISR" +
            "cc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mk" +
            "jzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
    private static final int SDK_PAY_FLAG = 1;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(PayMentActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        payComplete = true;
                        // 未替用户付款  支付成功后  重新登录
                        new Thread(payCompleteTask).start();
                        new Thread(payTask).start();

                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayMentActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayMentActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay(View v) {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }
        // 维修项目  详细描述  总金额
//        String orderInfo = getOrderInfo("维修项目:", "商品描述", "1");
        String orderInfo;
        String info;
        String info1;
        if (orderPush != null) {
            if (orderPush.getDataMap().getService().getGroup_name() != null &&
                    !orderPush.getDataMap().getService().getGroup_name().equals("")) {
                info = orderPush.getDataMap().getService().getGroup_name();
            } else {
                info = "测试商品";
            }

            if (orderPush.getDataMap().getOrder_comment() != null &&
                    !orderPush.getDataMap().getOrder_comment().equals("")) {
                info1 = orderPush.getDataMap().getOrder_comment();
            } else {
                info1 = "测试商品描述";
            }
        } else {
            if (entity.getService() != null &&
                    !entity.getService().equals("")) {
                info = entity.getService();
            } else {
                info = "测试商品";
            }

            if (entity.getComment() != null &&
                    !entity.getComment().equals("")) {
                info1 = entity.getComment();
            } else {
                info1 = "测试商品描述";
            }
        }
        orderInfo = getOrderInfo(info, info1, "0.01");

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayMentActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
     *
     * @param v
     */
    public void h5Pay(View v) {
        Intent intent = new Intent(this, H5PayActivity.class);
        Bundle extras = new Bundle();
        /**
         * url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity，
         * demo中拦截url进行支付的逻辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现，
         * 商户可以根据自己的需求来实现
         */
        String url = "http://m.meituan.com";
        // url可以是一号店或者美团等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
        extras.putString("url", url);
        intent.putExtras(extras);
        startActivity(intent);
    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    //=======================================================================================//
    //====================================支付宝 end=======================================//
    //=======================================================================================//

//    /**
//     * 支付成功
//     */
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//            }
//        }
//    };

//    /**
//     * 根据广播的订单id去获取订单信息
//     */
//    Runnable pushOrderTask = new Runnable() {
//        @Override
//        public void run() {
////            Message message = Message.obtain();
//            try {
//                HashMap<String, String> map = new HashMap<>();
//                map.put(AppConstants.ORDER_ID, String.valueOf(orderId));
//                String url = UrlUtils.addParams(AppApi.DETAIL, map); // 拼接参数
//                Log.d("pushorder", url);
//                String response = OkHttpClientManager.getAsString(url);
//                Log.d("pushorder", response.toString());
//
//                Gson gson = new Gson();
//                orderPush = gson.fromJson(response, OrderPush.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
////            handler.sendMessage(message);
//        }
//    };

//    @Subscribe(threadMode = ThreadMode.MAIN) // 在后台线程执行
//    public void onEvent(Integer orderId) {
//        this.orderId = orderId;
//        new Thread(pushOrderTask).start(); // 获取推送订单
//    }

    /**
     * 支付完成后
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: // 支付失败
                    Toast.makeText(PayMentActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    break;

                case 1: // 支付完成
                    JPushInterface.resumePush(getApplicationContext());
                    finish();
                    EventBus.getDefault().post("refresh");
                    break;

                case 2: // 未付款支付完成
//                    Intent toLogin = new Intent(PayActivity.this, LoginActivity.class);
//                    startActivity(toLogin);
//                    finish();
                    break;
            }
        }
    };

    /**
     * 改变订单状态--付款完成
     *
     * @param ORDER_STATUS 订单状态 2 抢单
     */
    Runnable payCompleteTask = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> map = new HashMap<>();
            map.put(AppConstants.ORDER_ID, entity.getOrder_id());
            map.put(AppConstants.ORDER_STATUS, "7");
            map.put(AppConstants.PARAM, "");
            String url = UrlUtils.addParams(AppApi.ORDERSTATUS, map);
            Response status;
            Message message = Message.obtain();
            try {
                status = OkHttpClientManager.getAsyn(url, PayMentActivity.this);
                if (status.code() != 401) {
                    if (type == 2) {
                        message.what = 1;
                    } else {
                        message.what = 2;
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 0;
            }
            handler.sendMessage(message);
        }
    };

    /**
     * 支付完成调用
     * type 1 微信 2 支付宝
     */
    Runnable payTask = new Runnable() {
        @Override
        public void run() {
            Message message = Message.obtain();
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.PAYTYPE, String.valueOf(payType));
                if (entity != null) {
                    map.put(AppConstants.ORDER_ID, entity.getOrder_id());
                } else {
                    map.put(AppConstants.ORDER_ID, String.valueOf(orderPush.getDataMap().getOrder_id()));
                }
                String url = UrlUtils.addParams(AppApi.PAY, map);
                Response response = OkHttpClientManager.getAsyn(url, PayMentActivity.this);
                String result = response.body().string();
                if (response.code() != 401) {
                    if (type == 2) {
                        message.what = 2;
                    } else {
                        message.what = 1;
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 0;
            }
            handler.sendMessage(message);
        }

    };

    /**
     * 微信支付完成
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(String str) {
        if (str.equals("wx0")) {
            new Thread(payTask).start(); // 获取推送订单
            new Thread(payCompleteTask).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
