package com.hengxun.builder.view.activity.personal;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.model.Marks;
import com.hengxun.builder.model.UserInfo;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.adapter.MyMarksRvAdapter;
import com.hengxun.builder.view.widget.SwipeRefreshLoadingLayout;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ZY on 2016/3/17.
 * 总积分
 */
public class MyMraksActivity extends BaseActivity {
    public static MyMraksActivity finishActivity; // 注销销毁的activity
    private TextView myMarks_Tv; // 总积分
    private RecyclerView marks_RV;
    private MyMarksRvAdapter adapter;
    private List<Marks.DataMapEntity.PointsEntity> list = new ArrayList<>(); // 积分列表
    private List<Marks.DataMapEntity.PointsEntity> allList = new ArrayList<>();//
    private Marks.DataMapEntity entity;
    private UserInfo userInfo;
    private WaittingDiaolog dialog;
    private SwipeRefreshLoadingLayout marks_Sl;// 下拉刷新
    private String timeStmap;           // 刷新所需的时间戳  刷新用的是
    private int type = 1;                   // 1 刷新  2 加载
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_marks);
        showToolBar(getResources().getString(R.string.my_mark), true, this);
        initView();
        initData();
        setListener();
    }

    @Override
    protected void initView() {
        finishActivity = this;
        myMarks_Tv = (TextView) findViewById(R.id.myMarks_Tv);
        marks_RV = (RecyclerView) findViewById(R.id.marks_RV);
        marks_Sl = (SwipeRefreshLoadingLayout) findViewById(R.id.marks_Sl);
        adapter = new MyMarksRvAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        marks_RV.setLayoutManager(gridLayoutManager);
        marks_RV.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        dialog = new WaittingDiaolog(this);
        dialog.setCanceledOnTouchOutside(false);
//        dialog.setMessage("正在加载中...");
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        // 上拉加载
        marks_Sl.setOnLoadListener(new SwipeRefreshLoadingLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                type = 2;
                page++;
                new Thread(pointTask).start(); // 获取积分
            }
        });

        // 下拉刷新
        marks_Sl.setOnRefreshListener(new SwipeRefreshLoadingLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                type = 1;
                page = 1;
                new Thread(pointTask).start(); // 获取积分
            }
        });

        new Thread(pointTask).start(); // 获取积分
    }

    /**
     * 将积分集合装入适配器
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // ZY 2016年5月19日13:26:02
            if (msg.what != 0) {
                if (null != entity && !"".equals(entity.getTotalPoints())) {
                    myMarks_Tv.setText(String.valueOf(entity.getTotalPoints())); // 设置总积分
                }
                List<Marks.DataMapEntity.PointsEntity> list = entity.getPoints();
                if (type == 1) { // 刷新
                    adapter.addData(list);
                    marks_Sl.setRefreshing(false);
                } else if (type == 2) { // 加载
                    adapter.addDataList(allList);
                    marks_Sl.setLoading(false);
                }
                if (null != dialog && dialog.isShowing()) {
                    dialog.dismiss();
                }
            } else {
                if (null != dialog && dialog.isShowing()) {
                    dialog.dismiss();
                }
                marks_Sl.setRefreshing(false);
                marks_Sl.setLoading(false);
            }
        }
    };

    /**
     * 获取积分集合
     */
    Runnable pointTask = new Runnable() {

        @Override
        public void run() {
            Date date = new Date();
            long time = date.getTime();
            Message message = Message.obtain();
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.PAGENO, String.valueOf(page));  // 页码
                map.put(AppConstants.WORKER_ID, String.valueOf(userInfo.getDataMap().getWorker_no())); // 工号
                map.put(AppConstants.TIMESTAMP, time + "");
                String url = UrlUtils.addParams(AppApi.POINTS, map);
                Response response = OkHttpClientManager.getAsyn(url, MyMraksActivity.this);
                // 401 token失效
                if (response.code() != 401) {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    Marks marks = gson.fromJson(result, Marks.class);
                    if (marks.getCode() == 401) {
                        errToken();
                        return;
                    }
                    entity = marks.getDataMap();
                    list = entity.getPoints();
                    if (type == 1) {//刷新
                        message.what = 2;
                        allList.clear();
                        allList.addAll(list);
                    } else {//加载
                        message.what = 1;
                        allList.clear();
                        allList.addAll(list);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 0;
                return;
            }
            handler.sendMessage(message);
        }
    };

//    /**
//     * 登录获取匠人信息
//     */
//    Runnable loginTask = new Runnable() {
//
//        @Override
//        public void run() {
//            Message msg = new Message();
//            try {
//                String account = PreferenceUtil.readString(MyMraksActivity.this,
//                        SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);
//                String psw = PreferenceUtil.readString(MyMraksActivity.this,
//                        SharedPrefer.PSW, SharedPrefer.PSW);
//
//                HashMap<String, String> map = new HashMap<>();
//                map.put(AppConstants.USERNAME, account);
//                map.put(AppConstants.PSW, MD5Util.getMD5String(psw).toLowerCase(Locale.getDefault()));
//                map.put(AppConstants.OS, "1");
//                String url = UrlUtils.addParams(AppApi.LOGIN, map); // 拼接参数
//                Log.d("loginTask", url);
//                String response = OkHttpClientManager.getAsString(url);
//                Log.d("loginTask", response);
//                Gson gson = new Gson();
//                userInfo = gson.fromJson(response.toString(), UserInfo.class);
//                if (userInfo.getCode() == 200) {
//                    msg.what = 200;
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//            handler.sendMessageDelayed(msg, 2000);
//        }
//    };
}
