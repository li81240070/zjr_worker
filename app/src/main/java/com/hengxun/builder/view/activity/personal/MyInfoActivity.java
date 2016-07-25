package com.hengxun.builder.view.activity.personal;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.model.UserInfo;
import com.hengxun.builder.utils.okhttp.HttpsController;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.MD5Util;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.order.MineOrderActivity;
import com.hengxun.builder.view.adapter.MyTagRvAdapter;
import com.hengxun.builder.view.widget.CircleImage;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZY on 2016/3/9.
 * 我的信息
 */
public class MyInfoActivity extends BaseActivity implements View.OnClickListener {
    public static MyInfoActivity finishActivity; // 注销销毁的activity

    private RatingBar my_Rb; // 星星条
    private ProgressBar myMark_Pb; // 积分条

    private RelativeLayout myOrder_Rl;          // 我的订单
    private RelativeLayout myBill_Rl;           // 我的账单
    private RelativeLayout myMarks_Rl;          // 我的积分
    //    private RelativeLayout myAccountSafety_Rl;  // 账号与安全
    private LatLng orderStart;                  // 工人位置
//    private LatLng orderEnd;                    // 订单位置

    private CircleImage myHead_Iv;              // 用户头像
    private TextView myName_Tv;                 // 用户姓名
    private TextView myOrderNumber_Tv;          // 用户已完成订单数
    private RecyclerView myTag_Rv;              // 匠人标签
    private MyTagRvAdapter adapter;
    private TextView my_orders_Tv;              // 我的订单
    private TextView myAccount_Tv;              // 我的账单
    private TextView myMarks_Tv;                // 我的积分
    private List<String> tags;                  // 匠人标签
    private WaittingDiaolog dialog;

    private RelativeLayout myGift_Rl;
    private UserInfo userInfo;                  // 用户信息
    private Handler imageHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        finishActivity = this;
        EventBus.getDefault().register(this);
        showToolBar(getResources().getString(R.string.my_info), true, this);

