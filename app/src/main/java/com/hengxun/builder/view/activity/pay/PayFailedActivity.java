package com.hengxun.builder.view.activity.pay;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hengxun.builder.R;

public class PayFailedActivity extends Activity {

    //标题
    private TextView titleTextView;
    //后退
    private ImageView backImageView;
    //选择其他付款方式
    private TextView otherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_failed);

        initView();


    }

    private void initView() {
        //设置标题
        titleTextView = (TextView) findViewById(R.id.toolbarTitle_Tv);
        titleTextView.setText("支付");
        //隐藏back
        backImageView = (ImageView) findViewById(R.id.toolbar_left_Ib);
        backImageView.setVisibility(View.GONE);
        //选择其他付款方式
        otherTextView = (TextView) findViewById(R.id.activity_pay_failed_other_Tv);
        otherTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO  选择其他付款方式
            }
        });

    }


}
