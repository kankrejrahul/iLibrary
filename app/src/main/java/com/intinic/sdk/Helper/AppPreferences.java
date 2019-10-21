package com.intinic.sdk.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.intinic.sdk.R;

public class AppPreferences
{
    public static void setIsDeviceSupportARCore(Context context, int arCoreSupport)
    {
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ar_core_support",arCoreSupport);
        editor.commit();
    }

    public static int getIsDeviceSupportARCore(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Activity.MODE_PRIVATE);
        return pref.getInt("ar_core_support",-1);
    }

    public static void setIsCameraPermissionAsked(Context context, boolean asked)
    {
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("camera",asked);
        editor.commit();
    }

    public static boolean getCameraPermissionAsked(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Activity.MODE_PRIVATE);
        return pref.getBoolean("camera",false);
    }


    public static void setIsStoragePermissionAsked(Context context, boolean asked)
    {
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("storage",asked);
        editor.commit();
    }

    public static boolean getStoragePermissionAsked(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Activity.MODE_PRIVATE);
        return pref.getBoolean("storage",false);
    }
}