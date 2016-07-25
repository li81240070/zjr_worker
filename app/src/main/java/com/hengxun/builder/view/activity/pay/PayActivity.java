package com.hengxun.builder.view.activity.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.model.BacklogOrder;
import com.hengxun.builder.model.OrderPush;
import com.hengxun.builder.model.PayAliInfo;
import com.hengxun.builder.model.PayCompleteInfo;
import com.hengxun.builder.model.PayWxInfo;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.payutils.payutils.ali.PayResult;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.order.OrderDetailsActivity;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Response;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by ZY on 2016/3/11.
 * 支付页面
 */
public class PayActivity extends BaseActivity implements View.OnClickListener {
    public static PayActivity finishActivity;

    private TextView pay_wx_Tv;  // 微信支付
    private TextView pay_zfb_Tv; // 支付宝支付
    private String orderId;         // 订单id
    private OrderPush orderPush; // 极光推送订单
    private int type;            // 跳转过来的类型 1 正常支付 2 未付款支付
    //    private boolean payComplete = false; // 付款是否完成
    private int payType;         // 支付方式 1 微信 2 支付宝
    private String status;       // APP端支付状态
    private List<BacklogOrder.DataMapEntity.ToPayOrdersEntity> orders; // 需要支付的订单集合

    private PayWxInfo wxInfo;    // 微信支付信息
    private PayAliInfo aliInfo;  // 支付宝支付信息
    private PayBrodcastReceiver receiver; // 微信支付结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        finishActivity = this;
//        EventBus.getDefault().register(this);
        showToolBar(getResources().getString(R.string.pay_money), true, this);
        registerBroadcastReceiver(); // 注册广播接收支付结果
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
        orderId = getIntent().getStringExtra("orderId");
        orders = (List<BacklogOrder.DataMapEntity.ToPayOrdersEntity>) getIntent().getSerializableExtra("orders");
        if (orderId != null) {
            new Thread(pushOrderTask).start();
        }
        type = getIntent().getIntExtra("type", 1);
//        if (type == 2) { // 如果是代支付状态
//            Toast.makeText(this, "请立刻完成支付", Toast.LENGTH_LONG).show();
//        }

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
            int notifactionId = getIntent().getIntExtra("notifactionId", 0);
            JPushInterface.clearNotificationById(PayActivity.this, notifactionId);
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
            case R.id.pay_wx_Tv: // wx pay
                payType = 1;
                new Thread(getPayInfoTask).start(); // 获取支付信息
//                payComplete = true;
//                JPushInterface.resumePush(getApplicationContext());
//                finish();
//                EventBus.getDefault().post("refresh");
                break;
            case R.id.pay_al_Tv: // ali pay
                payType = 2;
                new Thread(getPayInfoTask).start(); // 获取支付信息
                break;

        }
    }

    /**
     * 创建支付宝订单
     */
    private String getOrderInfo() {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + aliInfo.getPartner()
                + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + aliInfo.getSellerid()
                + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + aliInfo.getOuttradeno()
                + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + aliInfo.getSubject() + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + aliInfo.getBody() + "\"";

        double amount = aliInfo.getTotalfee();
        String totalFee = String.format(Locale.getDefault(), "%.2f", amount);
        // 商品金额
        orderInfo += "&total_fee=" + "\"" + totalFee + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + aliInfo.getNotifyurl()
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=" + "\"" + aliInfo.getService() + "\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=" + "\""
                + aliInfo.getPaymenttype() + "\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=" + "\""
                + aliInfo.getInputcharset() + "\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=" + "\"" + aliInfo.getItbpay() + "\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&show_url=" + "\"" + aliInfo.getShowurl()
                + "\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * 构建微信吊起支付接口的参数
     */
    private PayReq buildWechatPayReq() {
        PayReq payReq = new PayReq();
        payReq.appId = wxInfo.getAppid();
        payReq.partnerId = wxInfo.getPartnerid();
        payReq.prepayId = wxInfo.getPrepayid();
        payReq.packageValue = wxInfo.getPackage_();
        payReq.nonceStr = wxInfo.getNoncestr();
        payReq.timeStamp = wxInfo.getTimestamp();
        payReq.sign = wxInfo.getSign();
        return payReq;
    }

    /**
     * 支付宝部分
     */
    private void aliPay() {
//        // 构造PayTask 对象
//        PayTask alipay = new PayTask(PayActivity.this);

        // 签名
        String sign = aliInfo.getSign();
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /* 完整的符合支付宝参数规范的订单信息 */
        String orderInfo = getOrderInfo();
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + "sign_type=\"" + aliInfo.getSignType() + "\"";

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);
                Log.d("PayActivity", result);

                Message msg = new Message();
                msg.what = 6;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };
        // 必须异步调用
