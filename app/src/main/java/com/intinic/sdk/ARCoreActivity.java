package com.intinic.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.PixelCopy;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.PlaneRenderer;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.intinic.sdk.Helper.SaveImages;
import com.intinic.sdk.Helper.Utils;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public class ARCoreActivity extends AppCompatActivity implements View.OnClickListener
{
    ArFragment arFragment;
    Context context = ARCoreActivity.this;
    boolean modelPlace = false;
    String modelPath = "";

    ImageView addToCart;
    boolean addedToCart = false,addedToWishList = false;

    TextView addToWishListTV,addToCartTV;
    String colorCode;
    String scale = ".1f",maxScale="0.15f",minScale = ".06f";
    LinearLayout mainLL;

    ImageView screenShot,screenShotBackground;

    @SuppressLint("NewApi") private void setActionBar()
    {
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");

        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_core);

        setActionBar();

        File path = new File(Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name));
        if(!path.exists())
            path.mkdirs();

        addToWishListTV = findViewById(R.id.addToWishListTV);
        addToCartTV = findViewById(R.id.addToCartTV);


        mainLL = findViewById(R.id.main_ll);

        screenShot = findViewById(R.id.screenShot);
        screenShotBackground = findViewById(R.id.screenShotBackground);

        addToCart = findViewById(R.id.addToCart);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        addToWishListTV.setOnClickListener(this);
        addToCartTV.setOnClickListener(this);
        addToCart.setOnClickListener(this);
        screenShot.setOnClickListener(this);
        screenShotBackground.setOnClickListener(this);


        String url = getIntent().getStringExtra("modelUrl");
        scale = getIntent().getStringExtra("scale");
        minScale = getIntent().getStringExtra("minScale");
        maxScale = getIntent().getStringExtra("maxScale");
        colorCode =  getIntent().getStringExtra("colorCode");

        Log.d("scalesdf","scale: "+scale+" minScale: "+minScale+"maxScale:  "+maxScale);

        String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
        String filePath = Environment.getExternalStorageDirectory()+"/"+ context.getString(R.string.app_name)+"/"+fileName;

