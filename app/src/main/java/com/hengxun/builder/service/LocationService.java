package com.hengxun.builder.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.model.BacklogOrder;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.squareup.okhttp.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ZY on 2016/4/13.
 * 上传用户位置
 */
public class LocationService extends Service {
    private static int SUCESS = 1;
    private static int FAIL = 2;

    private double mLatitude, mLongtitude; // 经度  纬度
    private String worker_no;
    private LocationListener locationListener;
    private LocationClient mLocationClient;
    private OrderStatusThread orderStatusThread = null;

    boolean workState = false;//开工收工状态
    boolean isSendOrder = false;//有订单状态下发送位置
    boolean isSendNoOrder = false;//没有订单下发送位置
    private int time; // 上传位置间隔时间

    private BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
//          state 1 开工 2 收工
            int status = arg1.getIntExtra("state", 0);
            if (1 == status) //开工
            {
                workState = true;
            } else if (2 == status) //收工
            {
                workState = false;

            } else {
                //抢单/订单取消/订单完成
            }
            synchronized (orderStatusThread) {
                orderStatusThread.notify();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.hengxun.builder.service");
        registerReceiver(reciver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        orderStatusThread = new OrderStatusThread("OrderStatusThread");
        orderStatusThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        unregisterReceiver(reciver);
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        // 初始化定位方法
        locationListener = new LocationListener();
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(locationListener);    //注册监听函数
    }

    private synchronized void changeLocationOption(int span) {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
    }

    /**
     * 定位获取位置信息监听
     */
    private class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mLatitude = bdLocation.getLatitude(); // 获取经度
            mLongtitude = bdLocation.getLongitude(); // 获取纬度

            if (null == bdLocation.getAddrStr()) {

            } else {
//                new Thread(sendLocationTask).start(); // 上传位置
                PreferenceUtil.savePerfs(getApplicationContext(), SharedPrefer.MLATITUDE, SharedPrefer.MLATITUDE, String.valueOf(mLatitude));
                PreferenceUtil.savePerfs(getApplicationContext(), SharedPrefer.MLONGTITUDE, SharedPrefer.MLONGTITUDE, String.valueOf(mLongtitude));
            }
        }
    }

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new Thread(sendLocationTask).start(); // 上传位置
            handler.postDelayed(runnable, time);
        }
    };

    /**
     * 上传匠人位置
     */
    Runnable sendLocationTask = new Runnable() {

        @Override
        public void run() {
            Log.d("LocationService_------", "sendLocationTask1" + sendLocationTask.toString());
            try {
                worker_no = PreferenceUtil.readString(LocationService.this,
                        SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);
                String timestamp = "timestmap";
                Date date = new Date();
                OkHttpClientManager.Param params[] = new OkHttpClientManager.Param[]{
                        //参数名 mobile 不是 工号
                        new OkHttpClientManager.Param(AppConstants.WORKER_NO, worker_no)
                        //参数名 坐标
                        , new OkHttpClientManager.Param(AppConstants.X, String.valueOf(mLatitude))
                        , new OkHttpClientManager.Param(AppConstants.Y, String.valueOf(mLongtitude))
                        , new OkHttpClientManager.Param(timestamp, String.valueOf(date.getTime()))
                };
                Response response = OkHttpClientManager.post(AppApi.LOCATE, getApplicationContext(), params[0], params[1], params[2], params[3]);
                if (response.code() != 401) {
                    Log.d("LocationService_>>>>>>", "sendLocationTask2" + sendLocationTask.toString());
                    String result = response.body().string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

//    /**
//     * 未付款 派单列表
//     */
//    Runnable backlogTask = new Runnable() {
//        @Override
//        public void run() {
//            String userId = PreferenceUtil.readString(LocationService.this, SharedPrefer.WORKERID, SharedPrefer.WORKERID);
//            HashMap<String, String> map = new HashMap<>();
//            map.put(AppConstants.WORKER_ID, userId);
//            String url = UrlUtils.addParams(AppApi.BACKLOG, map); // 拼接参数
//            Response response;
//            Message message = Message.obtain();
//            try {
//                response = OkHttpClientManager.getAsyn(url, LocationService.this);
//                if (response.code() != 401) {
//                    String result = response.body().string();
//                    Gson gson = new Gson();
//                    BacklogOrder order = gson.fromJson(result, BacklogOrder.class);
//                    BacklogOrder.DataMapEntity entity = order.getDataMap();
//                    PreferenceUtil.savePerfs(LocationService.this, AppConstants.WORKERSSP, AppConstants.ISTODAYORDERS, entity.isTodayOrders());
//                    message.what = SUCESS;
//                } else {
//                    message.what = FAIL;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                message.what = FAIL;
//            }
//        }
//    };

//    //根据状态和参数判断多久上传一次位置
//    private int upMyLocation() {
//        boolean isStart = PreferenceUtil.readBoolean(LocationService.this, AppConstants.WORKERSSP, AppConstants.ISSTARTWORKER);
//        String istodayorders = PreferenceUtil.readString(LocationService.this, AppConstants.WORKERSSP, AppConstants.ISTODAYORDERS);
//        if (isStart) {
//            //开工
//            if ("0".equals(istodayorders)) {
//                //假如有今天得订单
//                return 10000;
//            } else {
//                // 今天没订单
//                return 600000;
//            }
//        } else {
//            // 完工
//            return 0;
//        }
//    }

//    public class LocationReciver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context arg0, Intent arg1) {
//            Integer status = 0;
//            System.out.println("内部接收者");
//
//            if (1 == status) //开工
//            {
//                workState = true;
//            } else if (2 == status) //收工
//            {
//                workState = false;
//
//            } else {
//                //抢单/订单取消/订单完成
//            }
//
//            orderStatusThread.notify();
//        }
//
//    }

    class OrderStatusThread extends Thread {
        private String name;                //操作人

        Integer orderCount = 0;

        OrderStatusThread(String name) {
            this.name = name;
            initLocation();
//            changeLocationOption(0);
        }

        public void run() {
            Lock lock = new ReentrantLock();
            while (true) {
                try {
                    lock.lock();
                    synchronized (orderStatusThread) {
                        orderStatusThread.wait();
                    }
                    if (workState) //开工状态
                    {
                        if (getOrderState()) //有订单
                        {
                            if (isSendNoOrder == true) {
                                mLocationClient.stop();
                                isSendOrder = false;
                                isSendNoOrder = false;
                            }

                            if (isSendOrder == false) {
                                // 10秒上报一次地址
                                time = 10000;
                                changeLocationOption(time);
                                mLocationClient.start(); //开始定位
                                isSendOrder = true;
                                handler.postDelayed(runnable, time);
                            }
                        } else {
                            if (true == isSendOrder) // 有订单状态下上报过位置信息
                            {
                                mLocationClient.stop();
                                isSendOrder = false;
                                isSendNoOrder = false;
                                handler.removeCallbacks(runnable);
                            }

                            if (false == isSendNoOrder) //没有上报过位置
                            {
                                time = 30000;
                                changeLocationOption(time);
                                mLocationClient.start(); // 开始定位
                                isSendNoOrder = true; //没有订单下定位启动
                                handler.postDelayed(runnable, time);
                            }
                        }
                    } else {
                        isSendNoOrder = false;
                        isSendOrder = false;
                        mLocationClient.stop();//收工状态下停止定位
                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }

        boolean getOrderState() {
            boolean bResult = false;

            try {
                String userId = String.valueOf(PreferenceUtil.readInteger(LocationService.this, SharedPrefer.WORKERID, SharedPrefer.WORKERID));
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.WORKER_ID, userId);
                String url = UrlUtils.addParams(AppApi.BACKLOG, map); // 拼接参数
                Response response = OkHttpClientManager.getAsyn(url, LocationService.this);
                if (response.code() != 401) {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    BacklogOrder order = gson.fromJson(result, BacklogOrder.class);
                    BacklogOrder.DataMapEntity entity = order.getDataMap();
                    if ("0".equals(entity.getTodayOrders())) //没有订单
                    {

                    } else {
                        bResult = true;
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bResult;
        }
    }
}
