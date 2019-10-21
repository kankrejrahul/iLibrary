package com.intinic.sdk.Helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.intinic.sdk.ARCoreActivity;
import com.intinic.sdk.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class SaveImages extends AsyncTask<String, String, String>
{
    boolean failure = false;
    String FileName;
    ProgressDialog pDialog;
    byte[] byte_arr = null;
    Bitmap bitmap;
    File photo;
    Context mContext;
    String folder_name="";
    boolean hideProgress = false;
    int viewId = 0,pageNumber = 0;

    public SaveImages(Context context,Bitmap bitmap)
    {
        folder_name = context.getResources().getString(R.string.app_name);
        this.bitmap = bitmap;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        if(!hideProgress)
        {
            try
            {
                pDialog = new ProgressDialog(mContext);
                pDialog.setIndeterminate(false);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();
            }
            catch (Exception e)
            {}
        }
    }

    @Override
    protected String doInBackground(String... args)
    {
        try
        {
//                    String Ex = FileName.substring((FileName.lastIndexOf(".") + 1), FileName.length());
                FileName = UUID.randomUUID().toString();
                FileName = FileName+".jpg";

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);//100 is compression ratio
                byte_arr = stream.toByteArray();

                photo=new File(Environment.getExternalStorageDirectory()+"/"+ folder_name+"/Pictures",FileName);

                if (photo.exists())
                {
                    photo.delete();
                }
                try
                {
                    FileOutputStream fos=new FileOutputStream(photo.getPath());
                    fos.write(byte_arr);
                    fos.close();
                }
                catch (java.io.IOException e)
                {}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Log.d("DAPTH",""+String.valueOf(FileName));
        return FileName;
    }

    @Override
    protected void onPostExecute(String message)
    {
        if(pDialog!=null)
        {
            if(pDialog.isShowing())
                pDialog.dismiss();
        }

        Log.d("photo","photo: "+photo.getAbsolutePath());

        if(mContext instanceof ARCoreActivity)
        ((ARCoreActivity)mContext).onImageSave(photo.getAbsolutePath(),FileName);

/*        else if(mContext instanceof SetProfileActivity)
        ((SetProfileActivity)mContext).onImageSave(photo.getAbsolutePath(),FileName);

        else if(mContext instanceof abc)
            ((abc)mContext).onImageSave(viewId,pageNumber,photo.getAbsolutePath());*/

    }
}