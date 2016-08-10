package com.hengxun.builder.view.activity.order;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.model.OrderList;
import com.hengxun.builder.model.UserInfo;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.pay.PayActivity;
import com.hengxun.builder.view.adapter.MineOrderRvAdapter;
import com.hengxun.builder.view.widget.SwipeRefreshLoadingLayout;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ZY on 2016/3/11.
 * 我的订单
 */
public class MineOrderActivity extends BaseActivity {
    public static MineOrderActivity finishActivity; // 注销销毁的activity

    private RecyclerView mineOrder_Rv;  // 我的订单列表
    private ImageView noOrder_Iv;       // 没有订单列表的显示
    private MineOrderRvAdapter adapter; // 我的订单
    private UserInfo userInfo;          // 匠人信息
    private List<OrderList.DataMapEntity.OrdersEntity> list; // 匠人的订单列表
    private List<OrderList.DataMapEntity.OrdersEntity> allList = new ArrayList<>();
    private LatLng orderStart;          // 工人位置
    private LatLng orderEnd;            // 订单位置
    private SwipeRefreshLoadingLayout mineOrder_Rl; // 下拉刷新
    private WaittingDiaolog dialog;
    private int type = 1;                   // 1 刷新 2 加载
    private int page = 1;
    private int typeMy = 1;
//////////////////////////////////////////////////
    private CountDownTimer timer;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_mine_order);
        showToolBar(getResources().getString(R.string.mine_order), true, this);
        initView();
        initData();
/////////////////////////////////////////////////
        mediaPlayer = MediaPlayer.create(this,R.raw.music);

//        mediaPlayer.create(this,R.raw.music);
//        try {
//            mediaPlayer.prepare();//缓冲
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //增加声音选项
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                timer=new CountDownTimer(Integer.MAX_VALUE,1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        dialog = new WaittingDiaolog(MineOrderActivity.this);
//                        dialog.setCanceledOnTouchOutside(false);
////                dialog.setMessage("正在加载中...");
//                        if (dialog != null && !dialog.isShowing()) {
//                            dialog.show();
//                        }
//                        type = 1;
//                        page = 1;
//                        typeMy = 1;
//                        new Thread(orderListTask).start(); // 获取匠人自己的订单列表
//                    }
//
//                    @Override
//                    public void onFinish() {
//
//                    }
//                }.start();
//            }
//        }).start();
        ///////////////////////////////////////////////
    }

    @Override
    protected void initView() {
        super.initView();
        mineOrder_Rl = (SwipeRefreshLoadingLayout) findViewById(R.id.mineOrder_Rl);
        mineOrder_Rv = (RecyclerView) findViewById(R.id.mineOrder_Rv);
        noOrder_Iv = (ImageView) findViewById(R.id.noOrder_Iv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mineOrder_Rv.setLayoutManager(gridLayoutManager);

        adapter = new MineOrderRvAdapter(this);
        adapter.setOnItemClickListener(new MineOrderRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 如果订单付款完成  或者已经取消  则不可见
                if (Integer.parseInt(allList.get(position).getOrder_status()) >= 7 ||
                        Integer.parseInt(allList.get(position).getOrder_status()) == 0 ||
                        Integer.parseInt(allList.get(position).getOrder_status()) == 5
                        ) {
                        /* nothing to do */
                } else if (allList.get(position).getOrder_status().equals("6")) {
                    Intent intent = new Intent(MineOrderActivity.this, PayActivity.class);
                    Bundle data = new Bundle();
//                    data.putSerializable("order", list.get(position));
                    intent.putExtra("orderId", allList.get(position).getOrder_id());
                    intent.putExtras(data);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MineOrderActivity.this, OrderDetailsActivity.class);
                    Bundle data = new Bundle();
                    LatLng latLng = new LatLng(Double.parseDouble(allList.get(position).getX()), Double.parseDouble(allList.get(position).getY()));
                    data.putParcelable("start", orderStart);
                    data.putParcelable("end", latLng);
                    data.putSerializable("order", allList.get(position));
                    data.putSerializable("userInfo", userInfo);
                    intent.putExtras(data);
                    startActivity(intent);
                }
            }
        });
        mineOrder_Rv.setAdapter(adapter);

