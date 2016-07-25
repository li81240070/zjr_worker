package com.hengxun.builder.view.activity.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.model.UserInfo;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.MD5Util;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.HomeActivity;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by ZY on 2016/3/7.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    public static LoginActivity finishActivity; // 注销销毁的activity

    private Button login_Btn; // 登录按钮
    private TextView login_forget_psw_Tv;   // 忘记密码
    private EditText login_obtainPhone_Et;  // 输入手机号或者工号
    private EditText login_pbtainPsw_Et;    // 输入密码
    //    private ProgressBar login_pb;           // 登录等待
//    private LinearLayout login_Ll;
    private UserInfo userInfo;
    private WaittingDiaolog dialog;

    private String account;                 // 用户输入的账号 修改变量名number ==> account 定义更准确
    private String psw;                     // 用户输入的密码

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        finishActivity = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        showToolBar(getResources().getString(R.string.login), true, this);
        login_Btn = (Button) findViewById(R.id.login_Btn);
        login_forget_psw_Tv = (TextView) findViewById(R.id.login_forget_psw_Tv);
        login_obtainPhone_Et = (EditText) findViewById(R.id.login_obtainPhone_Et);
        login_pbtainPsw_Et = (EditText) findViewById(R.id.login_pbtainPsw_Et);

//        login_Ll = (LinearLayout) findViewById(R.id.login_Ll);
//        login_pb = (ProgressBar) findViewById(R.id.login_pb);
    }

    @Override
    protected void initData() {
        super.initData();
        login_Btn.setOnClickListener(this);
        login_forget_psw_Tv.setOnClickListener(this);
        login_pbtainPsw_Et.setOnEditorActionListener(this);

        /* 若本地存有账号则将账号显示 */
        String account = PreferenceUtil.readString(LoginActivity.this,
                SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);
        if (!TextUtils.isEmpty(account)) {
            login_obtainPhone_Et.setText(account);
        }
    }

    @Override
    protected void showToolBar(String titleName, boolean isShow, Activity activity) {
        super.showToolBar(titleName, isShow, activity);
        ImageView toolbar_left_Ib = (ImageView) activity.findViewById(R.id.toolbar_left_Ib);
        toolbar_left_Ib.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 登录跳转到主页
            case R.id.login_Btn:
                clickLoginBtn();
                break;
            // 找回密码
            case R.id.login_forget_psw_Tv:
                Intent intent = new Intent(this, ForgetPswActivity.class);
                startActivity(intent);
//                finish();
                break;
        }
    }

    private void clickLoginBtn() {
        account = login_obtainPhone_Et.getText().toString().trim();  // 账号
        psw = login_pbtainPsw_Et.getText().toString().trim();       // 密码

        dialog = new WaittingDiaolog(this);
        dialog.setCanceledOnTouchOutside(false);
//        dialog.setMessage("正在加载中...");
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        new Thread(loginTask).start();
    }

    /**
     * 登录后跳转
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (msg.what == 200) {
//                handler.removeCallbacks(loginTask);
                // 存储账号密码
                PreferenceUtil.savePerfs(LoginActivity.this, SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT, account);
                PreferenceUtil.savePerfs(LoginActivity.this, SharedPrefer.PSW, SharedPrefer.PSW, psw);
                PreferenceUtil.savePerfs(LoginActivity.this, SharedPrefer.TOKEN, SharedPrefer.TOKEN, userInfo.getDataMap().getToken());
                PreferenceUtil.savePerfs(LoginActivity.this, SharedPrefer.WORKERID, SharedPrefer.WORKERID, userInfo.getDataMap().getUserId());

                Intent toMain = new Intent(LoginActivity.this, HomeActivity.class);
                toMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle data = new Bundle();
                data.putSerializable("userInfo", userInfo);
                toMain.putExtras(data);
                startActivity(toMain);
                finish();
            } else if (msg.what == 400) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "工人工号或者密码错误", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(LoginActivity.this, "工人工号或者密码错误", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 登录获取匠人信息
     */
    Runnable loginTask = new Runnable() {

        @Override
        public void run() {
            String token = PreferenceUtil.readString(LoginActivity.this, AppConstants.TOKEN, AppConstants.TOKEN);
            Message msg = Message.obtain();
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put(AppConstants.USERNAME, account);
                map.put(AppConstants.PSW, MD5Util.getMD5String(psw).toLowerCase(Locale.getDefault()));
                map.put(AppConstants.OS, "2");
                map.put(AppConstants.TOKEN, token != null ? token : "");
                String url = UrlUtils.addParams(AppApi.LOGIN, map); // 拼接参数
                Response response = OkHttpClientManager.getAsString(url, LoginActivity.this);
                String result = response.body().string();

                Gson gson = new Gson();
                userInfo = gson.fromJson(result, UserInfo.class);
                if (userInfo != null && userInfo.getCode() == 200) {
                    PreferenceUtil.savePerfs(LoginActivity.this, SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT, account);
                    PreferenceUtil.savePerfs(LoginActivity.this, SharedPrefer.PSW, SharedPrefer.PSW, psw);
                    PreferenceUtil.savePerfs(LoginActivity.this, SharedPrefer.TOKEN, SharedPrefer.TOKEN, userInfo.getDataMap().getToken());
                    PreferenceUtil.savePerfs(LoginActivity.this, SharedPrefer.WORKERID, SharedPrefer.WORKERID, userInfo.getDataMap().getUserId());

                    Intent toMain = new Intent(LoginActivity.this, HomeActivity.class);
                    toMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle data = new Bundle();
                    data.putSerializable("userInfo", userInfo);
                    toMain.putExtras(data);
                    startActivity(toMain);
                    finish();
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            super.run();
//                            sendRegistrationId(userInfo.getDataMap().getUserId(), userInfo.getDataMap().getToken());
//                        }
//                    }.start();
                } else if (userInfo.getCode() == 401) {
                    errToken();
                } else {
                    msg.what = 400;
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.what = 400;
            }
            handler.sendMessage(msg);
        }
    };

//    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

//    /**
//     * 向服务器发送registrationId
//     **/
//    private void sendRegistrationId(int userId, String token) {
//        String url = AppApi.REGISTIONID;
//        String registrationId = PreferenceUtil.readString(LoginActivity.this, AppConstants.WORKERSSP, AppConstants.REGISTRATIONID);
//        JSONObject object = new JSONObject();
//        try {
//            object.put(AppConstants.REGISTRATIONID, registrationId);
//            object.put(AppConstants.WORKER_ID, userId);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        RequestBody body = RequestBody.create(JSON, object.toString());
//        OkHttpClient okHttpClient = null;
//        try {
//            okHttpClient = HttpsController.setCertificates(LoginActivity.this, LoginActivity.this.getAssets().open("zhenjren.cer"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Request request = new Request.Builder().url(url).post(body).addHeader(AppConstants.TOKEN, token).build();
//        Response response = null;
//        Message msg = Message.obtain();
//
//        try {
//            response = okHttpClient.newCall(request).execute();
//            String s = response.body().string();
//            if (response.isSuccessful()) {
//                if (401 != response.code()) {
//                    msg.what = 200;
//                }
//            } else {
//                /* geanwen向服务器注册极光失败 */
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        handler.sendMessage(msg);
//
//    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            clickLoginBtn();//回车登陆
            //隐藏软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        }
        return false;
    }
}
