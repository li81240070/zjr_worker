package com.hengxun.builder.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZY on 2016/4/6.
 */
public class AppUtils {
    /**
     * 获取当前版本号;
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

//    /**
//     * 获取本机wifi ip地址
//     */
//    public static String intToIp(Context context, int i) {
//        WifiManager wifimanage = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);//获取WifiManager
//        WifiInfo wifiinfo = wifimanage.getConnectionInfo();
//        String ip = intToIp(wifiinfo.getIpAddress());
//
//
//        return (i & 0xFF) + "." +
//                ((i >> 8) & 0xFF) + "." +
//                ((i >> 16) & 0xFF) + "." +
//                (i >> 24 & 0xFF);
//    }

    //将bitmap写入文件
    public static Uri creatFile(Bitmap heaBitmap) {
        FileOutputStream fileOutputStream = null;
        String saveDir = Environment.getExternalStorageDirectory() + "/jr_ordor";
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddssSSS");
        String fileName = "JR" + (dateFormat.format(new Date())) + ".jpg";
        File file = new File(saveDir, fileName);
        try {
            fileOutputStream = new FileOutputStream(file);
            heaBitmap.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }


    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOpen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    public static boolean formatPrice(String price) {
//        DecimalFormat format = new DecimalFormat("0.00");
//        String fPrice = format.format(new BigDecimal(price));
//        String str = price;
        String moneyRegex = "^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$";
        Pattern pattern = Pattern.compile(moneyRegex);
        Matcher matcher = pattern.matcher(price);
        return matcher.find();
//        String s[] = price.split("[.]");
//        if (s[0].equals("")) {
//            return "wrong";
//        } else {
//            if (s.length > 1) {
//                String s1 = s[1].substring(0, 2);
//                String ss = s[0] + "." + s1;
//                return ss;
//            } else {
//                return price;
//            }
//        }
    }

}