        dialog = new WaittingDiaolog(this);
        dialog.setCanceledOnTouchOutside(false);
//        dialog.setMessage("正在加载中...");
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        new Thread(loginTask).start();
    }

    @Override
    protected void initView() {
        super.initView();
        my_Rb = (RatingBar) findViewById(R.id.my_Rb);
        myMark_Pb = (ProgressBar) findViewById(R.id.myMark_Pb);
        myOrder_Rl = (RelativeLayout) findViewById(R.id.myOrder_Rl);
        myBill_Rl = (RelativeLayout) findViewById(R.id.myBill_Rl);
        myMarks_Rl = (RelativeLayout) findViewById(R.id.myMarks_Rl);
        my_orders_Tv = (TextView) findViewById(R.id.my_orders_Tv);
//        myAccountSafety_Rl = (RelativeLayout) findViewById(R.id.myAccountSafety_Rl);

        myHead_Iv = (CircleImage) findViewById(R.id.myHead_Iv);
        myName_Tv = (TextView) findViewById(R.id.myName_Tv);
        myOrderNumber_Tv = (TextView) findViewById(R.id.myOrderNumber_Tv);
        myMarks_Tv = (TextView) findViewById(R.id.myMarks_Tv);
        myAccount_Tv = (TextView) findViewById(R.id.myAccount_Tv);
        // 礼物列表 暂时注掉
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
//        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        myGift_Rl = (RelativeLayout) findViewById(R.id.myGift_Rl);

        // 匠人标签列表
        myTag_Rv = (RecyclerView) findViewById(R.id.myTag_Rv);
        adapter = new MyTagRvAdapter(this);
    }

    @Override
    protected void initData() {
        super.initData();
        imageHandler = new Handler();
        orderStart = getIntent().getParcelableExtra("orderStart");

        myOrder_Rl.setOnClickListener(this);
        myBill_Rl.setOnClickListener(this);
        myMarks_Rl.setOnClickListener(this);
        myHead_Iv.setOnClickListener(this);

        // 数据错误则返回
        if (userInfo != null) {
            // 设置头像
            final String imgurl = AppApi.ORDER_IMG + userInfo.getDataMap().getAvatar() + "_200.jpg";
//            Picasso.with(this).load(imgurl).error(R.mipmap.default_head).into(myHead_Iv);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    OkHttpClient client = new OkHttpClient();
                    try {
                        client = HttpsController.setCertificates(MyInfoActivity.this, MyInfoActivity.this.getAssets().open("zhenjren.cer"));
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
                                    myHead_Iv.setImageBitmap(bm);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            // 设置姓名和工号
            myName_Tv.setText(userInfo.getDataMap().getReal_name() + " "
                    + userInfo.getDataMap().getWorker_no());
            // 设置评分
            my_Rb.setRating(userInfo.getDataMap().getStar());
            // 设置已完成订单数
            myOrderNumber_Tv.setText("本月" + getResources().getString(R.string.my_order_number) + " " + userInfo.getDataMap().getMonth_order());
            // 总订单数
            my_orders_Tv.setText(String.valueOf(userInfo.getDataMap().getTotal_order()));
            // 账户余额
            double d = userInfo.getDataMap().getAmount();
            String st = String.format("%.2f", d);
            myAccount_Tv.setText("¥ " + st);
            // 总积分
            myMarks_Tv.setText(String.valueOf(userInfo.getDataMap().getPoints()));

            // 给匠人添加标签
            GridLayoutManager layoutManager = null;
            tags = userInfo.getDataMap().getTags();
            // 标签可能为空
            if (tags != null && tags.size() > 0) {
                int num = tags.size() % 4;
                if (num == 0) {
                    layoutManager = new GridLayoutManager(this, tags.size() / 4);
                } else {
                    layoutManager = new GridLayoutManager(this, tags.size() / 4 + 1);
                }
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                myTag_Rv.setLayoutManager(layoutManager);
                myTag_Rv.setAdapter(adapter);
                adapter.addData(tags);
                // 暂时注掉
//                myGift_Rl.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        changeGiftImg(userInfo.getDataMap().getPoints(), userInfo.getDataMap().getGift_val());
//                    }
//                }, 300);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Bundle data = new Bundle();
        data.putSerializable("userInfo", userInfo);
        switch (v.getId()) {
            // 进入我的订单
            case R.id.myOrder_Rl:
                Intent myOrder = new Intent(MyInfoActivity.this, MineOrderActivity.class);
                data.putParcelable("orderStart", orderStart);
//                data.putParcelable("orderEnd", orderEnd);
                myOrder.putExtras(data);
                startActivity(myOrder);
                break;

            // 进入我的账单
            case R.id.myBill_Rl:
                Intent myBill = new Intent(MyInfoActivity.this, PersonalAccountActivity.class);
                myBill.putExtras(data);
                startActivity(myBill);
                break;

            // 进入我的积分
            case R.id.myMarks_Rl:
                Intent myMarks = new Intent(MyInfoActivity.this, MyMraksActivity.class);
                myMarks.putExtras(data);
                startActivity(myMarks);
                break;

//          // 账号与安全
//            case R.id.myAccountSafety_Rl:
//                Intent myAccountSafety = new Intent(MyInfoActivity.this, AccountSafeActivity.class);
//                startActivity(myAccountSafety);
//                break;

            // 进入个人信息
            case R.id.myHead_Iv:
                Intent toMyInfo = new Intent(MyInfoActivity.this, PersonalInfoActivity.class);
                toMyInfo.putExtras(data);
                startActivity(toMyInfo);
                break;
        }
    }

    /**
     * 获取信息后的操作
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (msg.what == 200) {
                initView();
                initData();
            }
        }
    };

    /**
     * 获取匠人信息
     */
    Runnable loginTask = new Runnable() {

        @Override
        public void run() {
            Message msg = Message.obtain();
            String account = PreferenceUtil.readString(MyInfoActivity.this,
                    SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);
            String psw = PreferenceUtil.readString(MyInfoActivity.this,
                    SharedPrefer.PSW, SharedPrefer.PSW);
            String token = PreferenceUtil.readString(MyInfoActivity.this, AppConstants.TOKEN, AppConstants.TOKEN);
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.USERNAME, account);
                map.put(AppConstants.PSW, MD5Util.getMD5String(psw).toLowerCase(Locale.getDefault()));
                map.put(AppConstants.OS, "2");
                map.put(AppConstants.TOKEN, token != null ? token : "");
                String url = UrlUtils.addParams(AppApi.LOGIN, map); // 拼接参数
                Response response = OkHttpClientManager.getAsyn(url, MyInfoActivity.this);
                if (response.code() != 401) {
                    Gson gson = new Gson();
                    String result = response.body().string();
                    userInfo = gson.fromJson(result, UserInfo.class);
                    if (userInfo.getCode() == 401) {
                        errToken();
                        return;
                    }
                    String _token;
                    if (null != userInfo.getDataMap().getToken()) {
                        _token = userInfo.getDataMap().getToken();
                        PreferenceUtil.savePerfs(MyInfoActivity.this, SharedPrefer.TOKEN, SharedPrefer.TOKEN, _token);
                    } else {
                        _token = token;
                    }
                    if (userInfo.getCode() == 200) {
                        msg.what = 200;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
        }
    };

    /**
     * 上传图片以后
     * 可以点击完成 否则提示需要上传图片
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(String str) {
        str.equals("finish");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post("123");
        EventBus.getDefault().unregister(this);
    }
}
