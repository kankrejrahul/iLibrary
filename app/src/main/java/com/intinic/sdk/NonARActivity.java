package com.intinic.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.intinic.sdk.Helper.Utils;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;
import com.wikitude.common.camera.CameraSettings;
import java.io.IOException;

public class NonARActivity extends AppCompatActivity implements View.OnClickListener
{
    protected ArchitectView architectView;
    Context context = NonARActivity.this;
    boolean cameraTwoEnabled = true;
    String modelToRender,scaleSize,colorCode;

    //    FloatingActionButton addToCart;
    ImageView addToCart;
    boolean addedToCart = false,addedToWishList = false;

    TextView addToWishListTV,addToCartTV;

    Drawable rectangleWithBorder,rectangleWithOutBorder;

    @SuppressLint("NewApi")
    private void setActionBar()
    {
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
    }

    void getData()
    {
        Intent intent = getIntent();
        modelToRender = intent.getStringExtra("modelToRender");
        scaleSize = intent.getStringExtra("scaleSize");

        colorCode =  getIntent().getStringExtra("colorCode");

        Log.d("arExperiencePath", "modelToLoad: " + modelToRender+" scaleSize: "+scaleSize);

        String packageName = intent.getStringExtra("packageName");
    }

    void changeColor(int color,int idToChange)
    {
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, idToChange);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.BLUE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WebView.setWebContentsDebuggingEnabled(true);

        setContentView(R.layout.activity_non_ar);
        getData();
        setActionBar();

//        changeColor(android.R.color.holo_blue_bright,R.drawable.rectangle_with_border);
  //      changeColor(android.R.color.holo_blue_bright,R.drawable.rectangle_without_border);

//        rectangleWithBorder = rectangleWithOutBorder

        addToWishListTV = findViewById(R.id.addToWishListTV);
        addToCartTV = findViewById(R.id.addToCartTV);


        addToCart = findViewById(R.id.addToCart);

        addToWishListTV.setOnClickListener(this);
        addToCartTV.setOnClickListener(this);
        addToCart.setOnClickListener(this);


//      architectView = new ArchitectView(this);
//      architectView.onCreate(config); // create ArchitectView with configuration

        this.architectView = this.findViewById(R.id.architectView);


        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration(); // Creates a config with its default values.
        config.setLicenseKey(getString(R.string.wikitude_license_key)); // Has to be set, to get a trial license key visit http://www.wikitude.com/developer/licenses.
        config.setCameraPosition(CameraSettings.CameraPosition.BACK);//back       // The default camera is the first camera available for the system.
//        config.setCameraResolution(sampleData.getCameraResolution());//      // The default resolution is 640x480.
//        config.setCameraFocusMode(sampleData.getCameraFocusMode());     // The default focus mode is continuous focusing.
        config.setCamera2Enabled(cameraTwoEnabled);        // The camera2 api is disabled by default (old camera api is used).


        architectView.onCreate(config);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();

        try
        {
//            SAMPLES_ROOT + arExperiencePath

            architectView.load("wiki/index.html");

            callJavaScript("World.setModelName", new String[] { modelToRender });
            callJavaScript("World.setModelScale", new String[] { scaleSize.replace("f","") });

        }
        catch (IOException e)
        {
            Toast.makeText(this, getString(R.string.error_loading_ar_experience), Toast.LENGTH_SHORT).show();
//            Log.e("MainActivity", "Exception while loading arExperience " + arExperiencePath + ".", e);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        architectView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        architectView.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        Intent intent = new Intent();
        intent.putExtra("addedToCart", addedToCart);
        setResult(500, intent);

        architectView.clearCache();
        architectView.onDestroy(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                addedToCart = false;
                addedToWishList = false;

                exit();
                break;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.addToCart)
        {
            if(addedToCart)
            {
                addedToCart = false;
                Utils.showMessage(context,"Removed from cart");
            }
            else
            {
                addedToCart = true;
                Utils.showMessage(context,"Added to cart");
            }
            //            exit();
        }
        else if (v.getId() == R.id.addToCartTV)
        {
            if(!addedToCart)
            {
                addedToCart = true;
                Utils.showMessage(context,"Added to cart");

                addToCartTV.setText("Goto Cart");


                addToWishListTV.setText("Add Wishlist");
            }
            else
            {
                addedToWishList = false;
                exit();
            }
        }
        else if (v.getId() == R.id.addToWishListTV)
        {
            if(!addedToWishList)
            {
                addedToWishList = true;
                Utils.showMessage(context,"Added to wishlist");
                addToWishListTV.setText("Goto Wishlist");

                addToCartTV.setText("Add to Cart");
            }
            else
            {
                addedToCart = false;
                exit();
            }
        }
    }

    void exit()
    {
        Intent intent=new Intent();
        intent.putExtra("addedToCart",addedToCart);
        intent.putExtra("addedToWishList",addedToWishList);
        setResult(500,intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        addedToCart = false;
        addedToWishList = false;

        exit();
//        super.onBackPressed();
    }

    public void callJavaScript(final String methodName, final String[] arguments)
    {
        final StringBuilder argumentsString = new StringBuilder("");
        if(arguments != null)
        {
            for (int i= 0; i<arguments.length; i++)
            {
                argumentsString.append(arguments[i]);
                if (i<arguments.length-1)
                {
                    argumentsString.append(", ");
                }
            }
        }
        if (this.architectView!=null)
        {
            //items+="<img src="+item.product_image2+"  onclick='changeImage(\""+item.product_image2+"\");'>";

            final String js = ( methodName + "( '" + argumentsString.toString() + "' );" );

            Log.d("xyz","argumentsString.toString(): "+argumentsString.toString());
            Log.d("xyz","js: "+js);

//            this.architectView.callJavascript(\""+js"\");
            this.architectView.callJavascript(js);


        }
    }
}