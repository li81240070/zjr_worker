package com.hengxun.builder.view.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.hengxun.builder.R;
import com.hengxun.builder.view.activity.BaseActivity;

/**
 * Created by ZY on 2016/3/25.
 */
public class AccountSafeActivity extends BaseActivity {
    private RelativeLayout accountChangePsw_Rl; // 修改密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_safe);
        showToolBar(getResources().getString(R.string.account_and_safe), true, this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        accountChangePsw_Rl = (RelativeLayout) findViewById(R.id.accountChangePsw_Rl);
    }

    @Override
    protected void initData() {
        super.initData();

        // 进入修改密码页面
        accountChangePsw_Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSafeActivity.this, ForgetPswActivity.class);
                startActivity(intent);
            }
        });
    }
}
