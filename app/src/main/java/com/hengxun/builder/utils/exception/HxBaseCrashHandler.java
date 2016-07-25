package com.hengxun.builder.utils.exception;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

/**
 * CrashHandler是移动Crash组件的Base类，这个类用来封装父类的基本实现。
 * 主要用来捕捉异常的。
 * Created by SuCong on 2/22/16.
 *
 * @author HX-SuCong
 * @version 1.0
 */
public enum HxBaseCrashHandler implements Thread.UncaughtExceptionHandler,
        CrashInterfaces<Throwable> {
    INSTANCE {
        /**
         * @param ex
         */
        @Override
        public void analyseCrash(Throwable ex) {

        }

        /**
         * @param ex
         */
        @Override
        public void cacheCrash(Throwable ex) {

        }

        /**
         * @param ex
         */
        @Override
        public void sendCrashToServer(Throwable ex) {

        }
    };

    /**
     * Log打印的标签。
     */
    public static final String CRASH_TAG = "HxBaseCrashTag";

    /**
     * 当前的环境变量。
     */
    private Context mCtx;

    /**
     * 系统默认的Handler。
     */
    private Thread.UncaughtExceptionHandler defCrashHandler;

    HxBaseCrashHandler() {
        // nothing.
    }

    private String localPath;
    private String url;

    /**
     * 初始化CrashHandler。
     *
     * @param context 当前的环境变量
     */
    public void initCrashHandler(Context context, String localPath, String url) {
        mCtx = context;

        // 获取系统默认的CrashHandler
        defCrashHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置当前CrashHandler去处理异常。
        Thread.setDefaultUncaughtExceptionHandler(this);

        //保存文件路径
        this.localPath = localPath;
        //上传服务器地址
        this.url = url;
    }

    /**
     * The thread is being terminated by an uncaught exception. Further
     * exceptions thrown in this method are prevent the remainder of the
     * method from executing, but are otherwise ignored.
     *
     * @param thread the thread that has an uncaught exception
     * @param ex     the exception that was thrown
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Date currentTime = new Date();
        long currentTimeStamp = currentTime.getTime();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        ex.printStackTrace(printWriter);
        //需要显示的内容
        String stacktrace = "出错时间 : " +  currentTime + "\n" + result.toString() + "\n";
        printWriter.close();

        String filename = year + "年" + month + "月" + day + "日" + ".txt";
        //写入文件中
        if (localPath != null) {
            writeToFile(stacktrace, filename);
        }
//        if (url != null) {
//            sendToServer(stacktrace, filename);
//        }
        //将异常交给系统默认处理
        defCrashHandler.uncaughtException(thread, ex);
    }

    private void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                    localPath + "/" + filename, true));
            //write是写入缓冲区域  并不是真正的写入文件中
            bos.write(stacktrace);
            //提交  flush才是将数据提交到文件中
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传服务器方法
     * **/
    private void sendToServer(String stacktrace, String filename) {

    }


    /**
     * 处理一个异常，这个异常可能需要我们自己添加其他的处理流程，
     * 比如 分析，缓存，发送到server等。这些需要我们利用接口或抽象方法来实现。
     *
     * @param ex 异常内容
         * @return 如果返回true表示异常已经处理完成，否则返回false。
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) return false;

        //提示信息
        new Thread(){
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                Toast.makeText(mCtx, "程序发生异常，即将退出程序，抱歉" , Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();

        // 分析异常
        analyseCrash(ex);

        // 缓存异常
        cacheCrash(ex);

        // 发送异常
        sendCrashToServer(ex);

        // 异常已经处理完成。
        return true;
    }

}
