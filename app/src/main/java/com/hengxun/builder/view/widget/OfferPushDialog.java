//package com.hengxun.builder.view.widget;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ZoomControls;
//
//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.BitmapDescriptor;
//import com.baidu.mapapi.map.BitmapDescriptorFactory;
//import com.baidu.mapapi.map.MapStatusUpdate;
//import com.baidu.mapapi.map.MapStatusUpdateFactory;
//import com.baidu.mapapi.map.MarkerOptions;
//import com.baidu.mapapi.map.OverlayOptions;
//import com.baidu.mapapi.map.TextureMapView;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.search.core.SearchResult;
//import com.baidu.mapapi.search.geocode.GeoCodeResult;
//import com.baidu.mapapi.search.geocode.GeoCoder;
//import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
//import com.baidu.mapapi.search.route.BikingRouteResult;
//import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
//import com.baidu.mapapi.search.route.DrivingRouteResult;
//import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
//import com.baidu.mapapi.search.route.PlanNode;
//import com.baidu.mapapi.search.route.RoutePlanSearch;
//import com.baidu.mapapi.search.route.TransitRouteResult;
//import com.baidu.mapapi.search.route.WalkingRouteResult;
//import com.baidu.mapapi.utils.DistanceUtil;
//import com.hengxun.builder.R;
//import com.hengxun.builder.model.BacklogOrder;
//import com.hengxun.builder.model.HelpOrder;
//import com.hengxun.builder.model.OrderPush;
//import com.hengxun.builder.utils.baidumap.jarfile.DrivingRouteOverlay;
//import com.hengxun.builder.utils.baidumap.jarfile.OverlayManager;
//import com.hengxun.builder.utils.widget.DateUtil;
//
//import java.math.BigDecimal;
//
///**
// * 订单信息的弹出框
// * Created by ge on 2016/3/28.
// */
//public class OfferPushDialog extends Dialog implements View.OnClickListener, OnGetRoutePlanResultListener, OnGetGeoCoderResultListener {
//
//    private Context context;
//    private int wid, hei;
//    private ImageView closeDialogBtn; //右上角×
//    private TextureMapView order_infor_map;
//    private BaiduMap baiduMap;
//    private RelativeLayout order_infor_map_title;//地图title
//    //订单详情到地图详情按钮, dialog标题, 底部按钮上面的字, 项目名称, 项目价格, 订单时间, 订单地址
//    private TextView order_to_mapTV, title_ordorTV, order_bottom_btn_textTV, subject_orderTV, subject_priceTV, time_orderTV, address_orderTV;
//    private LinearLayout order_mapLL, order_gutLL;
//    private RoutePlanSearch search;//计划路线相关
//    OverlayManager routeOverlay = null;
//    private ImageButton order_bottomBtn;//底部圆钮
//    private int isKnock; // 是否是抢单状态 1:抢单 2：派单 3:抢险抢修
//    private BacklogOrder.DataMapEntity.OfferOrdersEntity bean;//订单对象
//    private HelpOrder helpOrder;
//    private double mLatitude, mLongtitude;
//    private LocationListener locationListener;
//    private LocationClient mLocationClient;
//
//    private OnOrderClickListener onOrderClickListener;
//    private HelpOrderClickListener helpOrderClickListener;
//
//    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
//        this.onOrderClickListener = onOrderClickListener;
//    }
//
//    //计划路线假数据
//    String start = "";
//    String end = "";
//    String city = "大连";
//
//    /**
//     * wid 屏幕宽
//     * hei 屏幕高
//     * isknock 是否是抢单dialog true：是抢单 false：是派单
//     * ml 两个参数是我的（匠人）位置 : 目的是计划路线需要
//     **/
//    public OfferPushDialog(Context context, int wid, int hei, int isKnock, BacklogOrder.DataMapEntity.OfferOrdersEntity bean,
//                           double mLatitude, double mLongtitude) {
//        super(context, R.style.OrderInforDialog);
//        this.context = context;
//        this.wid = wid;
//        this.hei = hei;
//        this.isKnock = isKnock;
//        this.bean = bean;
//        this.mLatitude = mLatitude;
//        this.mLongtitude = mLongtitude;
//        init();
//    }
//
//    public OfferPushDialog(Context context, int wid, int hei, int isKnock, HelpOrder bean,
//                           double mLatitude, double mLongtitude) {
//        super(context, R.style.OrderInforDialog);
//        this.context = context;
//        this.wid = wid;
//        this.hei = hei;
//        this.isKnock = isKnock;
//        this.helpOrder = bean;
//        this.mLatitude = mLatitude;
//        this.mLongtitude = mLongtitude;
//        init();
//    }
//
//    private void init() {
//        setContentView(R.layout.dialog_order_infor);
//        initView();
//        initData();
//        setOnClick();
//
//        Window window = getWindow();
//        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.width = wid;
//        params.height = hei;
//        window.setAttributes(params);
//    }
//
//    private void initView() {
//        closeDialogBtn = (ImageView) findViewById(R.id.closeDialogBtn);//关闭本dialog按钮
//        order_infor_map = (TextureMapView) findViewById(R.id.order_infor_map);//地图
//        order_infor_map_title = (RelativeLayout) findViewById(R.id.order_infor_map_title);//地图title布局
//        order_to_mapTV = (TextView) findViewById(R.id.order_to_mapTV);//订单详情到地图详情按钮
//        order_mapLL = (LinearLayout) findViewById(R.id.order_mapLL);//订单地图总布局
//        order_gutLL = (LinearLayout) findViewById(R.id.order_gutLL);//订单详情布局
//        order_bottomBtn = (ImageButton) findViewById(R.id.order_bottomBtn);//底部按钮
//        title_ordorTV = (TextView) findViewById(R.id.title_ordorTV);//dialog标题
//        order_bottom_btn_textTV = (TextView) findViewById(R.id.order_bottom_btn_textTV);//底部按钮上面的字
//        subject_orderTV = (TextView) findViewById(R.id.subject_orderTV);//项目名称
//        subject_priceTV = (TextView) findViewById(R.id.subject_priceTV);//项目价格
//        time_orderTV = (TextView) findViewById(R.id.time_orderTV);//订单时间
//        address_orderTV = (TextView) findViewById(R.id.address_orderTV);//订单地址
//    }
//
//    private void initData() {
//        baiduMap = order_infor_map.getMap();
//        baiduMap.setMyLocationEnabled(true);
//        MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(13.0f);
//        baiduMap.setMapStatus(update);
//
//        LatLng orderStart = new LatLng(mLatitude, mLongtitude); // 订单起始位置
//        if (isKnock == 1) { //抢单状态
//            String x, y;
//            if (helpOrder != null) {
//                x = helpOrder.getDataMap().getDetail().getX();
//                y = helpOrder.getDataMap().getDetail().getY();
//            } else {
//                x = String.valueOf(bean.getDataMap().getX());
//                y = String.valueOf(bean.getDataMap().getY());
//            }
//            LatLng orderEnd = new LatLng(Float.parseFloat(x), Float.parseFloat(y)); // 订单结束位置
//            double dis = getDistance(orderStart, orderEnd); // 距离
//            if (dis > 1000) { // 单位 米
//                BigDecimal b = new BigDecimal(dis / 1000);
//                dis = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
//                Log.d("OrderInforDialog", "dis:" + dis);
//                title_ordorTV.setText("距你" + String.valueOf(dis) + "公里");
//            } else {
//                int distance = (int) dis;
//                title_ordorTV.setText("距你" + String.valueOf(distance) + "米");
//            }
//
//            order_bottom_btn_textTV.setText("抢单");
//            if (bean != null) {
//                subject_orderTV.setText(bean.getDataMap().getService().getGroup_name());//项目名
//                if (bean.getDataMap().getTotal_price() >= 0)
//                    subject_priceTV.setText("￥ " + bean.getDataMap().getTotal_price());//价格
//                time_orderTV.setText(DateUtil.getStrTime(String.valueOf(bean.getDataMap().getAppoint_time())) + "");//时间
//                address_orderTV.setText(bean.getDataMap().getAddress());//地址
//                end = bean.getDataMap().getAddress();
//            }
//        } else if (isKnock == 2) { //派单
//            String x, y;
//            if (helpOrder != null) {
//                x = helpOrder.getDataMap().getDetail().getX();
//                y = helpOrder.getDataMap().getDetail().getY();
//            } else {
//                x = String.valueOf(bean.getDataMap().getX());
//                y = String.valueOf(bean.getDataMap().getY());
//            }
//            LatLng orderEnd = new LatLng(Float.parseFloat(x), Float.parseFloat(y)); // 订单结束位置
//            double dis = getDistance(orderStart, orderEnd); // 距离
//            if (dis > 1000) { // 单位 米
//                BigDecimal b = new BigDecimal(dis / 1000);
//                dis = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
//                Log.d("OrderInforDialog", "dis:" + dis);
//                title_ordorTV.setText("距你" + String.valueOf(dis) + "公里");
//            } else {
//                int distance = (int) dis;
//                title_ordorTV.setText("距你" + String.valueOf(distance) + "米");
//            }
//
//            order_bottom_btn_textTV.setText("抢单");
//            if (bean != null) {
//                subject_orderTV.setText(bean.getDataMap().getService().getGroup_name());//项目名
//                if (bean.getDataMap().getTotal_price() >= 0)
//                    subject_priceTV.setText("￥ " + bean.getDataMap().getTotal_price());//价格
//                time_orderTV.setText(DateUtil.getStrTime(String.valueOf(bean.getDataMap().getAppoint_time())) + "");//时间
//                address_orderTV.setText(bean.getDataMap().getAddress());//地址
//                end = bean.getDataMap().getAddress();
//            }
//
//            title_ordorTV.setText("派单");
//            order_bottom_btn_textTV.setText("接单");
//            closeDialogBtn.setVisibility(View.GONE);
//        } else if (isKnock == 3) { // 抢险抢修
//            String x, y;
//            if (helpOrder != null) {
//                x = helpOrder.getDataMap().getDetail().getX();
//                y = helpOrder.getDataMap().getDetail().getY();
//            } else {
//                x = String.valueOf(bean.getDataMap().getX());
//                y = String.valueOf(bean.getDataMap().getY());
//            }
//            LatLng orderEnd = new LatLng(Float.parseFloat(x), Float.parseFloat(y)); // 订单结束位置
//            double dis = getDistance(orderStart, orderEnd); // 距离
//            if (dis > 1000) { // 单位 米
//                BigDecimal b = new BigDecimal(dis / 1000);
//                dis = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
//                Log.d("OrderInforDialog", "dis:" + dis);
//                title_ordorTV.setText("距你" + String.valueOf(dis) + "公里");
//            } else {
//                int distance = (int) dis;
//                title_ordorTV.setText("距你" + String.valueOf(distance) + "米");
//            }
//
//            if (bean != null) {
//                subject_orderTV.setText(helpOrder.getDataMap().getDetail().getService());//项目名
//                if (helpOrder.getDataMap().getDetail().getPrice().equals("0"))
//                    subject_priceTV.setText("￥ " + helpOrder.getDataMap().getDetail().getPrice());//价格
//                time_orderTV.setText(DateUtil.getStrTime(String.valueOf(helpOrder.getDataMap().getDetail().getAppoint_time())) + "");//时间
//                address_orderTV.setText(helpOrder.getDataMap().getDetail().getAddress());//地址
//                end = helpOrder.getDataMap().getDetail().getAddress();
//            }
//
//            title_ordorTV.setText("抢险抢修");
//            order_bottom_btn_textTV.setText("接单");
//            closeDialogBtn.setVisibility(View.GONE);
//        } else {
//            /* nothing to do */
//        }
//        int count = order_infor_map.getChildCount();
//        for (int i = 0; i < count; i++) {
//            View child = order_infor_map.getChildAt(i);
//            // 隐藏百度logo ZoomControl
//            if (child instanceof ZoomControls) {
//                child.setVisibility(View.INVISIBLE);
//            }
//        }
//
//        LatLng latLng = new LatLng(mLatitude, mLongtitude);
//
//        // 经纬度查询地址
//        GeoCoder coder = GeoCoder.newInstance();
//        coder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
//        coder.setOnGetGeoCodeResultListener(this);
//
//        baiduMap = order_infor_map.getMap();
//        search = RoutePlanSearch.newInstance(); //导航相关方法
//    }
//
//    /**
//     * 初始化定位
//     */
//    private void initLocation() {
//        // 初始化定位方法
//        locationListener = new LocationListener();
//        mLocationClient = new LocationClient(context);
//
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
//        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        mLocationClient.setLocOption(option);
//
//        mLocationClient.registerLocationListener(locationListener);    //注册监听函数
//        mLocationClient.start(); //开始定位
//    }
//
//    private void setOnClick() {
//        closeDialogBtn.setOnClickListener(this);
//        order_infor_map_title.setOnClickListener(this);
//        order_to_mapTV.setOnClickListener(this);
//        search.setOnGetRoutePlanResultListener(this);
//        order_bottomBtn.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.closeDialogBtn://关闭dialog按钮
//                this.dismiss();
//                break;
//            case R.id.order_infor_map_title://地图到详情
//                order_gutLL.setVisibility(View.VISIBLE);
//                order_mapLL.setVisibility(View.GONE);
//                break;
//            case R.id.order_to_mapTV://详情到地图
//                order_gutLL.setVisibility(View.GONE);
//                order_mapLL.setVisibility(View.VISIBLE);
//                drivePlan();
//                break;
//            case R.id.order_bottomBtn://底部圆钮
//                if (onOrderClickListener != null) {
//                    onOrderClickListener.onOrderclick(order_bottomBtn, isKnock, bean);
//                }
//                break;
//        }
//    }
//
//
//    //驾车路线
//    private void drivePlan() {
//        //起点与终点
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName(city, start);
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, end);
//        //驾车路线规划
//        boolean res = search.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
//    }
//
//
//    private boolean useDefaultIcon = true; // 导航图标状态
//
//    @Override
//    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
//
//    }
//
//    @Override
//    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
//
//    }
//
//    @Override
//    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
//        if (drivingRouteResult == null) {
//            Toast.makeText(context, "抱歉未找到相关结果", Toast.LENGTH_SHORT).show();
//        }
//        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
//            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(baiduMap);
//            baiduMap.setOnMarkerClickListener(overlay);
//            if (routeOverlay != null) {
//                routeOverlay.removeFromMap();
//            }
//            routeOverlay = overlay;
//            overlay.setData(drivingRouteResult.getRouteLines().get(0));
//            overlay.addToMap();
//            overlay.zoomToSpan();
//        }
//    }
//
//    @Override
//    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
//
//    }
//
//    /**
//     * 定位获取位置信息监听
//     */
//    private class LocationListener implements BDLocationListener {
//
//        @Override
//        public void onReceiveLocation(BDLocation bdLocation) {
//            start = bdLocation.getAddress().address;
//        }
//    }
//
//    //根据经纬度检索地址回调
//    @Override
//    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//        start = geoCodeResult.getAddress();
//        drivePlan();//路线
//    }
//
//    @Override
//    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
//            Toast toast = Toast.makeText(context, "查不到", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 40);
//            toast.show();
//            return;
//        } else {
//            LatLng latLng = reverseGeoCodeResult.getLocation();
//            BitmapDescriptor mMarker = BitmapDescriptorFactory.fromResource(R.mipmap.order_location);
//            OverlayOptions options = new MarkerOptions().position(latLng).icon(mMarker);
//            baiduMap.addOverlay(options);
//
//            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult.getLocation());
//            baiduMap.animateMapStatus(msu);
//            Log.i("geanwen", reverseGeoCodeResult.getAddress());
//        }
//    }
//
//    // 定制RouteOverly
//    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
//
//        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//        }
//
//        @Override
//        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.mipmap.location_start);
//            }
//            return null;
//        }
//    }
//
//
//    public interface OnOrderClickListener {
//        void onOrderclick(View view, int isKnoic, OrderPush bean);
//    }
//
//    public interface HelpOrderClickListener {
//        void onOrderclick(View view, int isKnoic, HelpOrder bean);
//    }
//
//    /**
//     * 计算两点之间距离
//     *
//     * @param start
//     * @param end
//     * @return 米
//     */
//    public double getDistance(LatLng start, LatLng end) {
//        double d = DistanceUtil.getDistance(start, end);
////        double lat1 = (Math.PI / 180) * start.latitude;
////        double lat2 = (Math.PI / 180) * end.latitude;
////
////        double lon1 = (Math.PI / 180) * start.longitude;
////        double lon2 = (Math.PI / 180) * end.longitude;
////
//////      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//////      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//////      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//////      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);
////
////        //地球半径
////        double R = 6371;
////
////        //两点间距离 km，如果想要米的话，结果*1000就可以了
////        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;
//
//        return d;
//    }
//
//}