//        mineOrder_Rl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                dialog = new ProgressDialog(MineOrderActivity.this);
//                dialog.setCanceledOnTouchOutside(false);
//                dialog.setMessage("正在加载中...");
//                if (dialog != null && !dialog.isShowing()) {
//                    dialog.show();
//                }
//                new Thread(orderListTask).start(); // 获取匠人自己的订单列表
//            }
//        });

        // 下拉刷新
        mineOrder_Rl.setOnRefreshListener(new SwipeRefreshLoadingLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dialog = new WaittingDiaolog(MineOrderActivity.this);
                dialog.setCanceledOnTouchOutside(false);
//                dialog.setMessage("正在加载中...");
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
                type = 1;
                page = 1;
                typeMy = 1;
                new Thread(orderListTask).start(); // 获取匠人自己的订单列表
            }
        });

        // 上拉加载
        mineOrder_Rl.setOnLoadListener(new SwipeRefreshLoadingLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                dialog = new WaittingDiaolog(MineOrderActivity.this);
                dialog.setCanceledOnTouchOutside(false);
//                dialog.setMessage("正在加载中...");
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
                type = 1;
                typeMy = 2;
                page++;
                new Thread(orderListTask).start(); // 获取匠人自己的订单列表
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        orderStart = getIntent().getParcelableExtra("orderStart");
        orderEnd = getIntent().getParcelableExtra("orderEnd");
        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo"); // 获取用户信息
        dialog = new WaittingDiaolog(MineOrderActivity.this);
        dialog.setCanceledOnTouchOutside(false);
//        dialog.setMessage("正在加载中...");
        if (dialog != null && !dialog.isShowing()) {

            dialog.show();

        }

        new Thread(orderListTask).start(); // 获取匠人自己的订单列表
    }

    /**
     * ZY 2016年5月19日16:22:00
     * 样式没有按照UI设计来  而是参照ios修改
     */
    @Override
    protected void showToolBar(String titleName, boolean isShow, Activity activity) {
        super.showToolBar(titleName, isShow, activity);
//        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.mine_toolbar));
//        ImageView toolbar_left_Ib = (ImageView) activity.findViewById(R.id.toolbar_left_Ib);
//        toolbar_left_Ib.setImageResource(R.mipmap.white_narrow);
//        TextView toolbarTitle_Tv = (TextView) activity.findViewById(R.id.toolbarTitle_Tv);
//        toolbarTitle_Tv.setTextColor(getResources().getColor(R.color.white));


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

    /**
     * 更新列表
     */
    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:

                    break;

                case 1:
                    if (list != null) {
                        adapter.addData(list);
                    }
                    mineOrder_Rl.setRefreshing(false);
                    break;
                case 2:
                    if (list != null) {
                        adapter.addDataList(list);
                    }
                    mineOrder_Rl.setLoading(false);
                    break;
            }
            if (allList == null || allList.size() == 0) {
                noOrder_Iv.setVisibility(View.VISIBLE);
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    };

    /**
     * 取匠人自己所有的订单列表
     */
    Runnable orderListTask = new Runnable() {

        @Override
        public void run() {
            Date date = new Date();
            long time = date.getTime();
            HashMap<String, String> map = new HashMap<>();
            map.put(AppConstants.WORKER_NO, userInfo.getDataMap().getWorker_no());
            map.put(AppConstants.TYPE, String.valueOf(type)); // type为1时是匠人自己的订单  2是所有的订单
            map.put(AppConstants.TIMESTAMP, time + "");// 不管是不是刷新或者加载 都可以拼接当前时间
            map.put(AppConstants.PAGENO, String.valueOf(page));
//            if (type == 1) { // 刷新
//                Date now = new Date();
//                String time = String.valueOf(now.getTime());
//                Log.i("geanwen列表刷新", )
//                map.put(AppConstants.TIMESTAMP, time); // 当前时间时间戳
//                PreferenceUtil.savePerfs(MineOrderActivity.this,
//                        SharedPrefer.ORDERLISTTIME, SharedPrefer.ORDERLISTTIME, time);
//
//            } else { // 加载
//                String timestmap = PreferenceUtil.readString(MineOrderActivity.this,
//                        SharedPrefer.ORDERLISTTIME, SharedPrefer.ORDERLISTTIME);
//                map.put(AppConstants.TIMESTAMP, timestmap); // 上次刷新时的时间戳
//
//            }

            String url = UrlUtils.addParams(AppApi.ORDERLIST, map); // 拼接参数
            Response response;
            Message msg = Message.obtain();
            try {
                response = OkHttpClientManager.getAsyn(url, MineOrderActivity.this);
                if (response.code() != 401) {
                    if (response.code() == 200) {
                        try {
                            String result = response.body().string();
                            Gson gson = new Gson();
                            OrderList orderList = gson.fromJson(result, OrderList.class);
                            if (orderList.getCode() == 401) {
                                errToken();
                                return;
                            }
                            OrderList.DataMapEntity entity = orderList.getDataMap();
                            list = entity.getOrders();
                            if (typeMy == 1) {
                                msg.what = 1;
                                allList.clear();
                                allList.addAll(list);
                            } else {
                                msg.what = 2;
                                allList.addAll(list);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == 401) {
                        errToken();
                    }
                } else if (response.code() == 401){
                    errToken();
                } else {
                    msg.what = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.what = 0;
            }
            handler.sendMessage(msg);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN) //在后台线程执行
    public void onUserEvent(String str) {
        // 微信支付成功后
        // 获取匠人自己的订单列表
        if (str.equals("123") || str.equals("wx0")) {
//            dialog = new ProgressDialog(MineOrderActivity.this);
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setMessage("正在加载中...");
//            if (dialog != null && !dialog.isShowing()) {
//                dialog.show();
//            }
            new Thread(orderListTask).start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String str) {
        if (str.equals("refresh")) {
//            mineOrder_Rl.setRefreshing(true);
            dialog = new WaittingDiaolog(MineOrderActivity.this);
            dialog.setCanceledOnTouchOutside(false);
//            dialog.setMessage("正在加载中...");
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
            type = 1;
            page = 1;

            new Thread(orderListTask).start(); // 获取匠人自己的订单列表
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
