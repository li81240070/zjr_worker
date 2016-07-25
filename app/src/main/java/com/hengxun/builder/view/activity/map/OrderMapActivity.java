package com.hengxun.builder.view.activity.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.model.OrderList;
import com.hengxun.builder.model.UserInfo;
import com.hengxun.builder.utils.baidumap.jarfile.DrivingRouteOverlay;
import com.hengxun.builder.utils.baidumap.jarfile.OverlayManager;
import com.hengxun.builder.utils.baidumap.jarfile.TransitRouteOverlay;
import com.hengxun.builder.utils.baidumap.jarfile.WalkingRouteOverlay;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.order.OrderDetailsActivity;
import com.hengxun.builder.view.widget.OrderInforDialog;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ZY on 2016/3/9.
 * 订单地图
 */
public class OrderMapActivity extends BaseActivity implements View.OnClickListener, OnGetRoutePlanResultListener {
    //计划路线假数据
//    String city = "大连";
    private boolean ISLOCATE = false;

    private MapView order_Mv;  // 百度地图view
    private BaiduMap baiduMap; // 百度地图操作
    private WaittingDiaolog progressDialog;

    private AlertDialog workDialog; // 提示开工对话框

    private UserInfo userInfo; // 用户信息
    private LocationClient mLocationClient;
    private LocationListener locationListener;
    private BitmapDescriptor mMarker; // 覆盖物相关
    private double mLatitude, mLongtitude; // 经度  纬度
    //计划路线相关
    private RoutePlanSearch search;
    private OverlayManager routeOverlay = null;

    private boolean useDefaultIcon = false; // 导航图标状态
    private LatLng orderStart; // 匠人现在位置
    private LatLng orderEnd; // 订单位置
    private List<OrderList.DataMapEntity.OrdersEntity> orders;
    //    private OrderList.DataMapEntity.OrdersEntity bean;
    private OrderInforDialog dialog; // 订单详情对话框

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_map);
        EventBus.getDefault().register(this);
        showToolBar(getResources().getString(R.string.map_order), true, this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        order_Mv = (MapView) findViewById(R.id.order_Mv);
    }

    @Override
    protected void initData() {
        super.initData();
        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        baiduMap = order_Mv.getMap();
        // 导航相关方法
        search = RoutePlanSearch.newInstance();
        search.setOnGetRoutePlanResultListener(this);

        // 初始化定位方法
        locationListener = new LocationListener();
        mLocationClient = new LocationClient(this);
        initLocation();
        mLocationClient.registerLocationListener(locationListener);    //注册监听函数
        mLocationClient.start();//开始定位

        //普通地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        order_Mv.animate();

        getMarKer();//覆盖物初始化
        progressDialog = new WaittingDiaolog(this);
        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setMessage("正在加载中...");
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
        new Thread(orderListTask).start(); // 获取地图订单点
    }

    @Override
    protected void showToolBar(String titleName, boolean isShow, Activity activity) {
        super.showToolBar(titleName, isShow, activity);
        TextView toolbar_right_Tv = (TextView) activity.findViewById(R.id.toolbar_right_Tv);
        toolbar_right_Tv.setText(getResources().getString(R.string.map_hot));
        toolbar_right_Tv.setOnClickListener(this);
    }

    private void initLocation() {
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(17.0f);
        baiduMap.setMapStatus(update);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 10000; // 10秒定一次位
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

    //覆盖物相关 初始化方法
    private void getMarKer() {
        //覆盖物相关
        mMarker = BitmapDescriptorFactory.fromResource(R.mipmap.order_icon);
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 先获取匠人工作状态
                boolean ISWORKING = getIntent().getBooleanExtra("isworking", false);

                // 开工状态弹出订单dialog
                if (ISWORKING) {
                    // 如果没有对话框
//                    if (null == dialog && !dialog.isShowing()) {
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    final Bundle bundle = marker.getExtraInfo();
                    OrderList.DataMapEntity.OrdersEntity bean = (OrderList.DataMapEntity.OrdersEntity) bundle.getSerializable("info");

                    WindowManager manager = OrderMapActivity.this.getWindowManager();
                    int wid = manager.getDefaultDisplay().getWidth();
                    int hei = manager.getDefaultDisplay().getHeight();
                    //1.context 2.屏幕宽度 3.高度 4.是否是抢单dialog 5.数据
                    dialog = new OrderInforDialog(OrderMapActivity.this, wid, hei, true, bean, mLatitude, mLongtitude);
                    dialog.show();
                    dialog.setOnOrderClickListener(new OrderInforDialog.OnOrderClickListener() {
                        @Override
                        public void onOrderclick(View view, boolean isKnoic, OrderList.DataMapEntity.OrdersEntity bean) {
                            dialog.dismiss();
                            new Thread(new GetOrderTaskRunnable().setOrderEntity(bean)).start(); // 抢单
                        }
                    });
//                    }

                } else { // 如果没有开工 需要先开工
                    showNotWorkDialog(); // 弹出对话框
                }

                return true;
            }
        });
    }

    //步行监听 结果回调
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        if (walkingRouteResult == null) {
            Toast.makeText(this, "抱歉，未找到相关结果", Toast.LENGTH_SHORT).show();
        }
        if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(baiduMap);
            baiduMap.setOnMarkerClickListener(overlay);
            if (routeOverlay != null) {
                routeOverlay.removeFromMap();
            }
            routeOverlay = overlay;
            overlay.setData(walkingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        } else {
            Toast.makeText(this, "定位失败,请电话联系用户", Toast.LENGTH_SHORT).show();
        }
    }

    //公交监听 结果回调
    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        if (transitRouteResult == null) {
            Toast.makeText(this, "抱歉，未找到相关结果", Toast.LENGTH_SHORT).show();
        }
        if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(baiduMap);
            baiduMap.setOnMarkerClickListener(overlay);
            if (routeOverlay != null) {
                routeOverlay.removeFromMap();
            }
            routeOverlay = overlay;
            overlay.setData(transitRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        } else {
            Toast.makeText(this, "定位失败,请电话联系用户", Toast.LENGTH_SHORT).show();
        }

    }

    //驾车监听 结果回调
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null) {
            Toast.makeText(this, "抱歉未找到相关结果", Toast.LENGTH_SHORT).show();
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(baiduMap);
            baiduMap.setOnMarkerClickListener(overlay);
            if (routeOverlay != null) {
                routeOverlay.removeFromMap();
            }
            routeOverlay = overlay;
            overlay.setData(drivingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        } else {
            Toast.makeText(this, "定位失败,请电话联系用户", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    // 自定义行走路线覆盖方法
    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.location_start);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.order_icon);
            }
            return null;
        }
    }

    // 自定义驾车路线覆盖方法
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.location_start);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.order_icon);
            }
            return null;
        }
    }

    // 自定义行走路线覆盖方法
    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.location_start);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.order_icon);
            }
            return null;
        }
    }

    private boolean isFrist = true;

    /**
     * 定位获取位置信息监听
     */
    private class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(0)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            mLatitude = bdLocation.getLatitude(); // 获取经度
            mLongtitude = bdLocation.getLongitude(); // 获取纬度
            // 开启定位图层
            baiduMap.setMyLocationEnabled(true);
