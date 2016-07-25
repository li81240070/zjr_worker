package com.hengxun.builder.utils.widget;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ZY on 2016/3/26.
 */
public class UrlUtils {

    // get请求拼接参数
    public static String addParams(String url, HashMap<String, String> params) {
        // 添加url参数
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            url += sb.toString();
        }
        return url;
    }
}
