package com.hengxun.builder.view.activity.pay;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hengxun.builder.R;
import com.hengxun.builder.common.AppApi;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.common.SharedPrefer;
import com.hengxun.builder.model.BankEntity;
import com.hengxun.builder.model.WithdrawNumber;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.hengxun.builder.utils.widget.StringUtils;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.personal.MyInfoActivity;
import com.hengxun.builder.view.activity.personal.PersonalAccountActivity;
import com.hengxun.builder.view.widget.WaittingDiaolog;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Created by ZY on 2016/3/11.
 * 提现页面
 */
public class WithdrawActivity extends BaseActivity {
    public static WithdrawActivity finishActivity; // 注销销毁的activity

    private Button withdraw_Btn;        // 提现按钮
    //    private double userAccount;       // 用户账户总额
    private TextView withdrawNumber_Tv; // 提现金额
    private WithdrawNumber.DataMapEntity entity; // 提现金额实体
    //    private UserInfo userInfo;          // 匠人信息
    private WaittingDiaolog dialog;
    private EditText withdrawName_Et;     // 匠人银行卡姓名
    private EditText withdrawNumber_Et;   // 匠人银行卡号
    private EditText withdrawBank_Et;     // 匠人银行卡的银行
    private EditText withdrawBankStart_Et;     // 匠人开户行
    private List<BankEntity.BankList> bankList; // 银行信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        finishActivity = this;
        showToolBar(getResources().getString(R.string.withdraw), true, this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        withdraw_Btn = (Button) findViewById(R.id.withdraw_Btn);
        withdrawNumber_Tv = (TextView) findViewById(R.id.withdrawNumber_Tv);
        withdrawName_Et = (EditText) findViewById(R.id.withdrawName_Et);
        withdrawNumber_Et = (EditText) findViewById(R.id.withdrawNumber_Et);
        withdrawBank_Et = (EditText) findViewById(R.id.withdrawBank_Et);
        withdrawBankStart_Et = (EditText) findViewById(R.id.withdrawBankStart_Et);
        withdrawNumber_Et.addTextChangedListener(watcher);
    }

