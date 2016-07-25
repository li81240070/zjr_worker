package com.hengxun.builder.utils.widget;

/**
 * Created by ZY on 2016/5/21.
 */
public class StringUtils {

    public static String changeToMoney(Double money) {
        String str = String.format("%.2f", money);
        return str;
    }
}
