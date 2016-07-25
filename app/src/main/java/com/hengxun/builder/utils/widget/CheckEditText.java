package com.hengxun.builder.utils.widget;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zy on 15/11/9.
 */
public class CheckEditText {

    /**
     * 判断是否是合法手机号
     * @param mobiles
     * @return
     */
    public static boolean isMobile(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 验证邮箱输入是否合法
     *
     * @param strEmail
     * @return
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * 判断密码是否正确
     * */
    public static boolean isPsw(String psw) {
        String strPattern = "^[\\@A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~]{6,22}$";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(psw);
        Log.d("CheckEditText", psw);
        return m.matches();
    }

    /**
     * 判断验证码是否正确
     * */
    public static boolean isCheckCode(String checkCode) {
        String strPattern = "^[\\@0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~]{4}$";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(checkCode);
        return m.matches();
    }



}
