package com.hengxun.builder.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.model.BacklogOrder;
import com.hengxun.builder.model.GetOrderStatus;
import com.hengxun.builder.model.HelpOrder;
import com.hengxun.builder.model.OrderList;
import com.hengxun.builder.model.OrderPush;
import com.hengxun.builder.model.PayCompleteInfo;
import com.hengxun.builder.model.UserInfo;
import com.hengxun.builder.model.Version;
import com.hengxun.builder.receiver.JPushReceiver;
import com.hengxun.builder.utils.AppUtils;
import com.hengxun.builder.utils.okhttp.HttpsController;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.MD5Util;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.account.ErrorActivity;
import com.hengxun.builder.view.activity.account.LoginActivity;
import com.hengxun.builder.view.activity.map.OrderMapActivity;
import com.hengxun.builder.view.activity.order.EmerDetailsActivity;
import com.hengxun.builder.view.activity.order.OfferOrderDetailsActivity;
import com.hengxun.builder.view.activity.order.OrderDetailsActivity;
import com.hengxun.builder.view.activity.personal.MyInfoActivity;
import com.hengxun.builder.view.adapter.HomeRvAdapter;
import com.hengxun.builder.view.widget.CircleImage;
import com.hengxun.builder.view.widget.HelpOrderDialog;
import com.hengxun.builder.view.widget.OrderInforDialog;
import com.hengxun.builder.view.widget.OrderPushDialog;
import com.hengxun.builder.view.widget.SwipeRefreshLoadingLayout;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by ZY on 2016/3/7.
 * 开工页面
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener, OnGetGeoCoderResultListener {
    public static HomeActivity finishActivity; // 注销销毁的activity
    public static int UPDATEUSER = 20;

    private static boolean ISWORKING = false; // 是否在工作
    private int param;
//    private static boolean ISOFFER = true; // 是否有没接收的派单

    private Button home_startWork_Btn; // 开工
    private RelativeLayout home_Rl; // 开工前的布局
    private AlertDialog dialog; // 确认收工的对话框
    //    private TextView homeDialogDistance_Tv;     // 订单距离匠人多少公里
    private OrderInforDialog orderDialog;       // 订单详情对话框
    private OrderPushDialog orderPushDialog;    // 推送订单对话框
    private HelpOrderDialog helpOrderDialog;    // 抢险抢修对话框
    private LocationClient mLocationClient;
    private LocationListener locationListener;
    private static double MLATITUDE, MLONGTITUDE; // 经度  纬度
    private LatLng orderStart;                  // 工人位置
    private Set<String> tags;                   // 开工匠人标签
    private Set<String> notWorktags;            // 未开工匠人标签
    private int orderId;                     // 广播信息
    private ImageView homeNoOrder_Iv;               // 没有订单列表
    private WaittingDiaolog progressDialog;

    private CircleImage home_userHead_Iv;             // 头像
    private TextView home_userName_Tv;              // 姓名
    private RatingBar home_userRating_Rb;           // 评分
    private TextView homeUserOrder_Tv;              // 订单数

    private SwipeRefreshLoadingLayout homeOrder_Rl; // 下拉刷新列表
    private RecyclerView homeOrder_Rv; // 订单列表
    private HomeRvAdapter adapter;
    private Handler imageHandler; // 设置头像的handler
    private UserInfo userInfo; // 登录信息
    private Version version;   // 版本信息
    private HelpOrder helpOrder;
    private List<OrderList.DataMapEntity.OrdersEntity> list;
    private List<OrderList.DataMapEntity.OrdersEntity> allList = new ArrayList<>();
    private List<OrderList.DataMapEntity.OrdersEntity> allAllList = new ArrayList<>();

    private OrderList.DataMapEntity.OrdersEntity entity;    // 抢单的订单
    //    private OrderList.DataMapEntity.OrdersEntity payOrder;  // 未付款的订单
    private List<BacklogOrder.DataMapEntity.ToPayOrdersEntity> toPayOrders; // 需要支付的订单
    private List<BacklogOrder.DataMapEntity.OfferOrdersEntity> offerOrders; // 需要接受的派单
    private List<BacklogOrder.DataMapEntity.OfferEmergencysEntity> emerOrders; // 抢险抢修订单
    private OrderPush orderPush; // 极光推送订单
    private boolean type = true;  // 区分是推送订单还是列表订单 true 列表订单 false 推送订单
    private int page = 1;           // 分页加载页码
    private int typeHome = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        finishActivity = this;
        EventBus.getDefault().register(this);
        showToolBar(getResources().getString(R.string.app_name), true, this);

//        if (null != LoginActivity.finishActivity) {
//            LoginActivity.finishActivity.finish();
//        }
        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        if (userInfo == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        home_startWork_Btn = (Button) findViewById(R.id.home_startWork_Btn);
        home_Rl = (RelativeLayout) findViewById(R.id.home_Rl);
        home_userHead_Iv = (CircleImage) findViewById(R.id.home_userHead_Iv);
        home_userName_Tv = (TextView) findViewById(R.id.home_userName_Tv);
        home_userRating_Rb = (RatingBar) findViewById(R.id.home_userRating_Rb);
        homeUserOrder_Tv = (TextView) findViewById(R.id.homeUserOrder_Tv);
        homeOrder_Rv = (RecyclerView) findViewById(R.id.homeOrder_Rv);
        homeOrder_Rl = (SwipeRefreshLoadingLayout) findViewById(R.id.homeRv);
        homeNoOrder_Iv = (ImageView) findViewById(R.id.homeNoOrder_Iv);

    }

    @Override
    protected void initData() {
        super.initData();
        if (LoginActivity.finishActivity != null) {
            LoginActivity.finishActivity.finish(); // 销毁登录页面
        }
        progressDialog = new WaittingDiaolog(this);
        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setMessage("正在加载中...");
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
        new Thread(versionTask).start(); // 获取自动更新信息
        home_startWork_Btn.setOnClickListener(this);
        new Thread(backlogTask).start();
        // 初始化定位方法
        locationListener = new LocationListener();
        mLocationClient = new LocationClient(this);
        initLocation(0);
        mLocationClient.registerLocationListener(locationListener);    //注册监听函数
        mLocationClient.start();//开始定位

        adapter = new HomeRvAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homeOrder_Rv.setLayoutManager(gridLayoutManager);
        homeOrder_Rv.setAdapter(adapter);

        homeOrder_Rl.setOnLoadListener(new SwipeRefreshLoadingLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                typeHome = 2;
                page++;
                new Thread(orderListTask).start();
            }
        });
        homeOrder_Rl.setOnRefreshListener(new SwipeRefreshLoadingLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                typeHome = 1;
                page = 1;
                new Thread(orderListTask).start();
            }
        });

        // 点击出现抢单对话框
        adapter.setOnItemClickListener(new HomeRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                entity = allAllList.get(position);
                WindowManager manager = getWindowManager();
                int wid = manager.getDefaultDisplay().getWidth();
                int hei = manager.getDefaultDisplay().getHeight();
                // 1.context 2.屏幕宽度 3.高度 4.是否是抢单dialog 5.数据
                // 如果没有正在显示的对话框
                if (orderPushDialog != null) {
                    orderPushDialog.dismiss();
                    orderPushDialog = null;
                }
                if (orderDialog != null) {
                    orderDialog.dismiss();
                    orderDialog = null;
                }
                if (helpOrderDialog != null) {
                    helpOrderDialog.dismiss();
                    helpOrderDialog = null;
                }

                orderDialog = new OrderInforDialog(HomeActivity.this, wid, hei, true, entity, MLATITUDE, MLONGTITUDE);
                orderDialog.setOnShowListener(showListener);
                orderDialog.setOnDismissListener(dismissListener);

                if (orderDialog != null && !orderDialog.isShowing()) {
                    orderDialog.show();
                    orderDialog.setOnOrderClickListener(new OrderInforDialog.OnOrderClickListener() {
                        @Override
                        public void onOrderclick(View view, boolean isKnoic, OrderList.DataMapEntity.OrdersEntity bean) {
                            orderDialog.dismiss();
                            switch (entity.getOrder_status()) {
                                case "0":
                                    Toast.makeText(HomeActivity.this, "订单已被用户取消", Toast.LENGTH_SHORT).show();
                                    break;
                                case "1":
                                    type = true;
                                    new Thread(getOrderTask).start(); // 抢单
                                    break;
                                case "2":
                                    Toast.makeText(HomeActivity.this, "订单已被其他匠人受理", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    break;

                            }
                        }
                    });
                }
            }
        });

        // 设置用户属性
        if (userInfo != null) {
            setUserInfo();
//            // 设置头像
//            final String imgurl = AppApi.ORDER_IMG + userInfo.getDataMap().getAvatar() + "_200.jpg";
//            imageHandler = new Handler();
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//                    OkHttpClient client = new OkHttpClient();
//                    try {
//                        client = HttpsController.setCertificates(HomeActivity.this,
//                                HomeActivity.this.getAssets().open("zhenjren.cer"));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        Request request = new Request.Builder().url(imgurl).build();
//                        Response response = client.newCall(request).execute();
//                        if (response.isSuccessful()) {
//                            InputStream is = response.body().byteStream();
//                            final Bitmap bm = BitmapFactory.decodeStream(is);
//                            imageHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    home_userHead_Iv.setImageBitmap(bm);
//                                }
//                            });
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
//
////            Picasso.with(this).load(imgurl).error(R.mipmap.default_head).into(home_userHead_Iv);
//            // 设置姓名
//            home_userName_Tv.setText(userInfo.getDataMap().getReal_name());
//            // 设置评分
//            home_userRating_Rb.setRating(userInfo.getDataMap().getStar());
//            // 设置已完成订单数
//            homeUserOrder_Tv.setText(getResources().getString(R.string.home_orders) + userInfo.getDataMap().getMonth_order());

            // 极光推送设置别名
            List<String> intTag = userInfo.getDataMap().getScope();
            tags = new HashSet<>();
            for (int i = 0; i < intTag.size(); i++) {
                tags.add(String.valueOf(intTag.get(i)));
            }
            // 极光推送设置未开工别名
            notWorktags = new HashSet<>();
            notWorktags.add("");

            // 初始化开工状态
            if (userInfo.getDataMap().getStatus() == 1) {
                homeOrder_Rl.setVisibility(View.VISIBLE);
                ISWORKING = true;
                JPushInterface.resumePush(HomeActivity.this);
                JPushInterface.setAliasAndTags(this, userInfo.getDataMap().getWorker_no(), tags, mAliasCallback); // 设置极光推送的别名和标签
                home_startWork_Btn.setText(getResources().getString(R.string.home_finish_work));
                home_Rl.setVisibility(View.GONE);
                homeOrder_Rv.setVisibility(View.VISIBLE);
                PreferenceUtil.savePerfs(HomeActivity.this, AppConstants.WORKERSSP,
                        AppConstants.ISSTARTWORKER, true);//记录开工状态
                new Thread(orderListTask).start();
                Intent receiver = new Intent(); // 发送广播启动发送地理位置的服务
                receiver.setAction("com.hengxun.builder.service");
                receiver.putExtra("state", 1);
                sendBroadcast(receiver);

            } else if (userInfo.getDataMap().getStatus() == 2) {
                ISWORKING = false;
                homeOrder_Rv.setVisibility(View.GONE);
                home_Rl.setVisibility(View.VISIBLE);
                home_startWork_Btn.setText(getResources().getString(R.string.home_start_work));
                JPushInterface.stopPush(this);
                PreferenceUtil.savePerfs(HomeActivity.this, AppConstants.WORKERSSP,
                        AppConstants.ISSTARTWORKER, false);//记录完工状态
                Intent receiver = new Intent(); // 发送广播启动发送地理位置的服务
                receiver.setAction("com.hengxun.builder.service");
                receiver.putExtra("state", 2);
                sendBroadcast(receiver);
            }
        }

        /*// 在开工完工的时候 操作service
        Intent intent = new Intent(HomeActivity.this, LocationService.class);
        // 获取匠人开工状态
        if (userInfo.getDataMap().getStatus() == 1) {
            PreferenceUtil.savePerfs(HomeActivity.this, AppConstants.WORKERSSP, AppConstants.ISSTARTWORKER, true);//记录开工完工状态
            homeOrder_Rl.setVisibility(View.VISIBLE);
            ISWORKING = true;
            home_startWork_Btn.setText(getResources().getString(R.string.home_finish_work));
            home_Rl.setVisibility(View.GONE);
            homeOrder_Rv.setVisibility(View.VISIBLE);
            JPushInterface.resumePush(this);
            setStyleBasic();
            JPushInterface.setAliasAndTags(this, userInfo.getDataMap().getWorker_no(), tags, mAliasCallback); // 设置极光推送的别名和标签
            new Thread(orderListTask).start();
        } else {
            PreferenceUtil.savePerfs(HomeActivity.this, AppConstants.WORKERSSP, AppConstants.ISSTARTWORKER, false);//记录开工完工状态
            ISWORKING = false;
            home_startWork_Btn.setText(getResources().getString(R.string.home_start_work));
            JPushInterface.setAliasAndTags(this, "", notWorktags, mAliasCallback);
            JPushInterface.stopPush(this);
            stopService(intent); // 收工的时候停止服务
        }*/

        new Thread() {
            @Override
            public void run() {
                super.run();
                if (userInfo != null) {
                    sendRegistrationId(userInfo.getDataMap().getUserId(), userInfo.getDataMap().getToken());
                }
            }
        }.start();

    }

    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 向服务器发送registrationId
     **/
    private void sendRegistrationId(int userId, String token) {
        String url = AppApi.REGISTIONID;
        String registrationId = PreferenceUtil.readString(HomeActivity.this, AppConstants.WORKERSSP, AppConstants.REGISTRATIONID);
        JSONObject object = new JSONObject();
        try {
            object.put(AppConstants.REGISTRATIONID, registrationId);
            object.put(AppConstants.WORKER_ID, userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, object.toString());
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(body).addHeader(AppConstants.TOKEN, null != token ? token : "").build();
        Response response = null;
        Message msg = Message.obtain();
        try {
            response = okHttpClient.newCall(request).execute();
            String s = response.body().string();
            if (response.isSuccessful()) {
                if (401 != response.code()) {
//                    msg.what = 200;
                }
            } else {
                /* geanwen向服务器注册极光失败 */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        handler.sendMessage(msg);

    }

    /**
     * 设置通知提示方式 - 基础属性
     */
    private void setStyleBasic() {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
        builder.statusBarDrawable = R.mipmap.applogo;
        builder.notificationFlags = Notification.FLAG_INSISTENT;  //设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
        JPushInterface.setPushNotificationBuilder(1, builder);
    }

    /**
     * 改变顶部toolbar状态
     */
    @Override
    protected void showToolBar(String titleName, boolean isShow, Activity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (isShow) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
        // → 个人页面
        ImageView toolbar_back_Ib = (ImageView) activity.findViewById(R.id.toolbar_left_Ib);
        toolbar_back_Ib.setImageResource(R.mipmap.home_person);
        toolbar_back_Ib.setOnClickListener(this);

        TextView baseactivity_title_TV = (TextView) activity.findViewById(R.id.toolbarTitle_Tv);
        baseactivity_title_TV.setText(titleName);
        // 地球图标 → 地图页面
        ImageView toolbar_right_Iv = (ImageView) activity.findViewById(R.id.toolbar_right_Iv);
        toolbar_right_Iv.setImageResource(R.mipmap.home_world);
        toolbar_right_Iv.setOnClickListener(this);
    }

    @Override
    public void exitBy2Click() {
        super.exitBy2Click();
    }

//    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
//        switch (event.getKeyCode()) {
//            case KeyEvent.KEYCODE_BACK: {
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     * 极光设置别名和标签的回调
     */
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    for (Iterator<String> it = tags.iterator(); it.hasNext(); ) {
                        logs += "====" + it.next();
                    }
                    Log.i("qwer", logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i("qwer", logs);
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.i("qwer", logs);
            }
        }

    };

    /**
     * ZY 2016年5月19日17:44:17
     * 设置dialog在显示的时候 收工按钮隐藏
     */
    private DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            home_startWork_Btn.setVisibility(View.VISIBLE);
        }
    };


    /**
     * ZY 2016年5月19日17:44:17
     * 设置dialog在消失的时候 收工按钮显示
     */
    private DialogInterface.OnShowListener showListener = new DialogInterface.OnShowListener() {
        @Override
        public void onShow(DialogInterface dialog) {
            home_startWork_Btn.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onClick(View v) {
        Bundle data = new Bundle();
        data.putSerializable("userInfo", userInfo);
        switch (v.getId()) {
            // 进入个人中心
            case R.id.toolbar_left_Ib:
                Intent toMy = new Intent(this, MyInfoActivity.class);
                orderStart = new LatLng(MLATITUDE, MLONGTITUDE);
                if (orderStart == null) {
                    Toast.makeText(this, "正在定位, 请稍等", Toast.LENGTH_SHORT).show();
                } else {
                    toMy.putExtra("start", orderStart); // 订单起始位置
                    toMy.putExtras(data);
                    startActivity(toMy);
                }
                break;

            // 开工或收工
            case R.id.home_startWork_Btn:
                changeWorking(); // 切换开工完工状态
                break;

            // 进入地图页面
            case R.id.toolbar_right_Iv:
                Intent toMap = new Intent(this, OrderMapActivity.class);
                toMap.putExtra("isworking", ISWORKING);
                toMap.putExtras(data);
                startActivity(toMap);
                break;

            case R.id.homeCancel_Tv: // 取消收工
                dialog.dismiss();
                break;

            case R.id.homeSure_Tv:   // 确定收工
                Intent receiver = new Intent(); // 发送广播启动发送地理位置的服务
                receiver.setAction("com.hengxun.builder.service");
                receiver.putExtra("state", 2);
                sendBroadcast(receiver);

                PreferenceUtil.savePerfs(HomeActivity.this, AppConstants.WORKERSSP, AppConstants.ISSTARTWORKER, false);//记录开工完工状态
                dialog.dismiss();
                JPushInterface.stopPush(this);
                ISWORKING = false;
                new Thread(changeWorkTask).start(); // 切换开工完工状态
                // 改变UI
                home_Rl.setVisibility(View.VISIBLE);
                homeOrder_Rv.setVisibility(View.GONE);
                homeOrder_Rl.setVisibility(View.GONE);
                homeNoOrder_Iv.setVisibility(View.GONE);
                home_startWork_Btn.setText(getResources().getString(R.string.home_start_work));
                break;
        }
    }

    /**
     * 收工的时候弹出对话框 匠人确认收工后才能收工
     */
    private void showExitDialog() {
        // 使用builder创建对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_finish_work, null);
        builder.setView(view);
        dialog = builder.create();

        // 设置对话框的view
        TextView homeCancel_Tv = (TextView) view.findViewById(R.id.homeCancel_Tv); // 取消
        TextView homeSure_Tv = (TextView) view.findViewById(R.id.homeSure_Tv); // 确定
        homeSure_Tv.setOnClickListener(this);
        homeCancel_Tv.setOnClickListener(this);
        dialog.show();
    }

    /**
     * 开工和完工状态切换
     */
    private void changeWorking() {
        // 判断是否在工作 切换布局
        if (ISWORKING) { // 正在工作
            // 弹出对话框
            showExitDialog();
        } else { // 没在工作
            // 改变btn的显示
            PreferenceUtil.savePerfs(HomeActivity.this, AppConstants.WORKERSSP, AppConstants.ISSTARTWORKER, true);//记录开工完工状态

            home_startWork_Btn.setText(getResources().getString(R.string.home_finish_work));
            home_Rl.setVisibility(View.GONE);
            homeOrder_Rv.setVisibility(View.VISIBLE);
            homeOrder_Rl.setVisibility(View.VISIBLE);
            JPushInterface.resumePush(this);
            JPushInterface.setAliasAndTags(this, userInfo.getDataMap().getWorker_no(), tags, mAliasCallback); // 设置极光推送的别名和标签
            setStyleBasic();

            Intent receiver = new Intent(); // 发送广播启动发送地理位置的服务
            receiver.setAction("com.hengxun.builder.service");
            receiver.putExtra("state", 1);
            sendBroadcast(receiver);

            ISWORKING = true; // 改变工作状态
            new Thread(changeWorkTask).start(); // 切换开工完工状态
            new Thread(orderListTask).start();
        }
    }

    private void initLocation(int span) {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span = 0; // 8秒定一次位
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    //定位后查询地址  保存到缓存
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onUserEvent(LatLng latlng) {
        if (latlng != null) {
            //经纬度查询地址
            GeoCoder coder = GeoCoder.newInstance();
            coder.reverseGeoCode(new ReverseGeoCodeOption().location(latlng));
            coder.setOnGetGeoCodeResultListener(this);
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (null != reverseGeoCodeResult && null != reverseGeoCodeResult.getAddressDetail()
                && null != reverseGeoCodeResult.getAddressDetail().city
                ) {
            String city = reverseGeoCodeResult.getAddressDetail().city;
            PreferenceUtil.savePerfs(HomeActivity.this, AppConstants.WORKERSSP, AppConstants.CITY, city);
        }
    }

    /**
     * 定位获取位置信息监听
     */
    private class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MLATITUDE = bdLocation.getLatitude(); // 获取经度
            MLONGTITUDE = bdLocation.getLongitude(); // 获取纬度

            // ZY 2016年5月20日15:15:51
            // 没开启定位
            if (null == bdLocation.getAddrStr()) {
                if (null == finishActivity) {
                    Intent intent = new Intent(HomeActivity.this, ErrorActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                LatLng latLng = new LatLng(MLATITUDE, MLONGTITUDE);
                EventBus.getDefault().post(latLng);
                PreferenceUtil.savePerfs(HomeActivity.this, SharedPrefer.MLATITUDE, SharedPrefer.MLATITUDE, String.valueOf(MLATITUDE));
                PreferenceUtil.savePerfs(HomeActivity.this, SharedPrefer.MLONGTITUDE, SharedPrefer.MLONGTITUDE, String.valueOf(MLONGTITUDE));
            }
        }
    }

    /**
     * ZY 2016年5月17日17:35:38
     * 初始化用户信息
     */
    private void setUserInfo() {
        // 设置头像
        final String imgurl = AppApi.ORDER_IMG + userInfo.getDataMap().getAvatar() + "_200.jpg";
        imageHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                super.run();
                OkHttpClient client = new OkHttpClient();
                try {
                    client = HttpsController.setCertificates(HomeActivity.this,
                            HomeActivity.this.getAssets().open("zhenjren.cer"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Request request = new Request.Builder().url(imgurl).build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        InputStream is = response.body().byteStream();
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        imageHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                home_userHead_Iv.setImageBitmap(bm);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

//            Picasso.with(this).load(imgurl).error(R.mipmap.default_head).into(home_userHead_Iv);
        // 设置姓名
        home_userName_Tv.setText(userInfo.getDataMap().getReal_name());
        // 设置评分
        home_userRating_Rb.setRating(userInfo.getDataMap().getStar());
        // 设置已完成订单数
        homeUserOrder_Tv.setText(getResources().getString(R.string.home_orders) + userInfo.getDataMap().getMonth_order());
    }

    AlertDialog startWorkDialog = null; // bugid=3;
    /**
     * 将订单集合装入适配器
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            switch (msg.what) {
                case 15: // 抢单 成功
                    Intent intent = new Intent(HomeActivity.this, OrderDetailsActivity.class);
                    Bundle data = new Bundle();

                    if (orderPushDialog != null) {
                        orderPushDialog.dismiss();
                        orderPushDialog = null;
                    }
                    if (orderDialog != null) {
                        orderDialog.dismiss();
                        orderDialog = null;
                    }
                    if (helpOrderDialog != null) {
                        helpOrderDialog.dismiss();
                        helpOrderDialog = null;
                    }
                    if (!type) {
                        if (orderPush == null || orderPush.getDataMap() == null) {
                            Toast.makeText(HomeActivity.this, "数据异常，请刷新列表重试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        orderPush.getDataMap().setOrder_status(2);
                        data.putSerializable("orderPush", orderPush);
                        data.putSerializable("userInfo", userInfo);
                        LatLng latLng = new LatLng(Double.parseDouble(String.valueOf(orderPush.getDataMap().getOrder_x())),
                                Double.parseDouble(String.valueOf(orderPush.getDataMap().getOrder_y())));
                        data.putParcelable("end", latLng);
                        intent.putExtras(data);
                        startActivity(intent);
                        Toast.makeText(HomeActivity.this, "抢单成功", Toast.LENGTH_SHORT).show();
                        typeHome = 1;
                        page = 1;
                        new Thread(orderListTask).start();
                    } else {
                        if (entity == null) {
                            Toast.makeText(HomeActivity.this, "数据异常，请刷新列表重试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        entity.setOrder_status("2");
                        data.putSerializable("order", entity);
                        LatLng latLng = new LatLng(Double.parseDouble(entity.getX()), Double.parseDouble(entity.getY()));
                        data.putSerializable("userInfo", userInfo);
                        data.putParcelable("end", latLng);
                        intent.putExtras(data);
                        startActivity(intent);
                        Toast.makeText(HomeActivity.this, "抢单成功", Toast.LENGTH_SHORT).show();
                        typeHome = 1;
                        page = 1;
                        new Thread(orderListTask).start();
                    }
                    Intent receiver = new Intent(); // 发送广播启动发送地理位置的服务
                    receiver.setAction("com.hengxun.builder.service");
                    sendBroadcast(receiver);
                    break;

                case 1: // 抢单 失败
                    if (orderPushDialog != null) {
                        orderPushDialog.dismiss();
                        orderPushDialog = null;
                    }
                    if (orderDialog != null) {
                        orderDialog.dismiss();
                        orderDialog = null;
                    }
                    if (helpOrderDialog != null) {
                        helpOrderDialog.dismiss();
                        helpOrderDialog = null;
                    }
                    Toast.makeText(HomeActivity.this, "订单已被其他匠人受理。", Toast.LENGTH_SHORT).show();
                    break;

                case 2: // 取订单列表 刷新
                    if (typeHome == 1) {
                        adapter.addData(allList);
                        homeOrder_Rl.setRefreshing(false);
                    } else if (typeHome == 2) {
                        adapter.addDataLoad(allList);
                        homeOrder_Rl.setLoading(false);
                    }
                    // 当开工之后
                    if (ISWORKING) {
                        // 当刚进入或者刷新时 刚获取到的数据为空
                        if ((page == 1) && (list == null || list.size() == 0)) {
                            homeNoOrder_Iv.setVisibility(View.VISIBLE);
                            homeOrder_Rv.setVisibility(View.GONE);
                        } else {
                            homeNoOrder_Iv.setVisibility(View.GONE);
                            homeOrder_Rv.setVisibility(View.VISIBLE);
                        }
                    }
                    break;

                case 3: // 取订单列表失败
                    if (page > 1) {
                        page--;
                    }
                    homeOrder_Rl.setRefreshing(false);
                    if (allList == null || allList.size() == 0) {
                        homeNoOrder_Iv.setVisibility(View.VISIBLE);
                    }
                    break;

                case 4: // 取消订单
                    final AlertDialog.Builder cancel = new AlertDialog.Builder(HomeActivity.this);
                    cancel.setTitle("订单被取消");
                    cancel.setMessage("订单号" + orderPush.getDataMap().getOrder_id() + orderPush.getDataMap().getService().getChildren().get(0).getService_name() + "已经被取消");
                    cancel.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    cancel.setCancelable(false);
                    cancel.create().show();
                    break;

                case 5:  // 新订单对话框
                    WindowManager manager = getWindowManager();
                    int wid = manager.getDefaultDisplay().getWidth();
                    int hei = manager.getDefaultDisplay().getHeight();
                    // 只显示最早打开对话框
                    if (orderPushDialog != null && orderPushDialog.isShowing()) {
                        return;
                    }
                    if (orderDialog != null && orderDialog.isShowing()) {
                        return;
                    }
                    orderPushDialog = new OrderPushDialog(HomeActivity.this, wid, hei, 1, orderPush, MLATITUDE, MLONGTITUDE);
                    orderPushDialog.setOnShowListener(showListener);
                    orderPushDialog.setOnDismissListener(dismissListener);
                    if (orderPushDialog != null && !orderPushDialog.isShowing()) {
                        orderPushDialog.show();
                    }
                    orderPushDialog.setOnOrderClickListener(new OrderPushDialog.OnOrderClickListener() {
                        @Override
                        public void onOrderclick(View view, int isKnoic, OrderPush bean) {
                            type = false;
                            switch (isKnoic) {
                                case 1:
                                    new Thread(getOrderTask).start(); // 抢单
                                    break;

                                case 2:

                                case 3:
                                    new Thread(getBacklogTask).start(); // 派单和抢险抢修
                                    break;
                            }
                        }
                    });
                    break;

                case 6: // 开工
                    final AlertDialog.Builder startWork = new AlertDialog.Builder(HomeActivity.this);
                    startWork.setTitle("订单开工");
                    startWork.setMessage("订单号" + orderPush.getDataMap().getOrder_id() + orderPush.getDataMap().getService().getChildren().get(0).getService_name() + "已经开工");
                    startWork.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startWorkDialog = null;//bugid=3;
                            Intent start = new Intent(HomeActivity.this, OrderDetailsActivity.class);
                            Bundle startBundle = new Bundle();
                            startBundle.putSerializable("orderPush", orderPush);
                            startBundle.putParcelable("start", orderStart);
                            startBundle.putSerializable("userInfo", userInfo);
                            start.putExtras(startBundle);
                            startActivity(start);
                        }
                    });
                    if (startWorkDialog == null) {//bugid=3;

                        startWork.setCancelable(false);
                        startWorkDialog = startWork.create();
                        startWorkDialog.show();
                    }
                    break;

                case 7: // 代用户付款
                    Intent toPay = new Intent(HomeActivity.this, OrderDetailsActivity.class);
                    Bundle datas = new Bundle();
                    datas.putSerializable("orderPush", orderPush);
//                    toPay.putExtra("orderId", String.valueOf(orderPush.getDataMap().getOrder_id()));
//                    toPay.putExtra("type", 1);
//                    toPay.putExtra("notifactionId", orderPush.getNotifactionId());
                    toPay.putExtras(datas);
                    startActivity(toPay);
                    break;

                case 10:
                    Toast.makeText(HomeActivity.this, "抢单失败", Toast.LENGTH_SHORT).show();
                    break;

                case 20:
                    homeUserOrder_Tv.setText(getResources().getString(R.string.home_orders) + userInfo.getDataMap().getMonth_order());
                    break;

                case 25: // 派单 抢单成功

                    if (orderPushDialog != null) {
                        orderPushDialog.dismiss();
                        orderPushDialog = null;
                    }
                    if (orderDialog != null) {
                        orderDialog.dismiss();
                        orderDialog = null;
                    }
                    if (helpOrderDialog != null) {
                        helpOrderDialog.dismiss();
                        helpOrderDialog = null;
                    }
                    if (offerOrders != null && offerOrders.size() > 0) {
                        Intent intent2 = new Intent(HomeActivity.this, OfferOrderDetailsActivity.class);
                        Bundle data2 = new Bundle();
                        offerOrders.get(0).setOrder_status(String.valueOf(2));
                        data2.putSerializable("orderPush", offerOrders.get(0));
                        data2.putSerializable("userInfo", userInfo);
                        LatLng latLng = new LatLng(Double.parseDouble(String.valueOf(offerOrders.get(0).getX())),
                                Double.parseDouble(String.valueOf(offerOrders.get(0).getY())));
                        data2.putParcelable("end", latLng);
                        intent2.putExtras(data2);
                        startActivity(intent2);
                        Toast.makeText(HomeActivity.this, "抢单成功", Toast.LENGTH_SHORT).show();
//                        new Thread(backlogTask).start();
                    } else {
                        Intent intent3 = new Intent(HomeActivity.this, OrderDetailsActivity.class);
                        Bundle data3 = new Bundle();

                        orderPush.getDataMap().setOrder_status(2);
                        data3.putSerializable("orderPush", orderPush);
                        LatLng latLng = new LatLng(orderPush.getDataMap().getX(),
                                orderPush.getDataMap().getY());
                        data3.putSerializable("userInfo", userInfo);
                        data3.putParcelable("end", latLng);
                        intent3.putExtras(data3);
                        startActivity(intent3);
                        Toast.makeText(HomeActivity.this, "抢单成功", Toast.LENGTH_SHORT).show();
                    }
                    Intent receiver1 = new Intent(); // 发送广播启动发送地理位置的服务
                    receiver1.setAction("com.hengxun.builder.service");
                    sendBroadcast(receiver1);
                    break;

                case 30: // 抢险抢修
                    if (userInfo == null) {
                        Toast.makeText(HomeActivity.this, "请重新登录", Toast.LENGTH_SHORT).show();
                        Intent notLogin = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(notLogin);
                        finish();
                        return;
                    }
                    if (helpOrderDialog != null) {
                        helpOrderDialog.dismiss();
                        helpOrderDialog = null;
                    }
                    if (orderPushDialog != null) {
                        orderPushDialog.dismiss();
                        orderPushDialog = null;
                    }
                    if (orderDialog != null) {
                        orderDialog.dismiss();
                        orderDialog = null;
                    }
                    if (emerOrders != null && emerOrders.size() > 0) {
                        Intent intent6 = new Intent(HomeActivity.this, EmerDetailsActivity.class);
                        Bundle data6 = new Bundle();
                        emerOrders.get(0).setOrder_status(String.valueOf(2));
                        data6.putSerializable("order", emerOrders.get(0));
                        data6.putSerializable("userInfo", userInfo);
                        LatLng latLng = new LatLng(Double.parseDouble(String.valueOf(emerOrders.get(0).getX())),
                                Double.parseDouble(String.valueOf(emerOrders.get(0).getY())));
                        data6.putParcelable("end", latLng);
                        intent6.putExtras(data6);
                        startActivity(intent6);
                        Toast.makeText(HomeActivity.this, "接单成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent5 = new Intent(HomeActivity.this, OfferOrderDetailsActivity.class);
                        Bundle data5 = new Bundle();
                        helpOrder.getDataMap().getDetail().setOrder_status("2");
                        data5.putSerializable("order", helpOrder.getDataMap().getDetail());
                        LatLng latLng = new LatLng(Double.parseDouble(helpOrder.getDataMap().getDetail().getX()),
                                Double.parseDouble(helpOrder.getDataMap().getDetail().getY()));
                        data5.putSerializable("userInfo", userInfo);
                        data5.putParcelable("end", latLng);
                        intent5.putExtras(data5);
                        startActivity(intent5);
                        Toast.makeText(HomeActivity.this, "接单成功", Toast.LENGTH_SHORT).show();
                    }
                    Intent receiver5 = new Intent(); // 发送广播启动发送地理位置的服务
                    receiver5.setAction("com.hengxun.builder.service");
                    sendBroadcast(receiver5);
                    break;
                case 50: // 匠人有未付款的订单 先停止推送  再跳转到支付界面
//                    JPushInterface.stopPush(HomeActivity.this);

//                    Intent toPay = new Intent(HomeActivity.this, PayActivity.class);
//                    Bundle payBundle = new Bundle();
//                    payBundle.putSerializable("orders", (Serializable) toPayOrders);
//                    toPay.putExtra("type", 2);
//                    toPay.putExtras(payBundle);
//                    startActivity(toPay);

//                    Intent i = new Intent(HomeActivity.this, NotPayDetailsActivity.class);
//                    Bundle payBundle = new Bundle();
//                    payBundle.putSerializable("order", (Serializable) toPayOrders);
//                    i.putExtras(payBundle);
//                    i.putExtra("type", 2);
//                    startActivity(i);
                    break;

                case 51: // 匠人有未付款的订单(网络异常)
//                    finish();
                    break;

                case 60: // 匠人有未接受的派单
                    WindowManager manager3 = getWindowManager();
                    int wid3 = manager3.getDefaultDisplay().getWidth();
                    int hei3 = manager3.getDefaultDisplay().getHeight();

                    int knoic = 2;
                    orderPushDialog = new OrderPushDialog(HomeActivity.this, wid3, hei3, knoic, offerOrders.get(0), MLATITUDE, MLONGTITUDE);
                    orderPushDialog.setOnShowListener(showListener);
                    orderPushDialog.setOnDismissListener(dismissListener);
                    if (orderPushDialog != null && !orderPushDialog.isShowing()) {
                        orderPushDialog.show();
                    }
                    orderPushDialog.setOnOrderClickListener(new OrderPushDialog.OnOrderClickListener() {
                        @Override
                        public void onOrderclick(View view, int isKnoic, OrderPush bean) {
                            type = false;
                            switch (isKnoic) {
                                case 1:
                                    new Thread(getOrderTask).start(); // 抢单
                                    break;

                                case 2:

                                case 3:
                                    new Thread(getBacklogTask).start(); // 派单和抢险抢修
                                    break;
                            }
                        }
                    });
                    notBack(orderPushDialog);
                    break;

                case 70:
                    WindowManager manager4 = getWindowManager();
                    int wid4 = manager4.getDefaultDisplay().getWidth();
                    int hei4 = manager4.getDefaultDisplay().getHeight();

                    int knoic1 = 2;
                    helpOrderDialog = new HelpOrderDialog(HomeActivity.this, wid4, hei4, knoic1, emerOrders.get(0), MLATITUDE, MLONGTITUDE);
                    helpOrderDialog.setOnShowListener(showListener);
                    helpOrderDialog.setOnDismissListener(dismissListener);
                    if (helpOrderDialog != null && !helpOrderDialog.isShowing()) {
                        helpOrderDialog.show();
                    }

                    helpOrderDialog.setOnOrderClickListener(new HelpOrderDialog.OnOrderClickListener() {
                        @Override
                        public void onOrderclick(View view, int isKnoic, HelpOrder bean) {
                            type = false;
                            new Thread(getHelpOrderTask).start(); // 派单和抢险抢修
                        }
                    });
                    notBack(helpOrderDialog);
//                    helpOrderDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                        @Override
//                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//                                return true;
//                            }
//                            return true;
//                        }
//                    });
                    break;

                case 99: // 自动更新 打开浏览器跳转URL
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setMessage("有新版本, 请更新后使用");
                    builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Uri uri = Uri.parse(version.getDataMap().getPath());
                            final Intent update = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(update);
                        }
                    });
                    builder.setCancelable(false);
                    builder.create().show();
                    break;

                case 200:

                    break;
            }
        }
    };

    /**
     * 切换匠人的开工完工状态
     */
    Runnable changeWorkTask = new Runnable() {
        @Override
        public void run() {
            // 传的是需要更改的状态
            if (ISWORKING) {
                param = 1; // 收工
                JPushInterface.resumePush(HomeActivity.this);
                JPushInterface.setAliasAndTags(HomeActivity.this, userInfo.getDataMap().getWorker_no(), tags, mAliasCallback); // 设置极光推送的别名和标签
            } else {
                param = 2; // 开工
                JPushInterface.setAliasAndTags(HomeActivity.this, "", notWorktags, mAliasCallback);
                JPushInterface.stopPush(HomeActivity.this);
            }

            // 匠人开工完工状态修改
            OkHttpClientManager.Param params[] = new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param(AppConstants.STATUS, String.valueOf(param))
                    , new OkHttpClientManager.Param(AppConstants.WORKER_NO, userInfo.getDataMap().getWorker_no())
            };
            Response status;
            try {
                status = OkHttpClientManager.post(AppApi.DUTYSTATUS, HomeActivity.this, params[0], params[1]);
                String result = status.body().string();
                if (status.code() != 401) {

                } else {
                    errToken();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 改变订单状态--抢单
     *
     * @param ORDER_STATUS 订单状态 2 抢单
     */
    Runnable getOrderTask = new Runnable() {
        @Override
        public void run() {
            Message message = Message.obtain();
            HashMap<String, String> map = new HashMap<>();
            // 如果是支付的任务  直接返回
            if ((entity != null && entity.getOrder_status().equals("6"))
                    || (orderPush != null && orderPush.getDataMap().getOrder_status() == 6)) {
                return;
            }
            // 判断订单类型来源
            if (type) {
                if (entity != null && !"1".equals(entity.getOrder_status())) {
                    message.what = 1;
                    handler.sendMessage(message);
                    return;
                }
                map.put(AppConstants.ORDER_ID, entity.getOrder_id());
            } else {
                if (orderPush.getDataMap().getOrder_status() == 22) {
                    message.what = 2;
                    handler.sendMessage(message);
                    return;
                }

                if (1 != orderPush.getDataMap().getOrder_status()) {
                    message.what = 1;
                    handler.sendMessage(message);
                    return;
                }
                map.put(AppConstants.ORDER_ID, String.valueOf(orderPush.getDataMap().getOrder_id()));
            }
            map.put(AppConstants.ORDER_STATUS, "2");
            map.put(AppConstants.PARAM, userInfo.getDataMap().getWorker_no());
            String url = UrlUtils.addParams(AppApi.ORDERSTATUS, map);
            Response status;
            Gson gson = new Gson();
            try {
                status = OkHttpClientManager.getAsyn(url, HomeActivity.this);
                String result = status.body().string();
                GetOrderStatus orderStatus = gson.fromJson(result, GetOrderStatus.class);
                // 判断token是否失效
                if (status.code() != 401) {
                    // 判断是否抢到订单
                    if (orderStatus.getCode() == 200) {
                        message.what = 15;
                    } else if (orderStatus.getCode() == 401) {
                        errToken();
                    } else {
                        message.what = 1; // 抢单失败
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 10;
            }
            handler.sendMessage(message);
        }
    };

    /**
     * 取所有可接的订单列表
     */
    Runnable orderListTask = new Runnable() {
        @Override
        public void run() {
            Message message = Message.obtain();
            message.what = -1;
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.WORKER_NO, userInfo.getDataMap().getWorker_no());
                map.put(AppConstants.TYPE, String.valueOf(2)); // type 为2 是该匠人所有可抢的订单
                map.put(AppConstants.PAGENO, String.valueOf(page));
                Date now = new Date();
                String time = String.valueOf(now.getTime());
                map.put(AppConstants.TIMESTAMP, time); // 当前时间时间戳
                String url = UrlUtils.addParams(AppApi.ORDERLIST, map); // 拼接参数
                Response response = OkHttpClientManager.getAsyn(url, HomeActivity.this);
                if (response.code() == 200) {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    OrderList orderList = gson.fromJson(result, OrderList.class);
                    if (orderList.getCode() != 401) {
                        OrderList.DataMapEntity entity = orderList.getDataMap();
                        list = entity.getOrders();
                        message.what = 2;
                        if (typeHome == 1) {//刷新
                            allList.clear();
                            allList.addAll(list);
                            allAllList.clear();
                            allAllList.addAll(list);
                        } else if (typeHome == 2) {//加载
                            allList.clear();
                            allList.addAll(list);
                            allAllList.addAll(list);
                        }
                    } else if (orderList.getCode() == 401) {
                        errToken();
                    }
                } else {
                    message.what = 3;
                }
            } catch (Exception e) {
                message.what = 3;
                e.printStackTrace();
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
            Message message = Message.obtain();
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.ORDER_ID, String.valueOf(orderId));
                String url = UrlUtils.addParams(AppApi.DETAIL, map); // 拼接参数
                Response response = OkHttpClientManager.getAsyn(url, HomeActivity.this);
                if (response.code() != 401) {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    orderPush = gson.fromJson(result, OrderPush.class);
                    if (orderPush.getCode() != 401) {
                        switch (orderPush.getDataMap().getOrder_status()) {
                            case 0: // 取消订单
                                message.what = 4;
                                break;

                            case 1: // 新订单
                                message.what = 5;
                                break;

                            case 4: // 开工
                                message.what = 6;
                                break;

//                    case 5: // 未付款
//                        message.what = 9;

                            case 6: // 替用户付款
                                message.what = 7;
                                break;

                            case 7: // 付款完成
                                message.what = 8;
                                break;
                        }
                    } else {
                        errToken();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            handler.sendMessage(message);
        }
    };

    /**
     * 未付款 派单列表
     */
    Runnable backlogTask = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> map = new HashMap<>();
            map.put(AppConstants.WORKER_ID, String.valueOf(userInfo.getDataMap().getUserId()));
            String url = UrlUtils.addParams(AppApi.BACKLOG, map); // 拼接参数
            Response response;
            Message message = Message.obtain();
            message.what = -1;
            try {
                response = OkHttpClientManager.getAsyn(url, HomeActivity.this);
                if (response.code() != 401) {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    BacklogOrder order = gson.fromJson(result, BacklogOrder.class);
                    if (order.getCode() != 401) {
                        BacklogOrder.DataMapEntity entity = order.getDataMap();
//                    PreferenceUtil.savePerfs(HomeActivity.this, AppConstants.WORKERSSP, AppConstants.ISTODAYORDERS, entity.isTodayOrders());
                        toPayOrders = entity.getToPayOrders();
                        offerOrders = entity.getOfferOrders();
                        emerOrders = entity.getOfferEmergencys();
                        if (toPayOrders != null && toPayOrders.size() > 0) {
                            message.what = 50;
                            message.arg1 = toPayOrders.size();
                        } else if (offerOrders != null && offerOrders.size() > 0) {
//                        ISOFFER = false;
                            message.what = 60;
                        } else if (emerOrders != null && emerOrders.size() > 0) {
//                        ISOFFER = false;
                            message.what = 70;
                        } else {

                        }
                    } else if (order.getCode() != 401) {
                        errToken();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
//                message.what = 51;
                return;
            }
            handler.sendMessage(message);
        }
    };

    /**
     * 抢抢险抢修的订单
     */
    Runnable getHelpOrderTask = new Runnable() {
        @Override
        public void run() {
            Message message = Message.obtain();
            try {
//                if (offerOrders == null || helpOrder == null) {
//                    message.what = 1;
//                    handler.sendMessage(message);
//                    return;
//                }
                JSONObject object = new JSONObject();
                String status;
                String orderid;
                if (helpOrder != null) {
                    status = String.valueOf(helpOrder.getDataMap().getDetail().getOrder_status());
                    orderid = String.valueOf(helpOrder.getDataMap().getDetail().getOrder_id());
                } else {
                    status = emerOrders.get(0).getOrder_status();
                    orderid = emerOrders.get(0).getOrder_id();
                }
                try {
                    object.put("type", status);
                    object.put("orderId", orderid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String token = PreferenceUtil.readString(HomeActivity.this, SharedPrefer.TOKEN, SharedPrefer.TOKEN);
                Headers.Builder builder = new Headers.Builder();
                builder.add("token", token != null ? token : "");
                Headers headers = builder.build();
                // 抢派单和抢险抢修的单
                String response = OkHttpClientManager.postHeadObjectAsString(AppApi.BACKLOG, headers, object);
                Gson gson = new Gson();
                PayCompleteInfo info = gson.fromJson(response, PayCompleteInfo.class);
                if (info.getCode() == 200) {
                    message.what = 30;
//                    if (offerOrders != null && offerOrders.size() > 0) { // 如果是派单
//                        offerOrders.remove(0);
//                    }
//                    ISOFFER = true;
                } else {
                    message.what = 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 1;
            }
            handler.sendMessage(message);
        }
    };

    /**
     * 抢派单的订单
     */
    Runnable getBacklogTask = new Runnable() {
        @Override
        public void run() {
            Message message = Message.obtain();
            try {
//                if (offerOrders == null || helpOrder == null) {
//                    message.what = 1;
//                    handler.sendMessage(message);
//                    return;
//                }
                JSONObject object = new JSONObject();
                String status;
                String orderid;
                if (orderPush != null) {
                    status = String.valueOf(orderPush.getDataMap().getOrder_status());
                    orderid = String.valueOf(orderPush.getDataMap().getOrder_id());
//                    orderPush = null;
                } else {
                    status = offerOrders.get(0).getOrder_status();
                    orderid = offerOrders.get(0).getOrder_id();
                }
                try {
                    object.put("type", status);
                    object.put("orderId", orderid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String token = PreferenceUtil.readString(HomeActivity.this, SharedPrefer.TOKEN, SharedPrefer.TOKEN);
                Headers.Builder builder = new Headers.Builder();
                builder.add("token", token != null ? token : "");
                Headers headers = builder.build();
                // 抢派单和抢险抢修的单
                String response = OkHttpClientManager.postHeadObjectAsString(AppApi.BACKLOG, headers, object);
                Gson gson = new Gson();
                PayCompleteInfo info = gson.fromJson(response, PayCompleteInfo.class);
                if (info.getCode() == 200) {
                    message.what = 25;
//                    if (offerOrders != null && offerOrders.size() > 0) { // 如果是派单
//                        offerOrders.remove(0);
//                    }
//                    ISOFFER = true;
                } else {
                    message.what = 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 1;
            }
            handler.sendMessage(message);
        }
    };

    /**
     * 自动更新
     *
     * @param os 0 IOS  1 android
     * @param type 0 工人端
     */
    Runnable versionTask = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> map = new HashMap<>();
            map.put(AppConstants.OS, "1");
            map.put(AppConstants.TYPE, "0");
            String url = UrlUtils.addParams(AppApi.VERSION, map);
            Response response;
            Message message = Message.obtain();
            try {
                response = OkHttpClientManager.getAsyn(url, HomeActivity.this);
                if (response.code() != 401) {
//                    Log.d("versionTask", response.body().string());
                    Gson gson = new Gson();
                    String result = response.body().string();
                    version = gson.fromJson(result, Version.class);
                    if (version.getCode() != 401) {
                        if (version.getDataMap().getVersion() != null &&
                                !version.getDataMap().getVersion().equals(AppUtils.getAppVersionName(getApplicationContext()))) {
                            message.what = 99;
                        } else {
                            message.what = 100;
                        }
                    } else if (version.getCode() == 401) {
                        errToken();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 100;
            }
            handler.sendMessage(message);
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        new Thread(loginTask).start();
    }

    /**
     * 更新匠人信息
     */
    Runnable loginTask = new Runnable() {

        @Override
        public void run() {
            Message msg = new Message();
            String account = PreferenceUtil.readString(HomeActivity.this,
                    SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);
            String psw = PreferenceUtil.readString(HomeActivity.this,
                    SharedPrefer.PSW, SharedPrefer.PSW);
            String token = PreferenceUtil.readString(HomeActivity.this, SharedPrefer.TOKEN, SharedPrefer.TOKEN);
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.USERNAME, account);
                map.put(AppConstants.PSW, MD5Util.getMD5String(psw).toLowerCase(Locale.getDefault()));
                map.put(AppConstants.OS, "2");
                map.put(AppConstants.TOKEN, token != null ? token : "");
                String url = UrlUtils.addParams(AppApi.LOGIN, map); // 拼接参数
                Response response = OkHttpClientManager.getAsyn(url, HomeActivity.this);
                if (response.code() != 401) {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    userInfo = gson.fromJson(result, UserInfo.class);
                    if (userInfo.getCode() == 401) {
                        errToken();
                        return;
                    }
                    String _token;
                    if (null != userInfo.getDataMap().getToken()) {
                        _token = userInfo.getDataMap().getToken();
                        PreferenceUtil.savePerfs(HomeActivity.this, SharedPrefer.TOKEN, SharedPrefer.TOKEN, _token);
                    } else {
                        _token = token;
                    }
                    if (userInfo.getCode() == 200) {
                        msg.what = UPDATEUSER;
                        handler.sendMessage(msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

//    /**
//     * 切换到主页后  刷新UI
//     * */
//    @Subscribe(threadMode = ThreadMode.BACKGROUND)
//    public void onEvent() {
//
//    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在后台线程执行
    public void onUserEvent(String str) {
        if (str.equals("123")) {
            if (ISWORKING) { // ZY 2016年5月19日13:05:20 开工之后才刷新
                new Thread(orderListTask).start(); // 获取匠人自己的订单列表
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(Integer orderId) {//走了两次 bugid=3;
        this.orderId = orderId;
        new Thread(pushOrderTask).start(); // 获取推送订单
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final HelpOrder helpOrder) {
        try {
            if (helpOrderDialog != null && helpOrderDialog.isShowing()) {
                return;
            }
            if (orderPushDialog != null) {
                orderPushDialog.dismiss();
                orderPushDialog = null;
            }
            if (orderDialog != null) {
                orderDialog.dismiss();
                orderDialog = null;
            }
            if (startWorkDialog != null) {
                startWorkDialog.dismiss();
                startWorkDialog = null;
            }

            this.helpOrder = helpOrder;

            WindowManager wm2 = getWindowManager();
            int wid2 = wm2.getDefaultDisplay().getWidth();
            int hei2 = wm2.getDefaultDisplay().getHeight();
//        // 只显示最早打开对话框
//        if (orderPushDialog != null && orderPushDialog.isShowing()) {
//            return;
//        }
//        if (orderDialog != null && orderDialog.isShowing()) {
//            return;
//        }

            double MLATITUDE1 = Double.parseDouble(PreferenceUtil.readString(HomeActivity.this, SharedPrefer.MLATITUDE, SharedPrefer.MLATITUDE));
            double MLONGTITUDE1 = Double.parseDouble(PreferenceUtil.readString(HomeActivity.this, SharedPrefer.MLONGTITUDE, SharedPrefer.MLONGTITUDE));

            helpOrderDialog = new HelpOrderDialog(HomeActivity.this, wid2, hei2, 1, helpOrder, MLATITUDE1, MLONGTITUDE1);
            helpOrderDialog.setOnShowListener(showListener);
            helpOrderDialog.setOnDismissListener(dismissListener);
            if (helpOrderDialog != null && !helpOrderDialog.isShowing()) {
                JPushInterface.clearNotificationById(HomeActivity.this, helpOrder.getNotifactionId());
                helpOrderDialog.show();
            }
            notBack(helpOrderDialog);
            helpOrderDialog.setOnOrderClickListener(new HelpOrderDialog.OnOrderClickListener() {
                @Override
                public void onOrderclick(View view, int isKnoic, HelpOrder bean) {
                    type = false;
                    new Thread(getHelpOrderTask).start(); // 抢险抢修
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final OrderPush orderPush) {
        switch (orderPush.getDataMap().getOrder_status()) {
            case 0: // 取消
                final AlertDialog.Builder cancel = new AlertDialog.Builder(HomeActivity.this);
                cancel.setTitle("订单被取消");
                cancel.setMessage("订单号" + orderPush.getDataMap().getOrder_id() + orderPush.getDataMap().getService().getChildren().get(0).getService_name() + "已经被取消");
                cancel.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                cancel.setCancelable(false);
                cancel.create().show();
                JPushInterface.clearNotificationById(HomeActivity.this, orderPush.getNotifactionId());
                break;

            case 1: // 新订单
                WindowManager wm = getWindowManager();
                int wid = wm.getDefaultDisplay().getWidth();
                int hei = wm.getDefaultDisplay().getHeight();
//                // 如果没有正在显示的对话框
//                if (orderPushDialog != null) {
//                    orderPushDialog.dismiss();
//                    orderPushDialog = null;
//                }
                // 只显示最早打开对话框
                if (orderPushDialog != null && orderPushDialog.isShowing()) {
                    return;
                }
                if (orderDialog != null && orderDialog.isShowing()) {
                    return;
                }
                this.orderPush = orderPush;
                double _MLATITUDE = Double.parseDouble(PreferenceUtil.readString(HomeActivity.this, SharedPrefer.MLATITUDE, SharedPrefer.MLATITUDE));
                double _MLONGTITUDE = Double.parseDouble(PreferenceUtil.readString(HomeActivity.this, SharedPrefer.MLONGTITUDE, SharedPrefer.MLONGTITUDE));
                orderPushDialog = new OrderPushDialog(HomeActivity.this, wid, hei, 1, orderPush, _MLATITUDE, _MLONGTITUDE);
                orderPushDialog.setOnShowListener(showListener);
                orderPushDialog.setOnDismissListener(dismissListener);
                if (orderPushDialog != null && !orderPushDialog.isShowing()) {
                    JPushInterface.clearNotificationById(HomeActivity.this, orderPush.getNotifactionId());
                    orderPushDialog.show();
                }
                orderPushDialog.setOnOrderClickListener(new OrderPushDialog.OnOrderClickListener() {
                    @Override
                    public void onOrderclick(View view, int isKnoic, OrderPush bean) {
                        type = false;
                        switch (isKnoic) {
                            case 1:
                                new Thread(getOrderTask).start(); // 抢单
                                break;

                            case 2:

                            case 3:
                                new Thread(getBacklogTask).start(); // 派单和抢险抢修
                                break;
                        }
                    }
                });
                break;

            case 4: // 开工
                if (startWorkDialog != null && startWorkDialog.isShowing()) {
                    return;
                }
                this.orderPush = orderPush;
                final LatLng latLng = new LatLng(Double.parseDouble(String.valueOf(HomeActivity.this.orderPush.getDataMap().getOrder_x())),
                        Double.parseDouble(String.valueOf(HomeActivity.this.orderPush.getDataMap().getOrder_y())));

                final AlertDialog.Builder startWork = new AlertDialog.Builder(HomeActivity.this);
                startWork.setTitle("订单开工");
                startWork.setMessage("订单号" + orderPush.getDataMap().getOrder_id() +
                        orderPush.getDataMap().getService().getChildren().get(0).getService_name() + "已经开工");
                startWork.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startWorkDialog = null; // bugid=3;
                        Intent start = new Intent(HomeActivity.this, OrderDetailsActivity.class);
                        Bundle startBundle = new Bundle();
                        startBundle.putSerializable("orderPush", HomeActivity.this.orderPush);
                        startBundle.putParcelable("start", orderStart);
                        startBundle.putSerializable("userInfo", userInfo);
                        startBundle.putParcelable("end", latLng);
                        start.putExtras(startBundle);
                        startActivity(start);
                    }
                });
                if (startWorkDialog == null) {//bugid=3;

                    startWork.setCancelable(false);
                    startWorkDialog = startWork.create();
                    startWorkDialog.show();
                    JPushInterface.clearNotificationById(HomeActivity.this, orderPush.getNotifactionId());
                }
                break;

            case 6: // 代付
//                JPushInterface.stopPush(HomeActivity.this); // 已经走不到这个case了
//                Intent i = new Intent(HomeActivity.this, PayActivity.class);
//                Bundle bundle = new Bundle();
//                i.putExtra("orderId", String.valueOf(orderPush.getDataMap().getOrder_id()));
//                i.putExtra("type", 1);
//                i.putExtra("notifactionId", orderPush.getNotifactionId());
//                i.putExtras(bundle);
//                startActivity(i);

                Intent toPay = new Intent(HomeActivity.this, OrderDetailsActivity.class);
                Bundle datas = new Bundle();
                datas.putSerializable("orderPush", orderPush);
                toPay.putExtras(datas);
                toPay.putExtra("type", 1);
                startActivity(toPay);
                break;
            case 7: // 付款完成

                break;

            case 22: // 派单
//                ISOFFER = false;
                WindowManager wm1 = getWindowManager();
                int wid1 = wm1.getDefaultDisplay().getWidth();
                int hei1 = wm1.getDefaultDisplay().getHeight();
//                // 只显示最早打开对话框
//                if (orderPushDialog != null && orderPushDialog.isShowing()) {
//                    return;
//                }
//                if (orderDialog != null && orderDialog.isShowing()) {
//                    return;
//                }

                double MLATITUDE_ = Double.parseDouble(PreferenceUtil.readString(HomeActivity.this, SharedPrefer.MLATITUDE, SharedPrefer.MLATITUDE));
                double MLONGTITUDE_ = Double.parseDouble(PreferenceUtil.readString(HomeActivity.this, SharedPrefer.MLONGTITUDE, SharedPrefer.MLONGTITUDE));
                orderPushDialog = new OrderPushDialog(HomeActivity.this, wid1, hei1, 1, orderPush, MLATITUDE_, MLONGTITUDE_);
                orderPushDialog.setOnShowListener(showListener);
                orderPushDialog.setOnDismissListener(dismissListener);
                if (orderPushDialog != null && !orderPushDialog.isShowing()) {
                    this.orderPush = orderPush;
                    JPushInterface.clearNotificationById(HomeActivity.this, orderPush.getNotifactionId());
                    notBack(orderPushDialog);
                    orderPushDialog.show();
                } else {
                    return;
                }
                orderPushDialog.setOnOrderClickListener(new OrderPushDialog.OnOrderClickListener() {
                    @Override
                    public void onOrderclick(View view, int isKnoic, OrderPush bean) {
                        type = false;
                        new Thread(getBacklogTask).start(); // 派单和抢险抢修
                    }
                });
                break;

        }
    }

    /**
     * 派单和抢险抢修不可以退出
     */
    private void notBack(Dialog dialog) {
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    return true;
                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        JPushInterface.stopPush(this);
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
//        JPushInterface.setAliasAndTags(HomeActivity.this, "", notWorktags, mAliasCallback);
    }
}
