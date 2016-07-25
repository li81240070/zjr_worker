package com.hengxun.builder.view.activity.personal;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.model.PersonalAccount;
import com.hengxun.builder.model.UserInfo;
import com.hengxun.builder.model.WithdrawNumber;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.MD5Util;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.pay.WithdrawActivity;
import com.hengxun.builder.view.adapter.PersonalRvAdapter;
import com.hengxun.builder.view.widget.SwipeRefreshLoadingLayout;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZY on 2016/3/25.
 * 个人账户
 */
public class PersonalAccountActivity extends BaseActivity {
    public static PersonalAccountActivity finishActivity; // 注销销毁的activity

    private Button personalWithdraw_btn;    // 提现按钮
    private UserInfo userInfo;              // 用户信息
    private RecyclerView personal_Rv;       // 账户信息列表
    private PersonalRvAdapter adapter;
    private TextView personalAmount_Tv;     // 匠人账户余额
    private List<PersonalAccount.DataMapEntity.IncomesEntity> entityList = new ArrayList<>(); // 账户列表
    private WaittingDiaolog dialog;
    private SwipeRefreshLoadingLayout personalCountRv;// 下拉刷新
    private int type = 1;                   // 1 刷新  2 加载
    private int page = 1;
    private List<PersonalAccount.DataMapEntity.IncomesEntity> allList = new ArrayList<>();
    //    private WithdrawNumber.DataMapEntity entity; // 提现金额实体
    private WithdrawNumber number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finishActivity = this;
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_personal_account);
        showToolBar(getResources().getString(R.string.personal_account), true, this);
        dialog = new WaittingDiaolog(this);
        dialog.setCanceledOnTouchOutside(false);
