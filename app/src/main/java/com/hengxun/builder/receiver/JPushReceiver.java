package com.hengxun.builder.receiver;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.model.HelpOrder;
import com.hengxun.builder.model.OrderPush;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.Foreground;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.HomeActivity;
import com.hengxun.builder.view.activity.account.LoginActivity;
import com.hengxun.builder.view.widget.OrderPushDialog;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by ZY on 2016/3/11.
 * 处理极光推送的信息
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private int orderId = 0; // 订单号
    private OrderPush orderPush; // 推送过来的订单
    private HelpOrder order; // 抢险抢修的订单
    private Context context;
    private int workerId;   // 匠人id 用于跟订单id比对
    private int typeId;     // 1正常 22派单 110抢险抢修
    private OrderPushDialog orderPushDialog;
    //////////////////////////////////
    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        this.context = context;
        //////////////////////////////////////////
        mediaPlayer=MediaPlayer.create(context,R.raw.music);
        //亮屏幕
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();

        ///////////////////////////////////////////

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver1] 接收Registration Id : " + regId);
            // send the Registration Id to your server...
            // 保存用户id
            PreferenceUtil.savePerfs(context, AppConstants.WORKERSSP, AppConstants.REGISTRATIONID, regId);
        }

        // 判断用户是否登录 未登录则去登录
        String token = PreferenceUtil.readString(context,
                SharedPrefer.TOKEN, SharedPrefer.TOKEN);
        if (TextUtils.isEmpty(token)) {
            if (LoginActivity.finishActivity == null) {
                Intent intent1 = new Intent(context, LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
            return;

        }
        workerId = PreferenceUtil.readInteger(context, SharedPrefer.WORKERID, SharedPrefer.WORKERID);

        if (bundle.getString(JPushInterface.EXTRA_EXTRA) != null) {
            String pushMsg = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.d(TAG, pushMsg);
            JSONObject jsonObject;
//            int order_status = -1; // 订单状态

            try {
                jsonObject = new JSONObject(pushMsg);
                orderId = jsonObject.getInt("orderId");
                typeId = jsonObject.getInt("typeId");
                //////////////////////////////////////
                if (typeId == 1){
                    mediaPlayer.start();
                }
                /////////////////////////////////////////
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int notifactionId;
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver2] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver3] 接收到推送下来的通知");
            // 如果在前台运行 获取订单状态
            boolean isrunning = Foreground.get().isForeground();
            notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            if (isrunning) {

                if (typeId == 0  // 取消
                        || typeId == 1  // 可接单
//                        || typeId == 2  // 已接单
//                        || typeId == 3  // 已改价
                        || typeId == 4  // 已开工
                        || typeId == 6  // 用户选择现金支付
                        || typeId == 22 // 派单
                        ) { // 抢单或者派单
                    new Thread(new PushOrderTask(notifactionId)).start();
                } else if (typeId == 110) { // 抢险抢修订单
                    new Thread(new EmergencyTask(notifactionId)).start();
                }
            }
            Log.d(TAG, "[MyReceiver4] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver5] 用户点击打开了通知");
            notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            new Thread(new PushOrderTask(notifactionId)).start();

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver7] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver8]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver9] Unhandled intent - " + intent.getAction());
        }
    }

    /**
     * 用户点击通知栏后的操作
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(context, HomeActivity.class);
            int notifactionId = msg.arg1;
            switch (msg.what) {
                case 10: // 是匠人自己的订单
                    switch (orderPush.getDataMap().getOrder_status()) {
                        case 0: // 订单取消了
                            Intent receiver = new Intent(); // 发送广播启动发送地理位置的服务
                            receiver.setAction("com.hengxun.builder.service");
                            context.sendBroadcast(receiver);

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            orderPush.setNotifactionId(notifactionId);
                            context.startActivity(intent);
                            EventBus.getDefault().post(orderPush);
                            break;

                        case 1: // 指定给我的
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            orderPush.setNotifactionId(notifactionId);
                            context.startActivity(intent);
                            EventBus.getDefault().post(orderPush);
                            orderPush = null;
                            break;

                        case 4: // 开工
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            orderPush.setNotifactionId(notifactionId);
                            context.startActivity(intent);
                            EventBus.getDefault().post(orderPush);
//                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            EventBus.getDefault().post(orderId);
//                            context.startActivity(i);
                            break;

                        case 6: // 替用户付款
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            orderPush.setNotifactionId(notifactionId);
                            context.startActivity(intent);
                            EventBus.getDefault().post(orderPush);
                            break;

                        case 7: // 付款完成
                            break;

                        case 22: // 派单
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            orderPush.setNotifactionId(notifactionId);
                            context.startActivity(intent);
                            EventBus.getDefault().post(orderPush);
                            break;
                    }
                    break;

                case 2: // 订单可以抢
//                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    orderPush.setNotifactionId(notifactionId);
                    context.startActivity(intent);
                    EventBus.getDefault().post(orderPush);
                    orderPush = null;
//                    WindowManager wm = (WindowManager) context
//                            .getSystemService(Context.WINDOW_SERVICE);
//                    int wid = wm.getDefaultDisplay().getWidth();
//                    int hei = wm.getDefaultDisplay().getHeight();
//                    // 如果没有正在显示的对话框
//                    if (orderPushDialog != null) {
//                        orderPushDialog.dismiss();
//                        orderPushDialog = null;
//                    }
//
//                    double MLATITUDE = Double.parseDouble(PreferenceUtil.readString(context, SharedPrefer.MLATITUDE, SharedPrefer.MLATITUDE));
//                    double MLONGTITUDE = Double.parseDouble(PreferenceUtil.readString(context, SharedPrefer.MLONGTITUDE, SharedPrefer.MLONGTITUDE));
//                    orderPushDialog = new OrderPushDialog(context, wid, hei, true, orderPush, MLATITUDE, MLONGTITUDE);
//                    if (orderPushDialog != null && !orderPushDialog.isShowing()) {
//                        orderPushDialog.show();
//                    }
//                    orderPushDialog.setOnOrderClickListener(new OrderPushDialog.OnOrderClickListener() {
//                        @Override
//                        public void onOrderclick(View view, boolean isKnoic, OrderPush bean) {
////                            new Thread(getOrderTask).start(); // 抢单
//                        }
//                    });
                    break;

                case 3: // 订单被抢了
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                    EventBus.getDefault().post(orderPush);
                    Toast.makeText(context, "该订单已经被其他匠人受理", Toast.LENGTH_SHORT).show();
                    break;

                case 110: // 抢险抢修订单
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    order.setNotifactionId(notifactionId);
                    context.startActivity(intent);
                    EventBus.getDefault().post(order);
                    break;

                case 999: // token失效
                    Toast.makeText(context, R.string.dialog_token_unable, Toast.LENGTH_SHORT).show();

                default:
//                    Toast.makeText(context, "该订单已经被其他匠人受理", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 根据广播的订单id去获取订单信息
     */
    public class PushOrderTask implements Runnable {
        private int notifactionId;

        public PushOrderTask(int notifactionId) {
            this.notifactionId = notifactionId;
        }

        @Override
        public void run() {
            Message message = Message.obtain();
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.ORDER_ID, String.valueOf(orderId));
                String url = UrlUtils.addParams(AppApi.DETAIL, map); // 拼接参数
                Response response = OkHttpClientManager.getAsyn(url, context);
                if (response.code() != 401) {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    orderPush = gson.fromJson(result, OrderPush.class);
                    message.what = -1;
                    message.arg1 = notifactionId;
                    // 订单是匠人自己的
                    if (workerId == orderPush.getDataMap().getWorker_id()) {
                        message.what = 10;
                    } else if (0 == orderPush.getDataMap().getWorker_id()) { // 订单没有归属

                        if (orderPush.getDataMap().getOrder_status() == 1) { // 订单可以抢
                            message.what = 2;
                        } else if (orderPush.getDataMap().getOrder_status() == 2) { // 订单已被抢
                            message.what = 3;
                        } else {
                        /* 服务器炸了 */
                        }
                    } else {
                        /* nothing to do*/
                    }
                } else { // token失效
                    message.what = 999;
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 999;
            }
            handler.sendMessage(message);
        }
    }

    /**
     * 抢险抢修的订单
     */
    public class EmergencyTask implements Runnable {
        private int notifactionId;

        public EmergencyTask(int notifactionId) {
            this.notifactionId = notifactionId;
        }

        @Override
        public void run() {
            Message message = Message.obtain();
            HashMap<String, String> map = new HashMap<>();
            map.put(AppConstants.ORDERID, String.valueOf(orderId));
            String url = UrlUtils.addParams(AppApi.EMERGENCY, map); // 拼接参数
            try {
                Response response = OkHttpClientManager.getAsyn(url, context);
                if (response.code() != 401) {
                    String result = response.body().string();
                    Log.d(TAG, result);
                    try {
                        Gson gson = new Gson();
                        order = gson.fromJson(result, HelpOrder.class);
                        if (order.getCode() == 200) {
                            message.what = 110;
                        }
                        order.setNotifactionId(notifactionId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(message);
        }
    }

//    /**
//     * 抢险抢修的订单
//     */
//    Runnable Emergency = new Runnable() {
//        @Override
//        public void run() {
//            Message message = Message.obtain();
//            HashMap<String, String> map = new HashMap<>();
//            map.put(AppConstants.ORDER_ID, String.valueOf(orderId));
//            String url = UrlUtils.addParams(AppApi.EMERGENCY, map); // 拼接参数
//            try {
//                Response response = OkHttpClientManager.getAsyn(url, context);
//                if (response.code() != 401) {
//                    String result = response.body().string();
//                    Log.d(TAG, result);
//                    try {
//                        JSONObject object = new JSONObject(result);
//                        OrderPush.DataMapEntity order = new OrderPush.DataMapEntity();
//                        order.setOrder_id(object.getInt("order_id"));
//                        order.setMobile(object.getString("mobile"));
//                        order.setPost_code(object.getInt("post_code"));
//                        order.setAddress(object.getString("address"));
//                        order.setCustomer_comment(object.getString("comment"));
//                        OrderPush.DataMapEntity.ServiceEntity entity = new OrderPush.DataMapEntity.ServiceEntity();
//                        entity.setGroup_name(object.getString("service"));
//                        order.setService(entity);
//                        order.setOrder_status(object.getInt("order_status"));
//                        order.setOrder_time(object.getLong("order_time"));
//                        order.setAdjustable(object.getInt("adjustable"));
//                        order.setAppoint_time(object.getLong("appoint_time"));
//                        List<String> img = new ArrayList<>();
//                        JSONArray array = object.getJSONArray("images");
//                        for (int i = 0; i < array.length(); i++) {
//                            img.add(array.getString(i));
//                        }
//                        order.setImages(img);
//                        order.setTotal_price(object.getDouble("price"));
//                        order.setCustomerName(object.getString("customerName"));
//                        order.setX(object.getDouble("x"));
//                        order.setY(object.getDouble("y"));
//                        orderPush.setDataMap(order);
//                        message.what = 110;
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            handler.sendMessage(message);
//        }
//    };

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }
}
