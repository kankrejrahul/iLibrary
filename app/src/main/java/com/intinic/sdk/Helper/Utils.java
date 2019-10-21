package com.intinic.sdk.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

public class Utils
{
    public static boolean isNetworkAvailable(Context context)
    {
        boolean g ;
        g = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
        return g;
    }

    public static void showMessage(Context context, String message)
    {
        Toast.makeText(context ,message, Toast.LENGTH_LONG).show();
    }

    public static int getDisplayHeight(Context context)
    {
        return ((context.getResources().getDisplayMetrics().heightPixels));
    }

    public static int getDisplayWidth(Context context)
    {
        return ((context.getResources().getDisplayMetrics().widthPixels));
    }

    public static boolean isDeviceBuildVersionMarshmallow()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isDeviceBuildVersionNogat()
    {
        return Build.VERSION.SDK_INT >= 26;
    }


    public static Bitmap getScreenShot(View view)
    {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }
}