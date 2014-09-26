package com.ameraz.android.cipdfcapture.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by The Bat Cave on 9/25/2014.
 */
public class DownloadFileTaskTest extends AsyncTask<String, String, String> {

    String dirPath;
    String fullFilePathName;
    Context context;
    private ProgressDialog mProgressDialog;

    public DownloadFileTaskTest(String dirPath, String fullFilePathName, Context context) {
        this.dirPath = dirPath;
        this.fullFilePathName = fullFilePathName;
        this.context = context;
    }

    void setDialogParms() {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Downloading file...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        setDialogParms();
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... aurl) {
        int count;

        try {
            URL url = new URL(aurl[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();

            int lengthOfFile = conexion.getContentLength();
            Log.d("ANDRO_ASYNC", "Length of file: " + lengthOfFile);
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(fullFilePathName);

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress(""+(int)((total*100)/lengthOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(String... progress) {
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String unused) {
        Log.d("DownloadFileTaskTest","File " + fullFilePathName + " written");
        mProgressDialog.dismiss();
    }

}