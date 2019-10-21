package com.intinic.sdk;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.intinic.sdk.Helper.Utils;

public class PLPActivity extends AppCompatActivity
{
    Context context = PLPActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plp);


        findViewById(R.id.press).setOnClickListener(v -> {

            ModelListContent item = new ModelListContent("1", "Chair", "https://intinic.com/Intinic_Product/sdk_test/Native_Asset_Bundle/ARCore/woodenChair.sfb",
                    "", "woodenChairNW.wt3",
                    "1", "", "0.4f", "0.35f", "0.42f");


            /*ModelListContent item = new ModelListContent("1", "Chair", "https://intinic.com/Intinic_Product/sdk_test/Native_Asset_Bundle/ARCore/woodenChair.sfb",
                    "", "woodenChairNW.wt3",
                    "1", "", "0.4f", "0.35f", "0.42f");*/

            Intent intent = new Intent(context,PermissionActivity.class);
            intent.putExtra("item",item);
            intent.putExtra("colorCode","123123");
            startActivityForResult(intent,200);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("requestCode","requestCode: "+requestCode);

        if(requestCode == 200)
        {
            boolean addedToCart = data.getBooleanExtra("addedToCart", false);
            boolean addedToWishList = data.getBooleanExtra("addedToWishList", false);
            Log.d("requestCode","requestCode: "+requestCode+" addedToCart: "+addedToCart);

            Utils.showMessage(context,"addedToCart: "+addedToCart+" addedToWishList: "+addedToWishList);
        }
    }
}