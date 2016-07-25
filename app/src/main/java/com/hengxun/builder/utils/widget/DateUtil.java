package com.hengxun.builder.utils.widget;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ZY on 2016/3/23.
 */
public class DateUtil {

    private static String mYear;
    private static String mMonth;
    private static String mDay;
    private static String mWay;

    // WeekName
    public static final String[] weekName = {"周日", "周一", "周二", "周三", "周四",
            "周五", "周六"};

    /**
     * 获取当前年份当前月份的总天数(判断是否为闰年)
     *
     * @param year  当前的年份
     * @param month 当前的月份
     * @return 当前月份的总天数
     */
    public static int getMonthDays(int year, int month) {
        if (month > 12) {
            month = 1;
            year += 1;
        } else if (month < 1) {
            month = 12;
            year -= 1;
        }

        // 1-12月中每个月的总天数的数组集合
        int[] arr = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int days = 0;

        // 判断闰年
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            arr[1] = 29;
        }

        try {
            // 获得每个月的天数
            days = arr[month - 1];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return days;
    }

    public static String StringData() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "7";
        } else if ("2".equals(mWay)) {
            mWay = "1";
        } else if ("3".equals(mWay)) {
            mWay = "2";
        } else if ("4".equals(mWay)) {
            mWay = "3";
        } else if ("5".equals(mWay)) {
            mWay = "4";
        } else if ("6".equals(mWay)) {
            mWay = "5";
        } else if ("7".equals(mWay)) {
            mWay = "6";
        }
//        return mYear + "年" + mMonth + "月" + mDay + "日" + "/星期" + mWay;
        return mWay;
    }

    /**
     * 获取当前的年份
     *
     * @return 当前的年份
     */
    public static int getYear() {
        return Calendar.getInstance(Locale.getDefault()).get(Calendar.YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentMonthDays() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getWeekDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 根据给定的年份与月份格式化一个日期
     *
     * @param year  格式化的年
     * @param month 格式化的月
     * @return 一个格式化的日期(Date)
     */
    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString(int year, int month) {
        String dateString = year + "-" + (month > 9 ? month : ("0" + month))
                + "-01"; // date的格式

        Date date = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        return date;
    }

    /**
     * 返回一个系统当前时间的字符串;
     *
     * @return
     */
    public static String getSystemDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String dateString = year + "-" + (month > 9 ? month : ("0" + month))
                + "-" + (day > 9 ? day : ("0" + day));

        return dateString;
    }

    /**
     * Get current time;
     *
     * @return current time
     */
    public static String getTimeNow() {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(cal.getTime());
        return time;
    }

    /**
     * Get current date;
     *
     * @return current data
     */
    public static String getDateNow() {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(cal.getTime());
        return dateStr;
    }

    private StringBuffer sb;

    /**
     * 时间戳转换成日期
     * 时间戳的含义是从1970年开始经过的时间（毫秒）
     * 括号里写时间戳
     */
    public static String getStrTime(String cc_time) {
        String re_StrTime = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");    // yyyy年MM月dd日HH时mm分ss秒
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));
        return re_StrTime;
    }

    /**
     * 时间戳转换成日期
     * 时间戳的含义是从1970年开始经过的时间（毫秒）
     * 括号里写时间戳
     */
    public static String getStringTime(String cc_time) {
        String re_StrTime = null;

        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH:mm");    // yyyy年MM月dd日HH时mm分ss秒
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));
        return re_StrTime;
    }


    /**
     * 两个时间戳的差
     */
    public static int getTime(long d1, long d2) {
        int hours = (int) (d2 - d1) / 3600 / 1000;
        hours /= 1;
        return hours;
    }


    /**
     * 时间戳距离现在的时间
     */
    public static String getStandardDate(String timeStr) {

        StringBuffer sb = new StringBuffer();

        long t = Long.parseLong(timeStr);
        long time = System.currentTimeMillis() - (t * 1000);
        long mill = (long) Math.ceil(time / 1000);//秒前

        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

        if (day - 1 > 0) {
            sb.append(day + "天");
        } else if (hour - 1 > 0) {
            if (hour >= 24) {
                sb.append("1天");
            } else {
                sb.append(hour + "小时");
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                sb.append("1小时");
            } else {
                sb.append(minute + "分钟");
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                sb.append("1分钟");
            } else {
                sb.append(mill + "秒");
            }
        } else {
            sb.append("刚刚");
        }
        if (!sb.toString().equals("刚刚")) {
            sb.append("前");
        }
        return sb.toString();
    }

    /**
     * 时间戳距离现在的时间
     */
    public static boolean standardDate(String timeStr) {
        long t = Long.parseLong(timeStr);
        long time = System.currentTimeMillis() - (t * 1000);
        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

        return hour <= 24;
    }
}
