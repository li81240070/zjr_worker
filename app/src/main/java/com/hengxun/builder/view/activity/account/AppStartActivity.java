package com.hengxun.builder.view.activity.account;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.model.UserInfo;
import com.hengxun.builder.utils.AppUtils;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.MD5Util;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.HomeActivity;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by ZY on 2016/3/24.
 */
public class AppStartActivity extends BaseActivity {
    private String address;    // 当前位置
    private String account;    // 匠人账号
    private String psw;        // 匠人密码
    private UserInfo userInfo; // 匠人信息
//    private ProgressDialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        init();
    }

    private void init() {
        // 判断定位是否开启 若没有则跳转到错误页面
        boolean isOpen = AppUtils.isOpen(this);
        if (!isOpen) {
            Intent intent = new Intent(this, ErrorActivity.class);
            startActivity(intent);
            finish();
            return;
        }
//        validateAddress(); // 初始化定位

        // 读取账号密码
        account = PreferenceUtil.readString(AppStartActivity.this,
                SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);
        psw = PreferenceUtil.readString(AppStartActivity.this,
                SharedPrefer.PSW, SharedPrefer.PSW);

        // 如果曾经登录过  直接登录
        // 否则去登录
        if (account != null && psw != null) {
//            dialog = new ProgressDialog(this);
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setMessage("正在加载中...");
//            if (dialog != null && !dialog.isShowing()) {
//                dialog.show();
//            }
            new Thread(loginTask).start();
        } else {
            mhandler.sendEmptyMessageDelayed(100, 0);
        }
    }

    private Handler mhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 100) {
                Intent intent = new Intent();
                intent.setClass(AppStartActivity.this, LoginActivity.class); // 跳转到登录页面
                startActivity(intent);
                finish();
                mhandler.removeMessages(100);
            }
            return true;
        }
    });

    /**
     * 初始化定位
     */
    private void validateAddress() {
        LocationClient mLocationClient;
        LocationListener locationListener;

        // 初始化定位方法
        locationListener = new LocationListener();
        mLocationClient = new LocationClient(this);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);

        mLocationClient.registerLocationListener(locationListener);    //注册监听函数
        mLocationClient.start(); //开始定位
    }

    /**
     * 定位获取位置信息监听
     */
    private class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
//            address = bdLocation.getAddrStr();
            if (bdLocation == null && bdLocation.getTime() == null && bdLocation.getAddress() == null) {
//                Toast.makeText(AppStartActivity.this, "null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 获取信息后的操作
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            if (dialog != null && dialog.isShowing()) {
//                dialog.dismiss();
//            }
            switch (msg.what) {
                case 200:
                    Intent toMain = new Intent(AppStartActivity.this, HomeActivity.class);
                    Bundle data = new Bundle();
                    data.putSerializable("userInfo", userInfo);
                    toMain.putExtras(data);
                    startActivity(toMain);
                    finish();
                    break;

                case 21:
                    Intent intent = new Intent(AppStartActivity.this, ErrorActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case 400:
//                    Toast.makeText(AppStartActivity.this, "登录异常, 请重新登录", Toast.LENGTH_SHORT).show();
                    Intent toLogin = new Intent(AppStartActivity.this, LoginActivity.class);
                    startActivity(toLogin);
                    finish();
                    break;
            }
        }
    };

    /**
     * 登录获取匠人信息
     */
    Runnable loginTask = new Runnable() {

        @Override
        public void run() {
            Message msg = new Message();
            try {
//                new Thread(new TimeThread()).start();
                String token = PreferenceUtil.readString(AppStartActivity.this, AppConstants.TOKEN, AppConstants.TOKEN);
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.USERNAME, account);
                map.put(AppConstants.PSW, MD5Util.getMD5String(psw).toLowerCase(Locale.getDefault()));
                map.put(AppConstants.OS, "2");
                map.put(AppConstants.TOKEN, token != null ? token : "");
                String url = UrlUtils.addParams(AppApi.LOGIN, map); // 拼接参数
                Response response = OkHttpClientManager.getAsyn(url, AppStartActivity.this);
                String result = response.body().string();
                if (response.code() != 401) {
                    Gson gson = new Gson();
                    userInfo = gson.fromJson(result, UserInfo.class);
                    if (userInfo.getCode() == 200) {
                        msg.what = 200;
                        String _token;
                        if (null != userInfo.getDataMap().getToken()) {
                            _token = userInfo.getDataMap().getToken();
                            PreferenceUtil.savePerfs(AppStartActivity.this, SharedPrefer.TOKEN, SharedPrefer.TOKEN, _token);
                        } else {
                            _token = "";
                        }
                    } else if (userInfo.getCode() == 401) {
                        errToken();
                    } else { // ZY 2016年5月17日11:57:10
                        msg.what = 400;
                    }
                } else {
                    msg.what = 400;
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.what = 400;
            }
            handler.sendMessage(msg);
        }
    };

//    public class TimeThread implements Runnable {
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            while (true) {
//                try {
//                    Thread.sleep(10000);// 线程暂停10秒，单位毫秒
//                    Message message = new Message();
//                    message.what = 1;
//                    mHandler.sendMessage(message);// 发送消息
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    /**
//     * 长时间登录无应答
//     * */
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1) {
//                Toast.makeText(AppStartActivity.this, "登录异常, 请重新登录", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(AppStartActivity.this, HomeActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }
//    };

    /**
     * 获取开放区域
     */
    Runnable getRegionTask = new Runnable() {

        @Override
        public void run() {
            Message msg = new Message();
            try {

                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.ADDRESS, address);
                String url = UrlUtils.addParams(AppApi.VALIDATE, map); // 拼接参数
                Response response = OkHttpClientManager.getAsyn(url, AppStartActivity.this);
//                Log.d("getRegionTask", response.body().string().toString());
                if (response.code() != 401) {
//                    Gson gson = new Gson();
//                    userInfo = gson.fromJson(response.body().string().toString(), UserInfo.class);
//                    if (userInfo.getCode() == 200) {
//                        msg.what = 21;
//                    }
                } else {
                    Log.d("AppStartActivity", "response.code():" + response.code());
                }
            } catch (Exception e) {
                e.printStackTrace();
//                msg.what = 21;
            }
        }
    };

}
