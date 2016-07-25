package com.hengxun.builder.utils.exception;

/**
 * 制定异常处理的标准接口。
 * 内容有分析，缓存，发送到服务器等。
 * <p/>
 * Created by HX-SuCong on 2/22/16.
 */
public interface CrashInterfaces<T extends Throwable> {
    /**
     * @param ex
     */
    void analyseCrash(T ex);

    /**
     * @param ex
     */
    void cacheCrash(T ex);

    /**
     * @param ex
     */
    void sendCrashToServer(T ex);
}
