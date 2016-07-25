package com.hengxun.builder.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hengxun.builder.utils.payutils.payutils.wx.Constants;
import com.hengxun.builder.view.activity.BaseActivity;
import com.hengxun.builder.view.activity.pay.PayActivity;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zy on 2016/4/4.
 * 微信支付结果回调
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Intent intent = new Intent("wxPay");
            intent.putExtra("errCode", resp.errCode);
            Log.d("WXPayEntryActivity", "resp.errCode:" + resp.errCode);
            sendBroadcast(intent);
            finish();
//            int errCode = resp.errCode;
//            if (errCode == -1) {
//                Toast.makeText(getApplicationContext(), resp.errStr, Toast.LENGTH_SHORT).show();
//                finish();
////                EventBus.getDefault().post("wx1");
//            } else if (errCode == 0) {
//                EventBus.getDefault().post("wx0");
//                Toast.makeText(getApplicationContext(), "支付完成", Toast.LENGTH_SHORT).show();
//                PayActivity.finishActivity.finish();
//
////                finish();
//            } else {
//                Toast.makeText(getApplicationContext(), "支付失败", Toast.LENGTH_SHORT).show();
//                finish();
//            }
        }
    }


}

//package com.lcx.qcsh.activity.wxapi;
//
//        import net.sourceforge.simcpux.Constants;
//
//        import com.lcx.qcsh.activity.PayActivity;
//        import com.lcx.qcsh.activity.R;
//        import com.tencent.mm.sdk.constants.ConstantsAPI;
//        import com.tencent.mm.sdk.modelbase.BaseReq;
//        import com.tencent.mm.sdk.modelbase.BaseResp;
//        import com.tencent.mm.sdk.openapi.IWXAPI;
//        import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
//        import com.tencent.mm.sdk.openapi.WXAPIFactory;
//
//        import android.app.Activity;
//        import android.content.Intent;
//        import android.os.Bundle;
//        import android.util.Log;
//        import android.widget.Toast;
//
//public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
//
//    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
//
//    private IWXAPI api;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);
//
//        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
//
//        api.handleIntent(getIntent(), this);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        api.handleIntent(intent, this);
//    }
//
//    @Override
//    public void onReq(BaseReq req) {
//    }
//
//    @Override
//    public void onResp(BaseResp resp) {
//        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
//
//        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//
//            Toast.makeText(WXPayEntryActivity.this, resp.errStr, Toast.LENGTH_SHORT).show();
//
//            PayActivity.handler.sendEmptyMessage(resp.errCode);
//            finish();
//            // AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            // builder.setTitle(R.string.app_tip);
//            // builder.setMessage(getString(R.string.pay_result_callback_msg,
//            // resp.errStr +";code=" + String.valueOf(resp.errCode)));
//            // builder.show();
//        }
//    }
//}
