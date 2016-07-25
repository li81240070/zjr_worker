package com.hengxun.builder.view.activity.account;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.MD5Util;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.HomeActivity;
import com.hengxun.builder.view.activity.order.MineOrderActivity;
import com.hengxun.builder.view.activity.order.OrderDetailsActivity;
import com.hengxun.builder.view.activity.pay.WithdrawActivity;
import com.hengxun.builder.view.activity.personal.MyInfoActivity;
import com.hengxun.builder.view.activity.personal.MyMraksActivity;
import com.hengxun.builder.view.activity.personal.PersonalInfoActivity;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

/**
 * Created by ZY on 2016/3/25.
 * 忘记密码（重设）
 */
public class ModifyPswActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
    public static ModifyPswActivity finishActivity; // 注销销毁的activity

    private Button reset_nextStep_Btn;
    private EditText resetPswold_Et; // 输入旧密码
    private EditText resetPsw_Et, resetNewPsw_Et; // 输入新密码，确认新密码
    private String oldPsw, firstPsw, secondPsw;//第一次输入密码，第二次输入密码
    private boolean isNext = false;
    private String mobile; // 用户信息
    private String wrongMsg; // 密码修改错误时 服务器返回的信息
    private WaittingDiaolog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_psw);
        finishActivity = this;
        showToolBar(getResources().getString(R.string.reset_psw), true, this);
        initView();
        initData();
        setListener();
    }

    @Override
    protected void initView() {
        resetPswold_Et = (EditText) findViewById(R.id.resetPswold_Et);
        reset_nextStep_Btn = (Button) findViewById(R.id.reset_nextStep_Btn);
        resetPsw_Et = (EditText) findViewById(R.id.resetPsw_Et);
        resetNewPsw_Et = (EditText) findViewById(R.id.resetNewPsw_Et);
    }

    @Override
    protected void initData() {
        mobile = getIntent().getStringExtra("mobile");
    }

    @Override
    protected void setListener() {
        resetPsw_Et.setOnTouchListener(this);
        resetNewPsw_Et.setOnTouchListener(this);
        resetPsw_Et.addTextChangedListener(watcher);
        resetNewPsw_Et.addTextChangedListener(watcher);
        reset_nextStep_Btn.setOnClickListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        initStatue(v);
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_nextStep_Btn: // 下一步
                clickNextBtn();
                break;
        }
    }

    // 下一步按钮
    private void clickNextBtn() {
        oldPsw = resetPswold_Et.getText().toString().trim();
        firstPsw = resetPsw_Et.getText().toString().trim();
        secondPsw = resetNewPsw_Et.getText().toString().trim();

//        // 登录时保存的密码
//        String psw = PreferenceUtil.readString(this, SharedPrefer.PSW, SharedPrefer.PSW);

        if (TextUtils.isEmpty(oldPsw)) {
            Toast.makeText(ModifyPswActivity.this, getString(R.string.reset_input_old_psw), Toast.LENGTH_SHORT).show();
            return;
        }
//        else if (!oldPsw.equals(psw)) {
//            Toast.makeText(this, R.string.modify_old_psw_wrong, Toast.LENGTH_SHORT).show();
//        }

        if (TextUtils.isEmpty(firstPsw) || firstPsw.length() < 6 || firstPsw.length() > 12) {
            Toast.makeText(ModifyPswActivity.this, getString(R.string.reset_input_new_psw), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(secondPsw) || secondPsw.length() < 6 || secondPsw.length() > 12) {
            Toast.makeText(ModifyPswActivity.this, getString(R.string.reset_input_new_psw_anain), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!firstPsw.equals(secondPsw)) {
            Toast.makeText(ModifyPswActivity.this, getString(R.string.reset_input_new_error), Toast.LENGTH_SHORT).show();
            return;
        }

        dialog = new WaittingDiaolog(this);
        dialog.setCanceledOnTouchOutside(false);
//        dialog.setMessage("正在加载中...");
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        new Thread(networkTask).start(); // 向服务器上传密码
    }

    /**
     * 如果两次输入的密码一致  则可以进行下一步
     * 否则button不可点击
     */
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            /* nothing to do */
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /* nothing to do */
        }

        @Override
        public void afterTextChanged(Editable s) {
            firstPsw = resetPsw_Et.getText().toString().trim();
            secondPsw = resetNewPsw_Et.getText().toString().trim();
            if (firstPsw.equals(secondPsw) && !firstPsw.equals("")) {
                reset_nextStep_Btn.setBackgroundResource(R.drawable.shape_send_order_btn);
            } else {
                reset_nextStep_Btn.setBackgroundResource(R.drawable.shape_forget_psw_btn);
            }
        }
    };

    // 清空状态  并改变点击的控件状态
    private void initStatue(View v) {
        resetPsw_Et.setBackgroundResource(R.drawable.shape_forget_not_psw_edit);
        resetNewPsw_Et.setBackgroundResource(R.drawable.shape_forget_not_psw_edit);
        resetPsw_Et.clearFocus();
        resetNewPsw_Et.clearFocus();
        v.setBackgroundResource(R.drawable.shape_forget_psw_edit);
        v.requestFocus();
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

        if (PersonalInfoActivity.finishActivity != null) {
            PersonalInfoActivity.finishActivity.finish();
        }

    }

    /**
     * 注销登录
     */
    Runnable loginOutTask = new Runnable() {

        @Override
        public void run() {
            int worker_no = PreferenceUtil.readInteger(ModifyPswActivity.this, SharedPrefer.ACCOUNT, SharedPrefer.WORKERID);
            Message msg = Message.obtain();
            try {
                OkHttpClientManager.Param params[] = new OkHttpClientManager.Param[]{
                        new OkHttpClientManager.Param(AppConstants.WORKER_NO, String.valueOf(worker_no))
                };
                // 注销登录
                Response test = OkHttpClientManager.post(AppApi.LOGOUT, ModifyPswActivity.this, params[0]);
                if (test.code() != 401) {
                    // 重设成功并跳转
                    msg.what = 200;
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            handler.sendMessage(msg);
        }
    };

    /**
     * 跳转到完成页面
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (msg.what == 200) {
                PreferenceUtil.clearPrefs(getApplicationContext(), SharedPrefer.ACCOUNT);
                PreferenceUtil.clearPrefs(getApplicationContext(), SharedPrefer.PSW);
                PreferenceUtil.clearPrefs(getApplicationContext(), SharedPrefer.TOKEN);
                PreferenceUtil.clearPrefs(getApplicationContext(), SharedPrefer.WORKERID);

                Toast.makeText(ModifyPswActivity.this, getString(R.string.reset_input_success), Toast.LENGTH_SHORT).show();

                finishActivity();
                Intent intent = new Intent(ModifyPswActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else if (msg.what == 0) {
                Toast.makeText(ModifyPswActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 10) {
                if (null != wrongMsg) {
                    Toast.makeText(ModifyPswActivity.this, wrongMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /**
     * 重设密码
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            Message msg = Message.obtain();
            msg.what = -1;
            try {
                String worker_no = PreferenceUtil.readString(ModifyPswActivity.this,
                        SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);
                secondPsw = resetNewPsw_Et.getText().toString().trim();
                OkHttpClientManager.Param params[] = new OkHttpClientManager.Param[]{
                        //参数名 工号
                        new OkHttpClientManager.Param(AppConstants.WORKER_NO, worker_no)
                        //参数名 旧密码
                        , new OkHttpClientManager.Param(AppConstants.CHANGE_OLD_PWD, MD5Util.getMD5String(oldPsw))
                        //参数名 newpwd
                        , new OkHttpClientManager.Param(AppConstants.CHANGE_NEW_PWD, MD5Util.getMD5String(secondPsw))
                };
                Response forgerPwd = OkHttpClientManager.post(AppApi.CHANGEPWD, ModifyPswActivity.this, params[0], params[1], params[2]);
                if (forgerPwd.code() != 401) {
                    JSONObject jsonObject = null;
                    String result = forgerPwd.body().string();
                    int code = 0;
                    try {
                        jsonObject = new JSONObject(result);
                        if (jsonObject != null && jsonObject.getString("code").equals("200")) {
                            code = jsonObject.getInt("code");
                        } else if (jsonObject != null && jsonObject.getString("code").equals("401")) {
                            errToken();
                        } else {
                            wrongMsg = jsonObject.getString("message");
                            msg.what = 10;
                            handler.sendMessage(msg);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg.what = 0;
                    }
                    if (code == 200) {
                        new Thread(loginOutTask).start();
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


}
