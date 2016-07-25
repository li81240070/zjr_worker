//package com.hengxun.builder.view.activity.account;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//import com.hengxun.builder.R;
//import com.hengxun.builder.view.activity.BaseActivity;
//
///**
// * Created by ZY on 2016/3/25.
// * 忘记密码（重设完成）
// */
//public class ResetCompleteActivity extends BaseActivity {
//    public static ResetCompleteActivity finishActivity; // 注销销毁的activity
//    private Button forget_nextStep_Btn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_reset_complete);
//        finishActivity = this;
//        showToolBar(getResources().getString(R.string.forgrt_psw), true, this);
//        init();
//    }
//
//    private void init() {
//        forget_nextStep_Btn = (Button) findViewById(R.id.forget_nextStep_Btn);
//        forget_nextStep_Btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ResetCompleteActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//    }
//
//}