/*        Log.d("fileNamexf","url: "+url);
        Log.d("fileNamexf","fileName: "+fileName+" filePath: "+filePath+" isexist: "+new File(filePath).exists());*/

        if(!new File(filePath).exists())
        {
            if(Utils.isNetworkAvailable(context))
            {
                addToCart.setVisibility(View.GONE);
                arFragment.getView().setVisibility(View.GONE);

                new DownloadFile(context).execute(url);
            }
            else
            {
                Utils.showMessage(context,getString(R.string.error_no_internet));
                finish();
            }
        }
        else
        {
            loadModel(filePath);
        }


        add("1. Please Scan the floor.");
        add("2. Tap over the red panel to place an object.");
        add("3. Hold and move an object.");
        add("4. To Rotate an object please hold it using a finger and use the second finger to rotate it.");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadModel(String path)
    {
        this.modelPath = path;
//        this.modelPath = "/storage/emulated/0/arCoreModel/ArcticFox.sfb";

        addToCart.setVisibility(View.VISIBLE);
        arFragment.getView().setVisibility(View.VISIBLE);


        File f = new File(modelPath);//("/storage/emulated/0/arCoreModel/chair.sfb");
        //       File f = new File("/storage/emulated/0/arCoreModel/ArcticFox.sfb");
        Log.d("fileNamexf","exist: "+f.exists());

        arFragment.getPlaneDiscoveryController().show();

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            mainLL.setVisibility(View.GONE);

            if(!modelPlace)
            {
                Anchor anchor = hitResult.createAnchor();

                ModelRenderable.builder()
                        .setSource(context, Uri.fromFile(new File(modelPath)))//Uri.parse(f.toURI()))
                        .build()
                        .thenAccept(modelRenderable -> addModelToScene(anchor,modelRenderable))
                        .exceptionally(throwable ->
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Error: "+throwable.getMessage()).show();

                            return  null;
                        });
            }
        });

        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {


/*                for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                if (plane.getTrackingState() == TrackingState.TRACKING) {
// Once there is a tracking plane, plane discovery stops.
// do your callback here.
                }
            }*/

/*                arFragment.onUpdate(frameTime);

            // If there is no frame then don't process anything.
            if (arFragment.getArSceneView().getArFrame() == null) {
                return;
            }



            // If ARCore is not tracking yet, then don't process anything.

            Log.d("getTrackingState","getTrackingState"+arFragment.getArSceneView().getPlaneRenderer().isEnabled());

            arFragment.getPlaneDiscoveryController().show();



            if (arFragment.getArSceneView().getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
//                    Log.d("here12344","here 123456");

                return;
            }
            else
            {
//                  Log.d("here12344","here else 123456");
            }

*//*                // Place the anchor 1m in front of the camera if anchorNode is null.
            if (this.anchorNode == null) {
                Session session = arFragment.getArSceneView().getSession();
                float[] pos = { 0,0,-1 };
                float[] rotation = {0,0,0,1};
                Anchor anchor =  session.createAnchor(new Pose(pos, rotation));
                anchorNode = new AnchorNode(anchor);
                anchorNode.setRenderable(andyRenderable);
                anchorNode.setParent(arFragment.getArSceneView().getScene());
            }*/
        });




        Texture.Sampler sampler = Texture.Sampler.builder()
                .setMinFilter(Texture.Sampler.MinFilter.LINEAR)
                .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
                .setWrapMode(Texture.Sampler.WrapMode.REPEAT).build();

        // Build texture with sampler
        CompletableFuture<Texture> trigrid = Texture.builder()
                .setSource(this, R.drawable.trigrid_0)
                .setSampler(sampler).build();

        // Set plane texture
        arFragment.getArSceneView()
                .getPlaneRenderer()
                .getMaterial()
                .thenAcceptBoth(trigrid, (material, texture) -> material.setTexture(PlaneRenderer.MATERIAL_TEXTURE, texture));
    }



    public void addModelToScene(Anchor anchor,ModelRenderable modelRenderable)
    {
        try
        {
            AnchorNode anchorNode = new AnchorNode(anchor);

//        TransformationSystem transformationSystem = new TransformationSystem(arFragment.getTransformationSystem());//getResources().getDisplayMetrics(), new FootprintSelectionVisualizer()

            TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
            transformableNode.setParent(anchorNode);
            transformableNode.getRotationController().setEnabled(true);
            transformableNode.getScaleController().setEnabled(true);
            transformableNode.getTranslationController().setEnabled(true);

            transformableNode.getScaleController().setMinScale(Float.parseFloat(minScale));
            transformableNode.getScaleController().setMaxScale(Float.parseFloat(maxScale));

            // Set the local scale of the node BEFORE setting its parent
            transformableNode.setLocalScale(new Vector3(Float.parseFloat(scale), Float.parseFloat(scale), Float.parseFloat(scale)));

            transformableNode.setRenderable(modelRenderable);
            arFragment.getArSceneView().getScene().addChild(anchorNode);
            transformableNode.select();


            Vector3 worldPosition = anchorNode.getWorldPosition();

            modelPlace = true;
        }
        catch(Exception e)
        {
            Log.d("error:s","getMessage: "+e.getMessage());
        }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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

                addToCartTV.setBackground(ContextCompat.getDrawable(context,R.drawable.rectangle_with_border_yellow));
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
            }
            else
            {
                addedToCart = false;
                exit();
            }
        }
        else if(v.getId() == R.id.screenShotBackground || v.getId() == R.id.screenShot)
        {
            File photo = new File(Environment.getExternalStorageDirectory()+"/"+getResources().getString(R.string.app_name)+"/Pictures");

            Log.d("photo","photo: "+photo.getAbsolutePath());

            if (!photo.exists())
                photo.mkdirs();

            final Bitmap screenShotBitmap = Bitmap.createBitmap(arFragment.getArSceneView().getWidth(), arFragment.getArSceneView().getHeight(), Bitmap.Config.ARGB_8888);

            final HandlerThread handlerThread = new HandlerThread("PixelCopier");
            handlerThread.start();

            PixelCopy.request(arFragment.getArSceneView(), screenShotBitmap, (copyResult) ->
            {
                if (copyResult == PixelCopy.SUCCESS)
                {
                    new SaveImages(context,screenShotBitmap).execute();
                }
                else
                {
                    Log.d("DrawAR", "Failed to copyPixels: " + copyResult);
                    Toast toast = Toast.makeText(this, "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                    toast.show();
                }
                handlerThread.quitSafely();
            }, new Handler(handlerThread.getLooper()));
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
        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        Intent intent=new Intent();
        intent.putExtra("addedToCart",addedToCart);
        setResult(500,intent);
    }


    //add floating content conntent

    int DynamicId = 1;

    void add(String message)
    {
        try
        {
            View skillLayout = getLayoutInflater().inflate(R.layout.basic_layout_indicator, mainLL, false);

            if (skillLayout != null)
            {
                skillLayout.setId(100 + DynamicId);
                skillLayout.setTag(100 + DynamicId);
                mainLL.addView(skillLayout);

                TextView indicatorText = mainLL.findViewById(R.id.indicatorText);
                indicatorText.setTag(4000+DynamicId);
                indicatorText.setId(4000+DynamicId);
                indicatorText.setText(message);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)indicatorText.getLayoutParams();
                params.width = (int) (Utils.getDisplayWidth(context)*.7);

                indicatorText.setLayoutParams(params);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();

//        ArSceneView arSceneView = arFragment.getArSceneView();
        //      Camera coreCamera = arSceneView.getArFrame().getCamera();
        //    if (coreCamera.getTrackingState() != TrackingState.TRACKING) {
        //      return;
        //}
    }

    public void onImageSave(String filePath,String fileName)
    {

    }
}