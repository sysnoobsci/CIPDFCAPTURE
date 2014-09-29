package com.ameraz.android.cipdfcapture.app.AsyncTasks;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.TempFileTracker;
import com.ameraz.android.cipdfcapture.app.VersionInfo;
import com.ameraz.android.cipdfcapture.app.fragments.DownloadView_Fragment;
import com.ameraz.android.cipdfcapture.app.fragments.Image_Preview_Fragment;

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
    int fragmentChooser = 0;
    Activity activity;

    public DownloadFileTaskTest(String dirPath, String fullFilePathName, Activity activity, Context context) {
        this.dirPath = dirPath;
        this.fullFilePathName = fullFilePathName;
        this.activity = activity;
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
    protected String doInBackground(String... args) {//1st arg is a url, 2nd is a flag
        int count;
        try {
            URL url = new URL(args[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            if(args.length > 1) {
                if (args[1] != null) {
                    fragmentChooser = Integer.parseInt(args[1]);
                }
            }
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
        if(fragmentChooser != 0){
            if (fragmentChooser == 1){
                callIP_Fragment();
            }
            else{
                Log.d("DownloadFileTask.onPostExecute","Invalid fragment chosen");
            }
        }
        Log.d("DownloadFileTaskTest","File " + fullFilePathName + " written");
        mProgressDialog.dismiss();
    }

    private void callIP_Fragment() {
        Bundle bundle = new Bundle();
        bundle.putString("retrieve_fileName", TempFileTracker.getTempFilePath(VersionInfo.getVersion()));
        bundle.putString("retrieve_fileFormat", VersionInfo.getFormat());
        Fragment fragment = new Image_Preview_Fragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

}