package com.intinic.sdk;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

class DownloadFile extends AsyncTask<String, String, String>
{
    Context context;
    URL url;
    String filePath = "";
    private ProgressDialog progressDialog;

    public DownloadFile(Context context)
    {
        this.context = context;
        Log.d("fileNamexf", "fileNamexf: here 1");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    @Override
    protected String doInBackground(String... f_url)
    {
        int count;
        try
        {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();

            String fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());


            int lenghtOfFile = conection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            File path = new File(Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name));
            if (!path.exists())
                path.mkdirs();

            // Output stream to write file
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name) + "/" + fileName);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();


            filePath = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name) + "/" + fileName;

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    protected void onProgressUpdate(String... progress)
    {
        progressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String file_url)
    {
        progressDialog.dismiss();

        Log.d("fileNamexf","filePath: "+filePath);

        if (filePath.length()>0 && context instanceof ARCoreActivity)
            ((ARCoreActivity) context).loadModel(filePath);
    }
}