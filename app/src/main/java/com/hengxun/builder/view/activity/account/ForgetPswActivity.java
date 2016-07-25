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

import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.model.ForgetPswMessageEntity;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.UrlUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.Response;

import java.util.HashMap;

/**
 * Created by ZY on 2016/3/7.
 */
public class ForgetPswActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    public static ForgetPswActivity finishActivity; // 注销销毁的activity

    private EditText phoneEditET, messageEditET;//手机号， 验证码输入
    private Button sendMessageBtn, nextBtn;//发送按钮, 下一步
    private String phone, message;
    private boolean isSend = false, isNext = false;
    private ForgetPswMessageEntity entity;
    private WaittingDiaolog dialog;

    private int time = 60;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (time > 0) {
                isSend = false;
                sendMessageBtn.setText(time + "秒");
                time = time - 1;
                handler.postDelayed(runnable, 1000);
            } else {
                time = 60;
                phone = phoneEditET.getText().toString().trim();
                if (phone.length() == 11) {
                    isSend = true;
                    sendMessageBtn.setText("验证");
                    sendMessageBtn.setBackgroundResource(R.drawable.shape_send_order_btn);
                } else {
                    isSend = false;
                    sendMessageBtn.setText("验证");
                    sendMessageBtn.setBackgroundResource(R.drawable.shape_forget_psw_btn);
                }
            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finishActivity = this;
        setContentView(R.layout.activity_forget_pwd_new);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        String titleName = getString(R.string.forgrt_psw);
        showToolBar(titleName, true, this);
        phoneEditET = (EditText) findViewById(R.id.phoneEditET);
        messageEditET = (EditText) findViewById(R.id.messageEditET);
        sendMessageBtn = (Button) findViewById(R.id.sendMessageBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);
    }

    @Override
    protected void initData() {
        handler = new Handler();
        phoneEditET.setOnClickListener(this);
        messageEditET.setOnClickListener(this);
        sendMessageBtn.setOnClickListener(this);
        phoneEditET.setOnTouchListener(this);
        messageEditET.setOnTouchListener(this);
        nextBtn.setOnClickListener(this);
        phoneEditET.addTextChangedListener(new MyTextWathch(phoneEditET));
        messageEditET.addTextChangedListener(new MyTextWathch(messageEditET));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phoneEditET://输入手机号
                initStatue(phoneEditET);
                break;
            case R.id.messageEditET: //输入验证码
                initStatue(messageEditET);
                break;
            case R.id.sendMessageBtn://发送按钮
                clickSendBtn();
                break;
            case R.id.nextBtn://下一步
                clickNextBtn();
                break;
        }
    }

    //点击发送验证码按钮
    private void clickSendBtn() {
        phone = phoneEditET.getText().toString();
        if (isSend) {
            handler.post(runnable);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    requestCode();
                }
            }.start();

        }
    }

    //下一步按钮
    private void clickNextBtn() {
        if (isNext) {
            if (handler != null) {
//                handler.removeCallbacks(runnable);
                if (entity != null && !TextUtils.isEmpty(entity.getDataMap().getVaildCode())) {
                    if (entity.getDataMap().getVaildCode().equals(messageEditET.getText().toString().trim())) {
                        Intent intent = new Intent(ForgetPswActivity.this, ResetPswActivity.class);
                        intent.putExtra("mobile", phone);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgetPswActivity.this, "验证码有误.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgetPswActivity.this, "验证码有误.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //清空状态  并改变点击的控件状态
    private void initStatue(View v) {
        phoneEditET.setBackgroundResource(R.drawable.shape_forget_not_psw_edit);
        messageEditET.setBackgroundResource(R.drawable.shape_forget_not_psw_edit);
        phoneEditET.clearFocus();
        messageEditET.clearFocus();
        v.setBackgroundResource(R.drawable.shape_forget_psw_edit);
        v.requestFocus();
        textWatcherLisener();//监听edittext输入情况
    }

    private void textWatcherLisener() {
        phone = phoneEditET.getText().toString();
        message = messageEditET.getText().toString();
        if ((!phone.equals("")) && (!message.equals(""))) {
            isNext = true;
            nextBtn.setBackgroundResource(R.drawable.shape_send_order_btn);
        } else {
            isNext = false;
            nextBtn.setBackgroundResource(R.drawable.shape_forget_psw_btn);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        initStatue(v);
        return false;
    }


    // 多个edittext监听
    class MyTextWathch implements TextWatcher {

        private EditText editText;

        public MyTextWathch(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            initStatue(editText);
            if (editText == phoneEditET) {
                if (phone.length() == 11) {
                    isSend = true;
                    sendMessageBtn.setBackgroundResource(R.drawable.shape_send_order_btn);
                } else {
                    isSend = false;
                    sendMessageBtn.setBackgroundResource(R.drawable.shape_forget_psw_btn);
                }
            }
        }

    }

    /**
     * 请求验证码网络操作
     */
    public void requestCode() {
        try {
            Message message = Message.obtain();
            HashMap<String, String> map = new HashMap<>();
            map.put(AppConstants.MOBILE, phone);
            String url = UrlUtils.addParams(AppApi.FORGETCODE, map); // 拼接参数
            Response response = OkHttpClientManager.getAsyn(url, ForgetPswActivity.this);
            if (response.code() != 401) {
                String result = response.body().string();
                Gson gson = new Gson();
                entity = gson.fromJson(result, ForgetPswMessageEntity.class);
                if (entity.getCode() == 401) {
                    errToken();
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

}
