package com.ameraz.android.cipdfcapture.app.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by adrian.meraz on 9/22/2014.
 */
public class DownloadFileTask extends AsyncTask<String, Void, String> {

    String url;
    String dirPath;
    String fullFilePathName;
    HttpClient httpClient = new DefaultHttpClient();
    HttpPost request;
    HttpResponse response;
    Boolean success = false;
    Context context;

    public DownloadFileTask(String url, String dirPath, String fullFilePathName, Context context) {
        this.url = url;
        this.dirPath = dirPath;
        this.fullFilePathName = fullFilePathName;
        request = new HttpPost(url);
        this.context = context;
    }


    protected void checkDirExists(String dirPath) {
        File file = new File(dirPath);
        if (file.isDirectory() && file.exists()) {
            Log.d("checkDirExists()", dirPath + " exists");
        } else {
            Log.d("checkDirExists()", dirPath + " does not exist");
            Log.d("checkDirExists()", "creating directory: " + dirPath);
            file.mkdir();
        }
    }

    protected String doInBackground(String... params) {
        Log.d("DownloadFileTask","Task is executing");
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getStatusLine().getStatusCode() != 401) {
            checkDirExists(dirPath);//check if dir exists, otherwise create it
            if (response != null) {
                File file = new File(fullFilePathName);
                Log.d("dlAndWriteFile()", "File name: " + fullFilePathName);
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    HttpEntity entity = response.getEntity();
                    FileOutputStream fileOS = new FileOutputStream(file);
                    entity.writeTo(fileOS);
                    entity.consumeContent();
                    fileOS.flush();
                    fileOS.close();
                    success = true;
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return String.valueOf(success);
    }

    @Override
    protected void onPreExecute() {
        Log.d("DownloadFileTask","Download starting");
        ToastMessageTask.downloadFileStarted(context);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("DownloadFileTask","Download finished");
        ToastMessageTask.downloadFileSuccessful(context);
    }



    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
