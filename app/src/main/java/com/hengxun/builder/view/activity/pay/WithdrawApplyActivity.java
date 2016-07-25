//package com.hengxun.builder.view.activity.pay;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import com.hengxun.builder.R;
//import com.hengxun.builder.view.activity.BaseActivity;
//
//import org.greenrobot.eventbus.EventBus;
//
///**
// * Created by ZY on 2016/3/14.
// */
//public class WithdrawApplyActivity extends BaseActivity {
//    private Button withdraw_complete_Btn; // 提现完成
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_withdraw_apply);
//        showToolBar(getResources().getString(R.string.withdraw_details), true, this);
//        initView();
//        initData();
//    }
//
//    @Override
//    protected void showToolBar(String titleName, boolean isShow, Activity activity) {
//        super.showToolBar(titleName, isShow, activity);
//        ImageView toolbar_left_Ib = (ImageView) activity.findViewById(R.id.toolbar_left_Ib);
//        toolbar_left_Ib.setVisibility(View.GONE);
//    }
//
//    @Override
//    protected void initView() {
//        super.initView();
//        withdraw_complete_Btn = (Button) findViewById(R.id.withdraw_complete_Btn);
//    }
//
//    @Override
//    protected void initData() {
//        super.initData();
//        withdraw_complete_Btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                EventBus.getDefault().post("");
//            }
//        });
//    }
//}
