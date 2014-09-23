package com.ameraz.android.cipdfcapture.app.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
    String path;
    String filePathName;
    String extension;
    HttpClient httpClient = new DefaultHttpClient();
    HttpPost request;
    HttpResponse response;
    Boolean success = false;
    Calendar cal = Calendar.getInstance();
    ProgressDialog ringProgressDialog;

    public DownloadFileTask(String url, String path, String filePathName, String extension, Context context) {
        this.url = url;
        this.path = path;
        this.filePathName = filePathName;
        this.extension = "." + extension;
        request = new HttpPost(url);
        ringProgressDialog = new ProgressDialog(context);
    }

    private void setDownloadingFileDialog() {
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Downloading File ...");
    }

    protected void checkDirExists(String pathName) {
        File file = new File(pathName);
        if (file.isDirectory() && file.exists()) {
            Log.d("checkDirExists()", pathName + " exists");
        } else {
            Log.d("checkDirExists()", pathName + " does not exist");
            Log.d("checkDirExists()", "creating directory: " + pathName);
            file.mkdir();
        }
    }

    protected String doInBackground(String... params) {
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getStatusLine().getStatusCode() != 401) {
            checkDirExists(path);
            if (response != null) {
                String fullFilename = path + cal.getTimeInMillis() + extension.toLowerCase();
                File file = new File(fullFilename);
                Log.d("dlAndWriteFile()", "File name: " + fullFilename);
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
    protected void onPostExecute(String result) {
        ringProgressDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        setDownloadingFileDialog();
        ringProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