//        Thread payThread = new Thread(payRunnable);
        new Thread(payRunnable).start();
    }

    public class PayBrodcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("wxPay")) {
                int result = intent.getIntExtra("errCode", 0);
                switch (result) {
                    case -1: // 取消支付
                        status = "0";
                        new Thread(payStatusTask).start();
                        break;
                    case 0: // 支付完成
                        status = "1";
                        new Thread(payStatusTask).start();
                        Toast.makeText(context, "支付完成", Toast.LENGTH_SHORT).show();
                        break;
                    case 1: // 支付失败

                    default:
                        status = "0";
                        new Thread(payStatusTask).start();
                        break;
                }
            }
        }
    }

    /**
     * 接收支付结果信息的广播
     */
    public void registerBroadcastReceiver() {
        // 注册广播
        IntentFilter counterActionFilter = new IntentFilter("wxPay");
        receiver = new PayBrodcastReceiver();
        registerReceiver(receiver, counterActionFilter);
    }

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
                    Toast.makeText(PayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    break;

                case 1: // 获取微信支付信息成功 进行支付
                    if (wxInfo != null) {
                        IWXAPI api = WXAPIFactory.createWXAPI(PayActivity.this, wxInfo.getAppid(), false);
                        // 真正的支付
                        api.sendReq(buildWechatPayReq());
                    } else {
//                        aliPay();
                        Toast.makeText(PayActivity.this, "获取支付信息失败", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 2: // 获取支付宝支付信息成功 进行支付
                    aliPay();
                    break;

                case 3: // 获取支付信息失败
                    Toast.makeText(PayActivity.this, "获取支付信息失败", Toast.LENGTH_SHORT).show();
                    break;

                case 4: // 支付成功
                    if (orders != null && orders.size() > 1) {
//                        if (orders == null || orders.size() == 0) {
//                            payComplete = true;
//                            JPushInterface.resumePush(getApplicationContext());
                        EventBus.getDefault().post("remove");
//                        }
                    } else if (orderPush != null) {
                        JPushInterface.resumePush(getApplicationContext());
                        EventBus.getDefault().post("easyPay");
                        if (OrderDetailsActivity.finishActivity != null) {
                            OrderDetailsActivity.finishActivity.finish();
                        }
//                        else if (NotPayDetailsActivity.finishActivity != null) {
//                            NotPayDetailsActivity.finishActivity.finish();
//                        }
                    }
                    finish();
                    break;

                case 5: // 未付款支付完成
//                    Intent toLogin = new Intent(PayActivity.this, LoginActivity.class);
//                    startActivity(toLogin);
//                    finish();
                    break;

                case 6: // 支付宝支付信息
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                        payComplete = true;
//                        // 未替用户付款  支付成功后  重新登录
//                        new Thread(payCompleteTask).start();
//                        new Thread(payTask).start();
                        status = "1";
                        new Thread(payStatusTask).start(); // 查询交易信息
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                            status = "0";
//                            EventBus.getDefault().post("refresh");
                            new Thread(payStatusTask).start();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                            status = "0";
                            new Thread(payStatusTask).start();
                        }
                    }
                    break;

                case 10:
                    new Thread(payInfoTask).start();
                    break;

                case 20:
                    Toast.makeText(PayActivity.this, "订单信息正在获取,请稍候", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 获取支付信息
     */
    Runnable getPayInfoTask = new Runnable() {
        @Override
        public void run() {
            String token = PreferenceUtil.readString(PayActivity.this, SharedPrefer.TOKEN, SharedPrefer.TOKEN);
            int workerId = PreferenceUtil.readInteger(PayActivity.this, SharedPrefer.WORKERID, SharedPrefer.WORKERID);

            Message message = Message.obtain();
            String response;
            JSONObject object = new JSONObject();
            Headers.Builder builder = new Headers.Builder();
            builder.add("token", token);
            Headers headers = builder.build();
            try {
                if (orderPush != null) {
                    try {
                        object.put("workerId", workerId);
//                        object.put("customerId", orderPush.getDataMap().getCustomer_id());
                        object.put("orderId", String.valueOf(orderPush.getDataMap().getOrder_id()));
                        object.put("payType", payType);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    response = OkHttpClientManager.postHeadObjectAsString(AppApi.TEMP, headers, object);
                    Gson gson = new Gson();

                    // 微信支付
                    if (payType == 1) {
                        wxInfo = gson.fromJson(response, PayWxInfo.class);
                        message.what = 1;

                    } else { // 支付宝支付
                        aliInfo = gson.fromJson(response, PayAliInfo.class);
                        message.what = 2;
                    }
                } else if (orders != null && orders.size() > 0) {
                    try {
                        object.put("workerId", workerId);
//                        object.put("customerId", orderPush.getDataMap().getCustomer_id());
                        object.put("orderId", String.valueOf(orders.get(0).getOrder_id()));
                        object.put("payType", payType);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    response = OkHttpClientManager.postHeadObjectAsString(AppApi.TEMP, headers, object);
                    Log.d("PayActivity", response);
                    Gson gson = new Gson();

                    // 微信支付
                    if (payType == 1) {
                        wxInfo = gson.fromJson(response, PayWxInfo.class);
                        message.what = 1;

                    } else { // 支付宝支付
                        aliInfo = gson.fromJson(response, PayAliInfo.class);
                        message.what = 2;
                    }
                } else {
                    message.what = 20;
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 3; // 获取支付信息失败
            }
            handler.sendMessage(message);
        }
    };

    /**
     * 修改订单状态
     */
    Runnable payStatusTask = new Runnable() {
        @Override
        public void run() {
            Message message = Message.obtain();
            Response response;
            String orderid;
            if (orderPush != null) {
                orderid = String.valueOf(orderPush.getDataMap().getOrder_id());
            } else {
                orderid = orders.get(0).getOrder_id();
            }
            try {
                OkHttpClientManager.Param params[] = new OkHttpClientManager.Param[]{
                        new OkHttpClientManager.Param(AppConstants.ORDERID, orderid)
                        , new OkHttpClientManager.Param(AppConstants.STATUS, status)
                };
                response = OkHttpClientManager.post(AppApi.PAYSTATUS, PayActivity.this, params[0], params[1]);
                String result = response.body().string();
                if (response.code() == 200) {
                    message.what = 10;
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 0; // 支付失败
            }
            handler.sendMessage(message);
        }
    };

    /**
     * 向服务器查询支付信息
     */
    Runnable payInfoTask = new Runnable() {
        @Override
        public void run() {
            Message message = Message.obtain();
            Response response;
            try {
                String orderid;
                if (orderPush != null) {
                    orderid = String.valueOf(orderPush.getDataMap().getOrder_id());
                } else {
                    orderid = orders.get(0).getOrder_id();
                }
                OkHttpClientManager.Param params[] = new OkHttpClientManager.Param[]{
                        new OkHttpClientManager.Param(AppConstants.ORDERID, orderid)
                        , new OkHttpClientManager.Param(AppConstants.STATUS, status)
                };
                response = OkHttpClientManager.post(AppApi.ORDERPAYSTATUS, PayActivity.this, params[0], params[1]);
                String result = response.body().string();
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    PayCompleteInfo info = gson.fromJson(result, PayCompleteInfo.class);
                    PayCompleteInfo.DataMapEntity entity = info.getDataMap();
                    if (entity.getStatus() == 1) {
                        message.what = 4;
                    } else {
                        message.what = 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 0; // 支付失败
            }
            handler.sendMessage(message);
        }
    };

    /**
     * 根据广播的订单id去获取订单信息
     */
    Runnable pushOrderTask = new Runnable() {
        @Override
        public void run() {
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.ORDER_ID, String.valueOf(orderId));
                String url = UrlUtils.addParams(AppApi.DETAIL, map); // 拼接参数
                Log.d("pushorder", url);
                Response response = OkHttpClientManager.getAsyn(url, PayActivity.this);
                if (response.code() != 401) {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    orderPush = gson.fromJson(result, OrderPush.class);
//                    Message message = Message.obtain();
//                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    };

//    /**
//     * 微信支付完成
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onUserEvent(String str) {
//        if (str.equals("wx0")) {
////            new Thread(payTask).start(); // 获取推送订单
////            new Thread(payCompleteTask).start();
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post("refresh");
        unregisterReceiver(receiver); // 取消广播
//        EventBus.getDefault().unregister(this);
    }
}
