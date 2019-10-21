package com.intinic.sdk.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.intinic.sdk.PermissionActivity;

public class PermissionHelper
{
    public void getCameraPermission(Activity activity)
    {
        int permissionCamera = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permissionCamera != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA))
            {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                        new AppConstants().PERMISSION_CAMERA_CONSTANT);
            }
            else
            {
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA},
                        new AppConstants().PERMISSION_CAMERA_CONSTANT);
            }
        }
        if(permissionCamera == 0)
        {
            getReadPermission(activity);
        }
    }

    public void getReadPermission(Activity activity)
    {
        int permissionReadStorage = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        new AppConstants().PERMISSION_READ_STORAGE_CONSTANT);
            }
            else
            {
                ActivityCompat.requestPermissions(activity,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        new AppConstants().PERMISSION_READ_STORAGE_CONSTANT);
            }
        }
        if(permissionReadStorage == 0)
        {
            getWritePermission(activity);
        }
    }


    private void getWritePermission(Activity activity)
    {
        int permissionWriteStorage = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        new AppConstants().PERMISSION_READ_WRITE_CONSTANT);
            }
            else
            {
                ActivityCompat.requestPermissions(activity,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        new AppConstants().PERMISSION_READ_WRITE_CONSTANT);
            }
        }
        if(permissionWriteStorage == 0)
        {
            if(activity instanceof PermissionActivity)
                ((PermissionActivity)activity).proceedToARExperience();
        }
    }



    public void onRequestPermissionsResult(Activity activity, int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 1://camera
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    getReadPermission(activity);
                }
                break;

            case 2://read
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    getWritePermission(activity);
                }
                break;

            case 3://write
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(activity instanceof PermissionActivity)
                    {
                        ((PermissionActivity)activity).proceedToARExperience();
                    }
                }
                break;


        }
    }
}
