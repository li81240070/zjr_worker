package com.hengxun.builder.view.activity.personal;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.model.UserInfo;
import com.hengxun.builder.service.LocationService;
import com.hengxun.builder.utils.okhttp.HttpsController;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.HomeActivity;
import com.hengxun.builder.view.activity.account.ForgetPswActivity;
import com.hengxun.builder.view.activity.account.LoginActivity;
import com.hengxun.builder.view.activity.account.ModifyPswActivity;
import com.hengxun.builder.view.activity.order.MineOrderActivity;
import com.hengxun.builder.view.activity.order.OrderDetailsActivity;
import com.hengxun.builder.view.activity.pay.WithdrawActivity;
import com.hengxun.builder.view.widget.CircleImage;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by ZY on 2016/3/10.
 */
public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener {
    public static PersonalInfoActivity finishActivity;

    private UserInfo userInfo;           // 匠人个人信息
    private CircleImage personAvater_Iv; // 匠人头像
    private TextView personName_Tv;      // 匠人昵称
    private Button exitLogin_Btn;        // 退出登录
    private TextView personSex_Tv;       // 匠人性别
    private TextView personMobile_Tv;    // 匠人手机号
    private TextView personType_Tv;      // 匠人类别
    private RelativeLayout personPsw_Rl; // 修改密码
    private WaittingDiaolog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finishActivity = this;
        setContentView(R.layout.activity_personal_info);
        showToolBar(getResources().getString(R.string.person_info), true, this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        exitLogin_Btn = (Button) findViewById(R.id.exitLogin_Btn);
        personName_Tv = (TextView) findViewById(R.id.personName_Tv);
        personAvater_Iv = (CircleImage) findViewById(R.id.personAvater_Iv);
        personPsw_Rl = (RelativeLayout) findViewById(R.id.personPsw_Rl);
        personMobile_Tv = (TextView) findViewById(R.id.personMobile_Tv);
        personType_Tv = (TextView) findViewById(R.id.personType_Tv);
        personSex_Tv = (TextView) findViewById(R.id.personSex_Tv);
    }

    @Override
    protected void initData() {
        super.initData();
        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        exitLogin_Btn.setOnClickListener(this);
        personPsw_Rl.setOnClickListener(this);
        if (userInfo != null) {
            final String imgurl = AppApi.ORDER_IMG + userInfo.getDataMap().getAvatar() + "_200.jpg";
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        getAvater(imgurl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
//            Picasso.with(this).load(imgurl).error(R.mipmap.default_head).into(personAvater_Iv);
            personName_Tv.setText(userInfo.getDataMap().getReal_name());
            // 匠人工作标签
            StringBuilder tags = new StringBuilder();
            for (int i = 0; i < userInfo.getDataMap().getTags().size(); i++) {
                tags.append(userInfo.getDataMap().getTags().get(i));
                tags.append(" ");
            }
            personType_Tv.setText(tags.toString().trim());
            personMobile_Tv.setText(userInfo.getDataMap().getMobile());
            personSex_Tv.setText(userInfo.getDataMap().getGender());
        }
    }

    /**
     * 下载头像
     **/
    public void getAvater(String avatarUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        client = HttpsController.setCertificates(PersonalInfoActivity.this, PersonalInfoActivity.this.getAssets().open("zhenjren.cer"));
        try {
            Request request = new Request.Builder().url(avatarUrl).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                InputStream is = response.body().byteStream();
                final Bitmap bm = BitmapFactory.decodeStream(is);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        personAvater_Iv.setImageBitmap(bm);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exitLogin_Btn:
                dialog = new WaittingDiaolog(this);
                dialog.setCanceledOnTouchOutside(false);
//                dialog.setMessage("正在加载中...");
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
                new Thread(networkTask).start();
                break;

            case R.id.personPsw_Rl: // 修改密码
                Intent intent = new Intent(this, ModifyPswActivity.class);
                intent.putExtra("mobile", userInfo.getDataMap().getMobile());
                startActivity(intent);
                break;
        }
    }

    /**
     * 销毁所有需要处理数据的activity
     */
    private void finishActivity() {
        if (HomeActivity.finishActivity != null) {
            HomeActivity.finishActivity.finish();
        }

        if (MyMraksActivity.finishActivity != null) {
            MyMraksActivity.finishActivity.finish();
        }

        if (MyInfoActivity.finishActivity != null) {
            MyInfoActivity.finishActivity.finish();
        }

        if (WithdrawActivity.finishActivity != null) {
            WithdrawActivity.finishActivity.finish();
        }

        if (OrderDetailsActivity.finishActivity != null) {
            OrderDetailsActivity.finishActivity.finish();
        }

        if (MineOrderActivity.finishActivity != null) {
            MineOrderActivity.finishActivity.finish();
        }

        if (ForgetPswActivity.finishActivity != null) {
            ForgetPswActivity.finishActivity.finish();
        }

        if (ModifyPswActivity.finishActivity != null) {
            ModifyPswActivity.finishActivity.finish();
        }

//        if (ResetCompleteActivity.finishActivity != null) {
//            ResetCompleteActivity.finishActivity.finish();
//        }

    }

    /**
     * 主线程中操作
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finishActivity();
            JPushInterface.stopPush(PersonalInfoActivity.this);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Intent stopService = new Intent(PersonalInfoActivity.this, LocationService.class);
            stopService(stopService);

            Intent toLogin = new Intent(PersonalInfoActivity.this, LoginActivity.class);
            startActivity(toLogin);
            finish();
        }
    };

    /**
     * 注销登录
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // 清除缓存的账号和密码
            PreferenceUtil.clearPrefs(getApplicationContext(), SharedPrefer.PSW);
            PreferenceUtil.clearPrefs(getApplicationContext(), SharedPrefer.TOKEN);
            PreferenceUtil.clearPrefs(getApplicationContext(), SharedPrefer.WORKERID);
            Message msg = Message.obtain();
            try {
                OkHttpClientManager.Param params[] = new OkHttpClientManager.Param[]{
                        new OkHttpClientManager.Param(AppConstants.WORKER_NO, userInfo.getDataMap().getWorker_no())
                };
                // 注销登录
                Response test = OkHttpClientManager.post(AppApi.LOGOUT, PersonalInfoActivity.this, params[0]);
                if (test.code() == 200) {
                    handler.sendMessage(msg);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    };
}