    @Override
    protected void initData() {
        super.initData();
        entity = (WithdrawNumber.DataMapEntity) getIntent().getSerializableExtra("money"); // 获取提现金额
        withdrawNumber_Tv.setText(StringUtils.changeToMoney(entity.getAmount()));

        /* 如果之前成功提现过 则将之前存储的信息直接显示出来 方便用户使用 */
        String name = PreferenceUtil.readString(WithdrawActivity.this
                , SharedPrefer.ACCOUNT, SharedPrefer.BANK_USER); // 持卡人
        String number = PreferenceUtil.readString(WithdrawActivity.this
                , SharedPrefer.ACCOUNT, SharedPrefer.BANK_NUMBER); // 银行卡号
        String bank = PreferenceUtil.readString(WithdrawActivity.this
                , SharedPrefer.ACCOUNT, SharedPrefer.BANK_NAME); // 银行
        String bank_deposit = PreferenceUtil.readString(WithdrawActivity.this
                , SharedPrefer.ACCOUNT, SharedPrefer.BANK_DEPOSIT); // 开户行
        withdrawName_Et.setText(name);
        withdrawNumber_Et.setText(number);
        withdrawBank_Et.setText(bank);
        withdrawBankStart_Et.setText(bank_deposit);

        InputStream inputStream = getResources().openRawResource(R.raw.bank);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        BankEntity bankEntity = new Gson().fromJson(json, BankEntity.class);
        bankList = bankEntity.getBank();

        withdraw_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = withdrawNumber_Tv.getText().toString().trim();

                if (!TextUtils.isEmpty(money)) {
                    if (!(Double.parseDouble(money) > 0.00)) {
                        Toast.makeText(WithdrawActivity.this, "金额为0元，不能提交申请", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(WithdrawActivity.this, "金额为0元，不能提交申请", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = withdrawName_Et.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(WithdrawActivity.this, R.string.withdraw_bank_account, Toast.LENGTH_SHORT).show();
                    return;
                }

                String number = withdrawNumber_Et.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(WithdrawActivity.this, R.string.withdraw_banknum_hint, Toast.LENGTH_SHORT).show();
                    return;
                }

                String bank = withdrawBank_Et.getText().toString().trim();
                if (TextUtils.isEmpty(bank)) {
                    Toast.makeText(WithdrawActivity.this, R.string.withdraw_bank_hint, Toast.LENGTH_SHORT).show();
                    return;
                }

                String bankStart = withdrawBankStart_Et.getText().toString().trim();
                if (TextUtils.isEmpty(bankStart)) {
                    Toast.makeText(WithdrawActivity.this, R.string.withdraw_bank_start_hint, Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog = new WaittingDiaolog(WithdrawActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
                new Thread(depositTask).start();
            }
        });

    }

    /**
     * 银行卡输入的监听 当输入到六位时会进行判断  该银行为哪些银行
     */
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if ((s.length() == 5 || s.length() == 6) && bankList != null) {
                for (int i = 0; i < bankList.size(); i++) {
                    String id = bankList.get(i).getId();
                    if (id.equals(s.toString().trim()))
                        withdrawBank_Et.setText(bankList.get(i).getName());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                withdrawBank_Et.setText("");
            }
        }
    };

    public String getStringFromAssert(String fileName) throws IOException {
        String content = null; //结果字符串
        InputStream is = getResources().getAssets().open(fileName); //打开文件
        int ch = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(); //实现了一个输出流
        while ((ch = is.read()) != -1) {
            out.write(ch); //将指定的字节写入此 byte 数组输出流
            byte[] buff = out.toByteArray();//以 byte 数组的形式返回此输出流的当前内容
            out.close(); //关闭流
            is.close(); //关闭流
            content = new String(buff, "UTF-8"); //设置字符串编码
        }
        return content;
    }


    /**
     * 主线程中操作
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            // 成功提钱到银行卡
            if (msg.what == 1) {
//                Intent intent = new Intent(WithdrawActivity.this, HomeActivity.class);
//                startActivity(intent); // 提现成功
                MyInfoActivity.finishActivity.finish();
                PersonalAccountActivity.finishActivity.finish();
                Toast.makeText(WithdrawActivity.this, R.string.toast_apply_complete, Toast.LENGTH_SHORT).show();
                finish();
            }
//            else if (msg.what == 2) { // 获取提现金额
//                withdrawNumber_Tv.setText(String.valueOf(entity.getAmount()));
//            }
        }
    };

    /**
     * 发送提现申请
     */
    Runnable depositTask = new Runnable() {

        @Override
        public void run() {
            Message msg = Message.obtain();
            try {
                String name = withdrawName_Et.getText().toString().trim(); // 持卡人
                String number = withdrawNumber_Et.getText().toString().trim(); // 银行卡号
                String bank = withdrawBank_Et.getText().toString().trim(); // 银行
                String bank_deposit = withdrawBankStart_Et.getText().toString().trim(); // 开户行
                String worker_no = PreferenceUtil.readString(WithdrawActivity.this,
                        SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);

                PreferenceUtil.savePerfs(WithdrawActivity.this, SharedPrefer.ACCOUNT, SharedPrefer.BANK_USER, name);
                PreferenceUtil.savePerfs(WithdrawActivity.this, SharedPrefer.ACCOUNT, SharedPrefer.BANK_NUMBER, number);
                PreferenceUtil.savePerfs(WithdrawActivity.this, SharedPrefer.ACCOUNT, SharedPrefer.BANK_NAME, bank);
                PreferenceUtil.savePerfs(WithdrawActivity.this, SharedPrefer.ACCOUNT, SharedPrefer.BANK_DEPOSIT, bank_deposit);

                // 提现到银行卡中
                OkHttpClientManager.Param params[] = new OkHttpClientManager.Param[]{
                        new OkHttpClientManager.Param(AppConstants.WORKER_NO, worker_no) // 匠人工号
                        , new OkHttpClientManager.Param(AppConstants.REAL_NAME, name)  // 用户输入的银行卡名
                        , new OkHttpClientManager.Param(AppConstants.CARD_NUMBER, number) // 用户输入的卡号
                        , new OkHttpClientManager.Param(AppConstants.BANK, bank) // 用户输入的银行
                        , new OkHttpClientManager.Param(AppConstants.DEPOSIT_BANK, bank_deposit) // 用户输入的开户行
                        , new OkHttpClientManager.Param(AppConstants.MONEY, String.valueOf(entity.getAmount())) // 用户的提现金额
                };
                Response DEPOSIT = OkHttpClientManager.post(AppApi.DEPOSIT, WithdrawActivity.this
                        , params[0], params[1], params[2], params[3], params[4], params[5]);

                if (DEPOSIT.code() != 401) {
                    msg.what = 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
        }
    };

//    /**
//     * 获取提现金额
//     */
//    Runnable getAmountTask = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                String worker_no = PreferenceUtil.readString(WithdrawActivity.this,
//                        SharedPrefer.ACCOUNT, SharedPrefer.ACCOUNT);
//                HashMap<String, String> map = new HashMap<>();
//                map.put(AppConstants.WORKER_NO, worker_no);
//                String url = UrlUtils.addParams(AppApi.GETAMOUNT, map);
//                Response response = OkHttpClientManager.getAsyn(url, WithdrawActivity.this);
//                if (response.code() != 401) {
//                    Gson gson = new Gson();
//                    String result = response.body().string();
//                    WithdrawNumber number = gson.fromJson(result, WithdrawNumber.class);
//                    entity = number.getDataMap();
//                    Message message = Message.obtain();
//                    message.what = 2;
//                    handler.sendMessage(message);
//                } else {
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//        }
//    };
}