//        dialog.setMessage("正在加载中...");
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        new Thread(loginTask).start();
//        initView();
//        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        personalWithdraw_btn = (Button) findViewById(R.id.personalWithdraw_btn);
        personal_Rv = (RecyclerView) findViewById(R.id.personal_Rv);
        personalAmount_Tv = (TextView) findViewById(R.id.personalAmount_Tv);
        personalCountRv = (SwipeRefreshLoadingLayout) findViewById(R.id.personalCountRv);
    }

    @Override
    protected void initData() {
        super.initData();
//        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        double d = userInfo.getDataMap().getAmount();
        String st = String.format("%.2f", d);
        personalAmount_Tv.setText(st); // 账户余额

        // 提现
        personalWithdraw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 先判断是不是周二  若不是周二不可提现 数据通过接口获得
                new Thread(getAmountTask).start();
//                int date = Integer.parseInt(DateUtil.StringData());
//                if (date != 2) {
//                    Toast.makeText(PersonalAccountActivity.this, "今天不是提现日, 请于下周二进行提现", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent intent = new Intent(PersonalAccountActivity.this, WithdrawActivity.class);
//                    Bundle data = new Bundle();
//                    data.putSerializable("userInfo", userInfo);
//                    intent.putExtras(data);
//                    startActivity(intent);
//                }
            }
        });
        new Thread(incomesTask).start(); // 获取账户列表

        adapter = new PersonalRvAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        personal_Rv.setLayoutManager(gridLayoutManager);
        personal_Rv.setAdapter(adapter);

        // 上拉加载
        personalCountRv.setOnLoadListener(new SwipeRefreshLoadingLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                type = 2;
                page++;
                new Thread(incomesTask).start(); // 获取积分
            }
        });

        // 下拉刷新
        personalCountRv.setOnRefreshListener(new SwipeRefreshLoadingLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                type = 1;
                page = 1;
                new Thread(incomesTask).start(); // 获取积分
            }
        });

    }

    /**
     * 提现成功后刷新列表
     */
    @Subscribe
    public void onEventMainThread(String str) {
        new Thread(incomesTask).start();
    }

    /**
     * 获取账户列表
     */
    Runnable incomesTask = new Runnable() {

        @Override
        public void run() {
            Message msg = Message.obtain();
            try {
                Date date = new Date();
                long time = date.getTime();
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.WORKER_NO, userInfo.getDataMap().getWorker_no());
                map.put(AppConstants.TOKEN, userInfo.getDataMap().getToken());
                map.put(AppConstants.PAGENO, page + "");
                map.put(AppConstants.TIMESTAMP, time + "");
                String url = UrlUtils.addParams(AppApi.INCOMES, map);
                Response response = OkHttpClientManager.getAsyn(url, PersonalAccountActivity.this);
                if (response.code() != 401) {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    PersonalAccount account = gson.fromJson(result, PersonalAccount.class);
                    if (account.getCode() == 401) {
                        errToken();
                    } else if (account.getCode() == 200) {
                        PersonalAccount.DataMapEntity entity = account.getDataMap();
                        entityList = entity.getIncomes();
                        if (entityList != null) {
                            if (type == 1) {
                                msg.what = 1;
                                allList.clear();
                                allList.addAll(entityList);
                            } else {
                                msg.what = 2;
                                allList.clear();
                                allList.addAll(entityList);
                            }
                        }
                    } else {
                        msg.what = 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.what = 0;
            }
            handler.sendMessage(msg);
        }
    };

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
//            if (msg.what == 200) {
//                initView();//这里葛安文感觉有点问题
//                initData();
//            } else if (msg.what == 1) {//刷新
//                adapter.addData(entityList);
//                personalCountRv.setRefreshing(false);
//            } else if (msg.what == 2) {
//                adapter.addDataLoad(allList);
//                personalCountRv.setLoading(false);
//            }

            switch (msg.what) {
                case 0:

                    break;

                case 1: // 刷新
                    adapter.addData(entityList);
                    personalCountRv.setRefreshing(false);
                    break;
                case 2:
                    adapter.addDataLoad(allList);
                    personalCountRv.setLoading(false);
                    break;
                case 10: // 可以提现
                    WithdrawNumber.DataMapEntity entity = number.getDataMap();
                    Intent intent = new Intent(PersonalAccountActivity.this, WithdrawActivity.class);
                    Bundle data = new Bundle();
                    data.putSerializable("userInfo", userInfo);
                    data.putSerializable("money", entity);
                    intent.putExtras(data);
                    startActivity(intent);
                    break;

                case 20: // 不能提现
                    if (number != null && number.getMessage() != null) {
                        Toast.makeText(PersonalAccountActivity.this, number.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 200:
                    initView();
                    initData();
                    break;
            }
        }
    };

    /**
     * 获取匠人信息
     */
    Runnable loginTask = new Runnable() {

        @Override
        public void run() {
            Message msg = new Message();
            String account = PreferenceUtil.readString(PersonalAccountActivity.this,
                    SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);
            String psw = PreferenceUtil.readString(PersonalAccountActivity.this,
                    SharedPrefer.PSW, SharedPrefer.PSW);
            String token = PreferenceUtil.readString(PersonalAccountActivity.this, SharedPrefer.TOKEN, SharedPrefer.TOKEN);
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.USERNAME, account);
                map.put(AppConstants.PSW, MD5Util.getMD5String(psw).toLowerCase(Locale.getDefault()));
                map.put(AppConstants.OS, "2");
                map.put(AppConstants.TOKEN, token != null ? token : "");
                String url = UrlUtils.addParams(AppApi.LOGIN, map); // 拼接参数
                Response response = OkHttpClientManager.getAsyn(url, PersonalAccountActivity.this);
                if (response.code() != 401) {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    userInfo = gson.fromJson(result, UserInfo.class);
                    if (userInfo.getCode() == 401) {
                        errToken();
                        return;
                    }
                    String _token;
                    if (null != userInfo.getDataMap().getToken()) {
                        _token = userInfo.getDataMap().getToken();
                        PreferenceUtil.savePerfs(PersonalAccountActivity.this, SharedPrefer.TOKEN, SharedPrefer.TOKEN, _token);
                    } else {
                        _token = token;
                    }
                    if (userInfo.getCode() == 200) {
                        msg.what = 200;
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
        }
    };

    /**
     * 获取提现金额
     */
    Runnable getAmountTask = new Runnable() {
        @Override
        public void run() {
            Message message = Message.obtain();
            try {
                String worker_no = PreferenceUtil.readString(PersonalAccountActivity.this,
                        SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.WORKER_NO, worker_no);
                String url = UrlUtils.addParams(AppApi.GETAMOUNT, map);
                Response response = OkHttpClientManager.getAsyn(url, PersonalAccountActivity.this);
                if (response.code() != 401) {
                    Gson gson = new Gson();
                    String result = response.body().string();
                    number = gson.fromJson(result, WithdrawNumber.class);
                    if (number.getCode() == 200) { // 200 说明今天是星期二  并且可以提现
                        message.what = 10;
                    } else if (number.getCode() == 401) {
                        errToken();
                    } else { // 不能提现 把服务器给的数据提示给用户
                        message.what = 20;
                    }
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
