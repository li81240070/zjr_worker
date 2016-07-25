package com.hengxun.builder.view.activity.map;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.model.HeatLocation;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ZY on 2016/3/11.
 */
public class HeatMapActivity extends BaseActivity {
    private MapView mapView; // 地图view
    private BaiduMap baiduMap;
    private LocationClient mLocationClient; // 定位
    private LocationListener locationListener;  // 定位监听
    private double mLatitude, mLongtitude; // 经度  纬度
    private WaittingDiaolog dialog;

    private HeatMap heatmap;
    private boolean isDestroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_map);
        showToolBar(getResources().getString(R.string.map_hot), true, this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mapView = (MapView) findViewById(R.id.mapView);
    }

    @Override
    protected void initData() {
        super.initData();
        baiduMap = mapView.getMap();
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));


        locationListener = new LocationListener();
        mLocationClient = new LocationClient(this);     //声明LocationClient类
        initLocation(); // 初始化定位方法
        mLocationClient.registerLocationListener(locationListener);    //注册监听函数
        myLocation();
        mLocationClient.start();//开始定位

        //开启热力图
//        baiduMap.setBaiduHeatMapEnabled(true);
        addHeatMap();
    }

    /**
     * 定位获取位置信息监听
     */
    private class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mLatitude = bdLocation.getLatitude(); // 获取经度
            mLongtitude = bdLocation.getLongitude(); // 获取纬度

//            // 开启定位图层
//            baiduMap.setMyLocationEnabled(true);
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            // 设置定位数据
            baiduMap.setMyLocationData(locData);

            // 经纬度
            LatLng latLng = new LatLng(mLatitude, mLongtitude);

            //移动到第一个涂层的位置
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.setMapStatus(msu);
            myLocation();
            Log.d("LocationListener", "设置位置数据");

            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(bdLocation.getTime());
            sb.append("\nerror code : ");
            sb.append(bdLocation.getLocType());
            sb.append("\nlatitude : ");
            sb.append(bdLocation.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(bdLocation.getLongitude());
            sb.append("\nradius : ");
            sb.append(bdLocation.getRadius());
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(bdLocation.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(bdLocation.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(bdLocation.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(bdLocation.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(bdLocation.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(bdLocation.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(bdLocation.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(bdLocation.getLocationDescribe());// 位置语义化信息
            List<Poi> list = bdLocation.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }

    /**
     * 初始化定位方法
     */
    private void initLocation() {
        MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(17.0f);
        baiduMap.setMapStatus(update);
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 0;
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
     * 添加热力图
     */
    private void addHeatMap() {
        final Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (!isDestroy) {
                    baiduMap.addHeatMap(heatmap);
                }
            }
        };

        dialog = new WaittingDiaolog(HeatMapActivity.this);
        dialog.setCanceledOnTouchOutside(false);
//        dialog.setMessage("正在加载中...");
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<LatLng> data = getLocations();
                if (data != null && data.size() > 0) {
                    try {
                        heatmap = new HeatMap.Builder().data(data).radius(50).build();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                h.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        // activity 销毁时同时销毁地图控件
        mapView.onDestroy();
    }

    /**
     * 获取热力图点
     */
    private List<LatLng> getLocations() {
        HashMap<String, String> map = new HashMap<>();
        map.put(AppConstants.VERSION, "0");  // 设置参数
        String url = UrlUtils.addParams(AppApi.GETHOT, map); // 拼接参数

        Response response;
        List<LatLng> list = new ArrayList<>();
        try {
            response = OkHttpClientManager.getAsyn(url, HeatMapActivity.this);
            if (response.code() != 401) {
                String result = response.body().string();
                Gson gson = new Gson();
                // 热点位置数据
                HeatLocation location = gson.fromJson(result, HeatLocation.class);
                if (location.getCode() == 401) {
                    errToken();
                }
                HeatLocation.DataMapEntity hotDataEntities = location.getDataMap();
                List<HeatLocation.DataMapEntity.HotDataEntity> entity = hotDataEntities.getHotData();

                for (int i = 0; i < entity.size(); i++) {
                    double lat = entity.get(i).getLatitude();
                    double lng = entity.get(i).getLongtitude();
                    list.add(new LatLng(lat, lng));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        List<LatLng> list = new ArrayList<>();
//        InputStream inputStream = getResources().openRawResource(R.raw.locations);
//        String json = new Scanner(inputStream).useDelimiter("\\A").next();
//        JSONArray array;
//        try {
//            array = new JSONArray(json);
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject object = array.getJSONObject(i);
//                double lat = object.getDouble("lat");
//                double lng = object.getDouble("lng");
//                list.add(new LatLng(lat, lng));
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        return list;
    }
}