//            // 设置定位数据
            baiduMap.setMyLocationData(locData);
            if (isFrist) {
                myLocation();
                isFrist = false;
            }

            orderStart = new LatLng(mLatitude, mLongtitude);
            Log.d("LocationListener", "设置位置数据");
        }
    }

    /**
     * 定位到我的位置
     **/
    private void myLocation() {
        //获取自己的位置：经纬度
        LatLng latLng = new LatLng(mLatitude, mLongtitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.animateMapStatus(msu);//设置动画显示
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_right_Tv: // 进入热点地图
                Intent intent = new Intent(this, HeatMapActivity.class);
                startActivity(intent);
                break;

            case R.id.homeCancel_Tv:
                workDialog.dismiss(); // 对话框消失
                break;

            case R.id.homeSure_Tv:
                finish();             // 回到首页去开工
                break;
        }
    }

//    // 步行路线
//    private void routePlan() {
//        //起点与终点
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName(city, start);
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, end);
//        //步行路线规划
//        boolean res = search.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(enNode));
//    }
//
//    // 公交路线
//    private void busPlan() {
//        //起点与终点
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName(city, start);
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, end);
//        //公交路线规划
//        boolean res = search.transitSearch(new TransitRoutePlanOption().from(stNode).city("大连").to(enNode));
//    }
//
//    // 驾车路线
//    private void drivePlan() {
//        //起点与终点
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName(city, start);
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, end);
//        //驾车路线规划
//        boolean res = search.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
//    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
        baiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    /**
     * 将订单位置设置到地图上
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            switch (msg.what) {
                case 1: // 添加覆盖物
                    addOverlays(orders);
//                    if (!ISLOCATE) { // 如果不是第一次定位  则定位
//                        ISLOCATE = true;
//                        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(orderStart);
//                        baiduMap.animateMapStatus(msu);//设置动画显示
//                    }
                    myLocation();
                    break;

                case 2:
                    /* 覆盖物添加失败 */
                    break;

                case 10: // 抢单
                {
                    OrderList.DataMapEntity.OrdersEntity entity = (OrderList.DataMapEntity.OrdersEntity) msg.obj;
                    Intent intent = new Intent(OrderMapActivity.this, OrderDetailsActivity.class);
                    Bundle data = new Bundle();
                    data.putParcelable("orderStart", orderStart);
                    data.putSerializable("order", entity);
                    orderEnd = new LatLng(Double.valueOf(entity.getX()), Double.valueOf(entity.getY()));
                    data.putParcelable("end", orderEnd);
                    data.putSerializable("userInfo", userInfo);
                    intent.putExtras(data);
                    startActivity(intent);
                    Toast.makeText(OrderMapActivity.this, "抢单成功", Toast.LENGTH_SHORT).show();

                    Intent receiver = new Intent(); // 发送广播启动发送地理位置的服务
                    receiver.setAction("com.hengxun.builder.service");
                    sendBroadcast(receiver);
                }
                break;

                case 11:
                    /* 抢单失败 */
                    break;
            }
        }
    };

    /**
     * 获取订单列表
     */
    Runnable orderListTask = new Runnable() {

        @Override
        public void run() {
            Message message = Message.obtain();
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.TYPE, "2");
                map.put(AppConstants.WORKER_NO, userInfo.getDataMap().getWorker_no());
                map.put(AppConstants.PAGENO, "1");
                Date now = new Date();
                String time = String.valueOf(now.getTime());
                map.put(AppConstants.TIMESTAMP, time); // 当前时间时间戳
                String url = UrlUtils.addParams(AppApi.ORDERLIST, map); // 拼接参数
                Response response = OkHttpClientManager.getAsyn(url, OrderMapActivity.this);
                if (response.code() != 401) {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    OrderList list = gson.fromJson(result, OrderList.class);
                    if (list.getCode() == 401) {
                        errToken();
                    }
                    OrderList.DataMapEntity entity = list.getDataMap();
                    orders = entity.getOrders();
                    message.what = 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(message);
        }
    };


    /**
     * 改变订单状态--抢单
     *
     * @param订单状态 2 抢单
     */
    private class GetOrderTaskRunnable implements Runnable {
        private OrderList.DataMapEntity.OrdersEntity entity;

        GetOrderTaskRunnable setOrderEntity(OrderList.DataMapEntity.OrdersEntity entity) {
            this.entity = entity;
            return this;
        }

        @Override
        public void run() {
            HashMap<String, String> map = new HashMap<>();
            map.put(AppConstants.ORDER_ID, entity.getOrder_id());
            map.put(AppConstants.ORDER_STATUS, "2");
            map.put(AppConstants.PARAM, userInfo.getDataMap().getWorker_no());
            String url = UrlUtils.addParams(AppApi.ORDERSTATUS, map);
            Response status;
            Message message = Message.obtain();
            try {
                status = OkHttpClientManager.getAsyn(url, OrderMapActivity.this);
                if (status.code() != 401) {
//                    String result = status.body().string();
                    message.what = 10;
                    message.obj = entity;
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 11;
            }
            handler.sendMessage(message);
        }
    }

//    /**
//     * 网络操作相关的子线程
//     */
//    Runnable getRegionTask = new Runnable() {
//
//        @Override
//        public void run() {
//            try {
//                HashMap<String, String> map = new HashMap<>();
//                map.put(AppConstants.VERSION, "1");
//                String url = UrlUtils.addParams(AppApi.POSTCODES, map); // 拼接参数
//                Log.d("qwer", url);
//                Response response = OkHttpClientManager.getAsyn(url, OrderMapActivity.this);
////                Log.d("qwer1", response.toString());
//                if (response.code() != 401) {
//                    String result = response.body().string();
//                    Gson gson = new Gson();
//                    RegionList regionList = gson.fromJson(result, RegionList.class);
//                    RegionList.DataMapEntity dataMapEntity = regionList.getDataMap();
//                    List<RegionList.DataMapEntity.PostCodesEntity> codesEntities = dataMapEntity.getPostCodes();
//                    List<RegionList.DataMapEntity.PostCodesEntity.SubEntity> sub =
//                            codesEntities.get(0).getSub();
//                } else {
//
//                }
////                if (entity != null) {
////                    list = entity.getDataMap().getOrders();
//////                    EventBus.getDefault().post(list);
////                    Message message = new Message();
////                    handler.sendMessage(message);
////                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//        }
//    };

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onUserEvent(List<OrderList.DataMapEntity.OrdersEntity> event) {
        if (event != null) {
            addOverlays(event);
            Log.d("event----", "event------");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在后台线程执行
    public void onEvent(String str) {
        if (str.equals("123")) {
            new Thread(orderListTask).start(); // 获取匠人自己的订单列表
        }
    }

    /**
     * 添加覆盖物
     */
    private void addOverlays(List<OrderList.DataMapEntity.OrdersEntity> infors) {
        baiduMap.clear(); //清除上面的图层

        for (OrderList.DataMapEntity.OrdersEntity infor : infors) {
            String city = PreferenceUtil.readString(OrderMapActivity.this, AppConstants.WORKERSSP, AppConstants.CITY);
            GeoCodeOption option = new GeoCodeOption();
            option.city(city != null ? city : "大连市");
            option.address(infor.getAddress());
            Log.i("geanwen1", infor.getAddress() + "");
            GeoCoder coder = GeoCoder.newInstance();
            coder.setOnGetGeoCodeResultListener(getGeoCoderResultListener(infor));
            boolean c = coder.geocode(option);
            Log.i("geanwen1", c + "");
        }
    }

    private OnGetGeoCoderResultListener getGeoCoderResultListener(final OrderList.DataMapEntity.OrdersEntity entity) {
        return new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                } else {
                    LatLng latLng = null;
                    Marker marker = null;
                    OverlayOptions options;

                    //经纬度
                    latLng = geoCodeResult.getLocation();
                    Log.i("geanwen2", geoCodeResult.getAddress() + latLng.toString());
                    //图标
                    options = new MarkerOptions().position(latLng).icon(mMarker);
                    marker = (Marker) baiduMap.addOverlay(options);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("info", entity);
                    marker.setExtraInfo(bundle);

//                    try {
//                        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
//                        if (null != msu && null != baiduMap) {
//                            baiduMap.setMapStatus(msu);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
//        if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
//            Toast toast = Toast.makeText(OrderMapActivity.this, "查不到", Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 40);
//            toast.show();
//            return;
//        } else {
//            LatLng latLng = null;
//            Marker marker = null;
//            OverlayOptions options;
//
//            //经纬度
//            latLng = geoCodeResult.getLocation();
//            Log.i("geoCodeResult", geoCodeResult.getAddress() + latLng.toString());
//            //图标
//            options = new MarkerOptions().position(latLng).icon(mMarker);
//            marker = (Marker) baiduMap.addOverlay(options);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("info", bean);
//            marker.setExtraInfo(bundle);
//
//            Log.i("geoCodeResult", geoCodeResult.getAddress());
//        }
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
//            Toast toast = Toast.makeText(OrderMapActivity.this, "查不到", Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 40);
//            toast.show();
//            return;
//        } else {
//            LatLng latLng = null;
//            Marker marker = null;
//            OverlayOptions options;
//
//            //经纬度
//            latLng = reverseGeoCodeResult.getLocation();
//            Log.i("reverseGeoCodeResult", reverseGeoCodeResult.getAddress() + latLng.toString());
//            //图标
//            options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
//            marker = (Marker) baiduMap.addOverlay(options);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("info", bean);
//            marker.setExtraInfo(bundle);
//
//            Log.i("reverseGeoCodeResult", reverseGeoCodeResult.getAddress());
//        }
            }
        };
    }

//    @Override
//    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//
//    }
//
//    @Override
//    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//
//    }

    /**
     * 提示开工对话框
     */
    private void showNotWorkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_finish_work, null);
        builder.setView(view);
        workDialog = builder.create();

        // 设置对话框的view
        TextView homeCancel_Tv = (TextView) view.findViewById(R.id.homeCancel_Tv); // 取消
        TextView homeSure_Tv = (TextView) view.findViewById(R.id.homeSure_Tv); // 确定
        TextView dialogTitle_Tv = (TextView) view.findViewById(R.id.dialogTitle_Tv); // 标题
        dialogTitle_Tv.setText(R.string.map_dialog_title);
        homeSure_Tv.setOnClickListener(this);
        homeCancel_Tv.setOnClickListener(this);
        workDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (order_Mv != null) {
            order_Mv.onResume();
        }
        if (mLocationClient != null) {
            mLocationClient.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (order_Mv != null) {
            order_Mv.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        ISLOCATE = false;
        EventBus.getDefault().unregister(this);
        order_Mv.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        baiduMap.setMyLocationEnabled(false);
        super.onDestroy();
    }
}
