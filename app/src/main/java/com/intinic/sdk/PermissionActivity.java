package com.intinic.sdk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.intinic.sdk.Helper.AppConstants;
import com.intinic.sdk.Helper.AppPreferences;
import com.intinic.sdk.Helper.JSONParser;
import com.intinic.sdk.Helper.PermissionHelper;
import com.intinic.sdk.Helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.intinic.sdk.Helper.AppConstants.TAG_DEVICE_MATCH;
import static com.intinic.sdk.Helper.AppConstants.TAG_SUCCESS;

public class PermissionActivity extends AppCompatActivity
{
    Context context = PermissionActivity.this;

    PermissionHelper permissionsHelper;
    ProgressBar progressCircular;
    //Toolbar toolbar;
    int isDeviceSupportARCore = -1;
    ModelListContent item;
    String colorCode;

    boolean addedToWishList = false,addedToCart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        Intent intent = getIntent();
        item = intent.getParcelableExtra("item");
        colorCode = intent.getStringExtra("colorCode");

        permissionsHelper = new PermissionHelper();

        progressCircular = findViewById(R.id.progressCircular);
        //toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

//        GradientDrawable unwrappedDrawable = (GradientDrawable)AppCompatResources.getDrawable(context, R.drawable.rectangle_with_border);
  //      unwrappedDrawable.setStroke(1, Color.BLUE);

        checkDeviceARCoreSupport();
    }

    public void checkDeviceARCoreSupport()
    {
        isDeviceSupportARCore = AppPreferences.getIsDeviceSupportARCore(context);
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL+" "+Build.BRAND+ " "+Build.PRODUCT;

        Log.d("modelcycy","manufacturer: "+manufacturer+" model: "+model);

        if(isDeviceSupportARCore == -1)
            makeAPICalToCheckARCoreSupport();
        else
        {
            checkForPermission();
            //check arcore support locally
        }
    }

    //ARCore Part
    public void makeAPICalToCheckARCoreSupport()
    {
        if(Utils.isNetworkAvailable(context))
        {
            progressCircular.setVisibility(View.VISIBLE);
//            APIHandler.checkDeviceARCoreSupport(context);
            new DemoTask().execute();
        }
        else
        {
            progressCircular.setVisibility(View.GONE);
            Utils.showMessage(context,getString(R.string.error_no_internet));
        }
    }

    public void onCheckARCoreSupport(int result)
    {
        progressCircular.setVisibility(View.GONE);
        isDeviceSupportARCore = result;
        AppPreferences.setIsDeviceSupportARCore(context,isDeviceSupportARCore);
        checkForPermission();
    }


    void checkForPermission()
    {
        if(Utils.isDeviceBuildVersionMarshmallow())
            permissionsHelper.getCameraPermission(PermissionActivity.this);
        else
            proceedToARExperience();
    }


    public void proceedToARExperience()
    {
        isDeviceSupportARCore = AppPreferences.getIsDeviceSupportARCore(context);
        Log.d("isDeviceSupportARCore","isDeviceSupportARCore: "+isDeviceSupportARCore);

        if(isDeviceSupportARCore==2)
        {
            Log.d("deviceslist","getKeyAndroidARCoreModelUrl: "+item.getKeyAndroidARCoreModelUrl());

            Intent intent = new Intent(this, ARCoreActivity.class);
            intent.putExtra("colorCode",colorCode);
            intent.putExtra("scale",item.getKeyScale());
            intent.putExtra("minScale",item.getMinScale());
            intent.putExtra("maxScale",item.getMaxScale());
            intent.putExtra("modelUrl",item.getKeyAndroidARCoreModelUrl());
            startActivityForResult(intent,500);
            //buildNotification(getString(R.string.app_name),"Click to load content");
        }
        else
        {
            Log.d("deviceslist","getKeyOtherDeviceModelUrl: "+item.getKeyOtherDeviceModelUrl());

            Intent intent = new Intent(PermissionActivity.this,NonARActivity.class);
            intent.putExtra("modelToRender",item.getKeyOtherDeviceModelUrl());
            intent.putExtra("colorCode",colorCode);
            intent.putExtra("scaleSize",item.getKeyScale());
            intent.putExtra("packageName","com.intinic.sdk");
            startActivityForResult(intent,500);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
/*        if (Utils.isAppRunning(this, "com.intinic.augment.unity")) {
            Log.d("processrunning","native is running");

        } else {
            Log.d("processrunning","native is not running");
        }*/
    }

    //Permission check
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        permissionsHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("requestCode","requestCode: "+requestCode);

        if(requestCode == 500)
        {
            addedToCart = data.getBooleanExtra("addedToCart", false);
            addedToWishList = data.getBooleanExtra("addedToWishList", false);
            Log.d("requestCode","requestCode: "+requestCode+" addedToCart: "+addedToCart);
/*            Log.d("requestCode","addedToCart: "+addedToCart);

            if(addedToCart)
            {
                Intent intent = new Intent(this,CartActivity.class);
                startActivity(intent);
            }*/

            Intent intent=new Intent();
            intent.putExtra("addedToCart",addedToCart);
            intent.putExtra("addedToWishList",addedToWishList);
            setResult(200,intent);
            finish();
        }
    }

    @Override
    protected void onDestroy()
    {
        Intent intent=new Intent();
        intent.putExtra("addedToCart",addedToCart);
        setResult(200,intent);

        super.onDestroy();
    }



    class DemoTask extends AsyncTask<Void, Void, Void>
    {
        int result = 0;

        protected Void doInBackground(Void... arg0)
        {
            JSONParser jParser = new JSONParser();

            String manufacturer = Build.MANUFACTURER.toLowerCase();
            String deviceName = Build.DEVICE.toLowerCase();
            String model = Build.MODEL.toLowerCase();
            String osVersion = String.valueOf(Build.VERSION.SDK_INT);

            JSONObject json = jParser.getJSONFromUrl(AppConstants.VALIDATE_ARCORE_SUPPORT_URL,AppConstants.APP_UNIQUE_KEY,
                    manufacturer,model,deviceName,osVersion);

            try
            {
                String successValue = json.getString(TAG_SUCCESS);
                String message = json.getString(TAG_DEVICE_MATCH);

                Log.d("checkeckec","successValue: "+successValue+" message: "+message);

                if (Integer.parseInt(successValue) == 1 && message.equalsIgnoreCase("success"))
                    result = 2;
                else
                    result = 1;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void res)
        {
            switch (result)
            {
                case 1:
                        onCheckARCoreSupport(1);
                        break;
                case 2:
                        onCheckARCoreSupport(2);
                        break;
            }
        }
    }
}
