package com.hengxun.builder.utils.widget;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by ZY on 2016/3/30.
 * sp变量存储工具类
 */
public final class PreferenceUtil {

    private final static int DEF_PREFS_MODE = Context.MODE_PRIVATE;

    public static void savePerfs(Context context, String prefsName,
                                 String key, Object value) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                DEF_PREFS_MODE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if (value instanceof String)
            editor.putString(key, (String) value);
        else if (value instanceof Integer)
            editor.putInt(key, (Integer) value);
        else if (value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);
        else if (value instanceof Float)
            editor.putFloat(key, (Float) value);

        editor.commit();
    }

    public static void savePerfs(Context context, String prefsName, int mode,
                                 String key, Object value) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                mode);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if (value instanceof String)
            editor.putString(key, (String) value);
        else if (value instanceof Integer)
            editor.putInt(key, (Integer) value);
        else if (value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);
        else if (value instanceof Float)
            editor.putFloat(key, (Float) value);

        editor.commit();
    }

    public static void clearPrefs(Context context, String prefsName) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                DEF_PREFS_MODE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear().commit();
    }

    public static void clearPrefs(Context context, String prefsName, int mode) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                mode);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear().commit();
    }


    public static void removePrefs(Context context, String prefsName,
                                   int prefsMode, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                prefsMode);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(key).commit();
    }

    public static Object readPrefs(Context context, String prefsName,
                                   int prefsMode, int type, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                prefsMode);
        Object obj = null;

        switch (type) {
            case 0: {
                obj = sharedPrefs.getString(key, null);
                break;
            }
            case 1: {
                obj = sharedPrefs.getInt(key, -1);
                break;
            }
            case 2: {
                obj = sharedPrefs.getBoolean(key, false);
                break;
            }
            default:
                break;
        }

        return obj;
    }

    public static String readString(Context context, String prefsName, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                DEF_PREFS_MODE);
        String target = sharedPrefs.getString(key, null);

        return target;
    }

    public static int readInteger(Context context, String prefsName, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                DEF_PREFS_MODE);
        int target = sharedPrefs.getInt(key, 0);

        return target;
    }

    public static boolean readBoolean(Context context, String prefsName, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                DEF_PREFS_MODE);
        boolean target = sharedPrefs.getBoolean(key, false);

        return target;
    }

    public static long readLong(Context context, String prefsName, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                DEF_PREFS_MODE);
        long target = sharedPrefs.getLong(key, 0);

        return target;
    }

    public static float readFloat(Context context, String prefsName, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                DEF_PREFS_MODE);
        float target = sharedPrefs.getFloat(key, 0);

        return target;
    }

    public static Set<String> getStringSet(Context context, String prefsName, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                DEF_PREFS_MODE);
        Set<String> target = sharedPrefs.getStringSet(key, null);

        return target;
    }

    public static boolean isPrefsExist(Context context, String prefsName,
                                       int prefsMode, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                prefsMode);
        return sharedPrefs.contains(key);
    }

    public static boolean isPrefsExist(Context context, String prefsName, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName,
                DEF_PREFS_MODE);
        return sharedPrefs.contains(key);
    }

    public static void updatePrefs(Context context, String prefsName, String key, Object value) {
        if (!PreferenceUtil.isPrefsExist(context, prefsName, key))
            return;

        PreferenceUtil.savePerfs(context, prefsName, key, value);
    }

}
