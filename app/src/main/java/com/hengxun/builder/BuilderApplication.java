package com.hengxun.builder;

import android.app.Application;
import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;
import com.hengxun.builder.common.AppConstants;
import com.hengxun.builder.service.LocationService;
import com.hengxun.builder.utils.okhttp.OkHttpClientManager;
import com.hengxun.builder.utils.widget.Foreground;
import com.hengxun.builder.utils.widget.PreferenceUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by ZY on 2016/3/7.
 */
public class BuilderApplication extends Application {
    private static BuilderApplication sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this); // 初始化百度地图
        JPushInterface.setDebugMode(true); // 极光设置调试模式
        JPushInterface.init(this);       // 初始化极光推送
        String s = JPushInterface.getRegistrationID(this);
        PreferenceUtil.savePerfs(this, AppConstants.WORKERSSP, AppConstants.REGISTRATIONID, s);//向极光注册后再次初始化 本方法会回调id
        final IWXAPI msgApi = WXAPIFactory.createWXAPI(getApplicationContext(), null);
        // 将该app注册到微信
        msgApi.registerApp("wxd848789615613a16");
        Foreground.init(this);
        sAppContext = this;

        Intent intent = new Intent(getsAppContext(), LocationService.class);
        startService(intent);

        try {
            OkHttpClientManager.getInstance()
                    .setCertificates(getAssets().open("zhenjren.cer"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BuilderApplication getsAppContext() {
        return sAppContext;
    }
}
