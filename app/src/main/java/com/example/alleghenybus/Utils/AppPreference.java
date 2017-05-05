package com.example.alleghenybus.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Alabhya Farkiya.
 */
public class AppPreference {

    public static final String PREF_NAME = "bus_app_pref_com";


    public static int getInt(String key, int defValue,Context context)
    {
        return getSharedPreferences(context).getInt(key, defValue);
    }

    public static String getString(String key, String defValue,Context context)
    {
        return getSharedPreferences(context).getString(key, defValue);
    }

    public static Set<String> getStringSet(String key, Set<String> defValue, Context context)
    {
        return getSharedPreferences(context).getStringSet(key, defValue);
    }
    public static void putStringSet(String key, Set<String> stringSet, Context context)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putStringSet(key, stringSet);
        editor.commit();
    }
    public static void putInt(String key, int defaultValue,Context context)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, defaultValue);
        editor.commit();
    }

    public static void putString(String key, String defaultValue,Context context)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, defaultValue);
        editor.commit();
    }

    private static SharedPreferences getSharedPreferences(Context context)
    {
        SharedPreferences pref =  context.getSharedPreferences(AppPreference.PREF_NAME, 0);
        return pref;
    }
    public static boolean getBoolean(String key, boolean defValue,Context context)
    {
        return getSharedPreferences(context).getBoolean(key, defValue);
    }

    public static float getFloat(String key, float defValue,Context context)
    {
        return getSharedPreferences(context).getFloat(key, defValue);
    }
    public static long getLong(String key, long defValue,Context context)
    {
        return getSharedPreferences(context).getLong(key, defValue);
    }
    public static void putBoolean(String key, boolean defaultValue,Context context)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, defaultValue);
        editor.commit();
    }

    public static void putFloat(String key, float defaultValue,Context context)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putFloat(key, defaultValue);
        editor.commit();
    }
    public static void putLong(String key, long defaultValue,Context context)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(key, defaultValue);
        editor.commit();
    }



    public static void clearALL(Context context)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.commit();
    }

}
