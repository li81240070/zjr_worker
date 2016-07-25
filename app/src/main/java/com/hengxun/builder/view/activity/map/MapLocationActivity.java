package com.hengxun.builder.view.activity.map;

import android.app.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.utils.baidumap.jarfile.DrivingRouteOverlay;
import com.hengxun.builder.utils.baidumap.jarfile.OverlayManager;
import com.hengxun.builder.utils.baidumap.jarfile.TransitRouteOverlay;
import com.hengxun.builder.utils.baidumap.jarfile.WalkingRouteOverlay;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.view.activity.BaseActivity;

/**
 * Created by ZY on 2016/3/29.
 * 定位页面
 */
public class MapLocationActivity extends BaseActivity implements View.OnClickListener, OnGetRoutePlanResultListener {
    private boolean ISLOCATE = false;

    private MapView location_Mv;  // 百度地图view
    private BaiduMap baiduMap;    // 百度地图操作
    private RoutePlanSearch search; // 计划路线相关
    private OverlayManager routeOverlay = null;
    private boolean useDefaultIcon = false; // 导航图标状态

    private LocationClient mLocationClient;     // 定位相关
    private LocationListener locationListener;
    private double mLatitude, mLongtitude; // 经度  纬度
    private ImageView bus_Iv, driver_Iv, walk_Iv; // 公交 驾车 步行导航

    // 当前所在城市
//    private String city = "大连";
    private LatLng start; // 起始位置
    private LatLng end;   // 结束位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        showToolBarView(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        location_Mv = (MapView) findViewById(R.id.location_Mv);
    }

    @Override
    protected void initData() {
        super.initData();
//        start = getIntent().getParcelableExtra("start");
        end = getIntent().getParcelableExtra("end");

        baiduMap = location_Mv.getMap();
        // 导航相关方法
        search = RoutePlanSearch.newInstance();
        search.setOnGetRoutePlanResultListener(this);

        // 定位相关方法
        locationListener = new LocationListener();
        mLocationClient = new LocationClient(this);     //声明LocationClient类
        initLocation(); // 初始化定位方法
        mLocationClient.registerLocationListener(locationListener);    //注册监听函数
        mLocationClient.start();//开始定位
        myLocation();

    }

    @Override
    protected void showToolBar(String titleName, boolean isShow, Activity activity) {
        super.showToolBar(titleName, isShow, activity);
    }

    private void initLocation() {
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(14.0f);
        baiduMap.setMapStatus(update);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 10000; // 10秒钟定一次位
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

    /**
     * 定位到我的位置
     **/
    private void myLocation() {
        //获取自己的位置：经纬度
        LatLng latLng = new LatLng(mLatitude, mLongtitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.animateMapStatus(msu);//设置动画显示
    }

    /**
     * 定位获取位置信息监听
     */
    private class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mLatitude = bdLocation.getLatitude(); // 获取经度
            mLongtitude = bdLocation.getLongitude(); // 获取纬度

            start = new LatLng(mLatitude, mLongtitude);
            // 开启定位图层
            baiduMap.setMyLocationEnabled(true);
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            // 设置定位数据
            baiduMap.setMyLocationData(locData);

            // 如果不是第一次定位  则定位
            if (!ISLOCATE) {
                ISLOCATE = true;
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(start);
                baiduMap.animateMapStatus(msu);//设置动画显示

                // 默认定位car
                drivePlan();
                driver_Iv.setImageResource(R.mipmap.driver_full);
            }
            Log.d("LocationListener", "设置位置数据");
        }
    }

    //步行监听 结果回调
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        if (walkingRouteResult == null) {
            Toast.makeText(this, "定位失败,请电话联系用户", Toast.LENGTH_SHORT).show();
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
        }
//        else {
//            Toast.makeText(this, "定位失败,请电话联系用户", Toast.LENGTH_SHORT).show();
//        }
    }

    //公交监听 结果回调
    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        if (transitRouteResult == null) {
            Toast.makeText(this, "定位失败,请电话联系用户", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "定位失败, 请更换定位方式", Toast.LENGTH_SHORT).show();
        }
    }

    //步行路线
    private void routePlan() {
        //起点与终点
        PlanNode stNode = PlanNode.withLocation(start);
        PlanNode enNode = PlanNode.withLocation(end);
        //步行路线规划
        boolean res = search.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(enNode));
        if (!res) {
            Toast.makeText(this, "定位失败,请电话联系用户", Toast.LENGTH_SHORT).show();
        }
    }

    //公交路线
    private void busPlan() {
        //起点与终点
        PlanNode stNode = PlanNode.withLocation(start);
        PlanNode enNode = PlanNode.withLocation(end);
        //公交路线规划
        String city = PreferenceUtil.readString(MapLocationActivity.this, AppConstants.WORKERSSP, AppConstants.CITY);
        boolean res = search.transitSearch(new TransitRoutePlanOption().from(stNode).city(city != null ? city : "大连市").to(enNode));

        if (!res) {
            Toast.makeText(this, "定位失败,请电话联系用户", Toast.LENGTH_SHORT).show();
        }
    }

    //驾车路线
    private void drivePlan() {
        //起点与终点
        PlanNode stNode = PlanNode.withLocation(start);
        PlanNode enNode = PlanNode.withLocation(end);
        //驾车路线规划
        boolean res = search.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));

        if (!res) {
            Toast.makeText(this, "定位失败,请电话联系用户", Toast.LENGTH_SHORT).show();
        }
    }

    //驾车监听 结果回调
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null) {
            Toast.makeText(this, "定位失败,请电话联系用户", Toast.LENGTH_SHORT).show();
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
        }
//        else {
//            Toast.makeText(this, "定位失败,请电话联系用户", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    //自定义行走路线覆盖方法
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

    // 定制RouteOverly
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

    /**
     * toolbarView
     */
    protected void showToolBarView(Activity activity) {
        Toolbar toolbar_view = (Toolbar) activity.findViewById(R.id.toolbar_view);
        ImageView toolbar_left_Ib = (ImageView) activity.findViewById(R.id.toolbar_left_Ib);
        toolbar_left_Ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bus_Iv = (ImageView) activity.findViewById(R.id.bus_Iv);  // 公交导航
        bus_Iv.setOnClickListener(this);
        driver_Iv = (ImageView) activity.findViewById(R.id.driver_Iv);  // 驾车导航
        driver_Iv.setOnClickListener(this);
        walk_Iv = (ImageView) activity.findViewById(R.id.walk_Iv);  // 步行导航
        walk_Iv.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        location_Mv.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        location_Mv.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        location_Mv.onDestroy();
        ISLOCATE = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bus_Iv: // 公交导航
                resetIcon();
                busPlan();
                bus_Iv.setImageResource(R.mipmap.bus_full);
                break;

            case R.id.driver_Iv: // 驾车导航
                resetIcon();
                drivePlan();
                driver_Iv.setImageResource(R.mipmap.driver_full);
                break;

            case R.id.walk_Iv: // 步行导航
                resetIcon();
                routePlan();
                walk_Iv.setImageResource(R.mipmap.walk_full);
                break;
        }
    }

    /**
     * 重置图标
     */
    private void resetIcon() {
        bus_Iv.setImageResource(R.mipmap.bus_empty);
        driver_Iv.setImageResource(R.mipmap.driver_empty);
        walk_Iv.setImageResource(R.mipmap.walk_empty);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
        baiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }
}
