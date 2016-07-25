package com.hengxun.builder.view.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
//import com.hengxun.builder.utils.StatusBarCompat;
import com.hengxun.builder.utils.exception.HxBaseCrashHandler;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.squareup.okhttp.Response;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;


/**
 * 程序的基类activity
 * 主要实现的功能：
 * 1：实现连续点击退出程序
 * 2:设置页面的titlebar是否显示和标题的内容
 * 3:设置状态栏的颜色
 * 4：存储异常的日志
 *
 * @author dave
 */
public class BaseActivity extends AppCompatActivity {
//    private double mLatitude, mLongtitude; // 经度  纬度
    private String worker_no;       // 匠人工号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int color = Color.parseColor("#ffffff");
//        StatusBarCompat.compat(this, color);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化布局
     */
    protected void initView() {
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);
    }

    /**
     * 设置监听事件
     */
    protected void setListener() {
    }

    /**
     * 设置状态栏的颜色
     *
     * @param isShow        是否显示
     * @param statuBarColor 状态栏的颜色
     * @param activity      Activity
     */
    protected void showStatuBar(boolean isShow, int statuBarColor, Activity activity) {
        if (isShow) {
//            StatusBarCompat.compat(activity, statuBarColor);
        }
    }

    /**
     * ZY 2016年5月20日15:51:11
     * token失效
     * */
    protected void errToken() {
        Message message = Message.obtain();
        message.what = 10;
        errHandler.sendMessage(message);
    }

    protected Handler errHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10) {
                Toast.makeText(BaseActivity.this, R.string.dialog_token_unable, Toast.LENGTH_SHORT).show();
            }
        }
    };

//    /**
//     * 发送匠人位置
//     */
//    protected void sendLocation(boolean isSend, int span, String worker_no) {
//        // 确定发送
//        if (isSend) {
//            this.worker_no = worker_no;
//            LocationClient mLocationClient;
//            LocationListener locationListener;
////          double mLatitude, mLongtitude; // 经度  纬度
//
//            // 初始化定位方法
//            locationListener = new LocationListener();
//            mLocationClient = new LocationClient(this);
//
//            LocationClientOption option = new LocationClientOption();
//            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
//            );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//            option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//            option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//            option.setOpenGps(true);//可选，默认false,设置是否使用gps
//            option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//            option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//            option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//            option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//            option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//            option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
//            mLocationClient.setLocOption(option);
//
//            mLocationClient.registerLocationListener(locationListener);    //注册监听函数
//            mLocationClient.start(); //开始定位
//
//        }
//    }

    /**
     * 是否打印错误日志
     *
     * @param isSave //是否保存异常日志
     */


    protected void saveCrashLog(boolean isSave) {
        //sd卡根目录下新建文件夹
        String path = Environment.getExternalStorageDirectory() + "/crashLocatFile";
        File storePath = new File(path);
        //判断是否存在
        if (!storePath.exists()) {
            storePath.mkdirs();
        }

        //异常捕获处理类
        if (isSave) {
            HxBaseCrashHandler hxBaseCrashHandler = HxBaseCrashHandler.INSTANCE;
            hxBaseCrashHandler.initCrashHandler(this, path, null);
        }
    }

    /**
     * 设置是否显示toolbar
     *
     * @param titleName 标题的名字
     * @param isShow    是否显示
     */
    protected void showToolBar(String titleName, boolean isShow, Activity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (isShow) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
        TextView baseactivity_title_TV = (TextView) activity.findViewById(R.id.toolbarTitle_Tv);
        baseactivity_title_TV.setText(titleName);
        ImageView toolbar_left_Ib = (ImageView) activity.findViewById(R.id.toolbar_left_Ib);
        toolbar_left_Ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    /**
//     * 判断是否开启定位  待测试
//     */
//    protected boolean isLocate() {
//        LocationClient mLocationClient;
//        LocationListener locationListener;
////        double mLatitude, mLongtitude; // 经度  纬度
////            RoutePlanSearch search;
//
//        // 初始化定位方法
//        locationListener = new LocationListener();
//        mLocationClient = new LocationClient(this);
//
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
//        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
////            span = 8000; // 8秒定一次位
//        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
//        mLocationClient.setLocOption(option);
//
//        mLocationClient.registerLocationListener(locationListener);    //注册监听函数
//        mLocationClient.start(); //开始定位
//
//        return mLatitude >= 0 && mLongtitude >= 0;
//    }

//    /**
//     * 菜单、返回键响应
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // TODO Auto-generated method stub
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            exitBy2Click(); //调用双击退出函数
//        }
//        return false;
//    }

//    /**
//     * 定位获取位置信息监听
//     */
//    private class LocationListener implements BDLocationListener {
//
//        @Override
//        public void onReceiveLocation(BDLocation bdLocation) {
//            mLatitude = bdLocation.getLatitude(); // 获取经度
//            mLongtitude = bdLocation.getLongitude(); // 获取纬度
//            new Thread(sendLocationTask).start(); // 上传位置
//        }
//    }

//    /**
//     * 上传匠人位置
//     */
//    Runnable sendLocationTask = new Runnable() {
//
//        @Override
//        public void run() {
//            try {
//                OkHttpClientManager.Param params[] = new OkHttpClientManager.Param[]{
//                        //参数名 mobile 不是 工号
//                        new OkHttpClientManager.Param(AppConstants.WORKER_NO, worker_no)
//                        //参数名 坐标
//                        , new OkHttpClientManager.Param(AppConstants.X, String.valueOf(mLatitude))
//                        , new OkHttpClientManager.Param(AppConstants.Y, String.valueOf(mLongtitude))
//                        , new OkHttpClientManager.Param(AppConstants.TYPE, "1") // 写死  不知道什么用
//                        , new OkHttpClientManager.Param(AppConstants.TIMESTAMP, String.valueOf(System.currentTimeMillis()))
//                };
//                Response response = OkHttpClientManager.post(AppApi.LOCATE, BaseActivity.this, params[0], params[1], params[2], params[3]);
////                Log.d("locate", response.body().string());
//            } catch (Exception e) {
//                e.printStackTrace();
//                return;
//            }
//        }
//    };

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    public void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
            System.exit(0);
        }
    }

}
