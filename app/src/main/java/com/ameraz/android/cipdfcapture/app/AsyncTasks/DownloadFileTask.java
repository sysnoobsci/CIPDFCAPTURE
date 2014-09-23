package com.ameraz.android.cipdfcapture.app.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.ameraz.android.cipdfcapture.app.FilePath;

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
    String pathName;
    HttpClient httpClient = new DefaultHttpClient();
    HttpPost request;
    HttpResponse response;
    Boolean success = false;
    Calendar cal = Calendar.getInstance();

    public DownloadFileTask(String url,String pathName) {
        this.url = url;
        request = new HttpPost(url);
    }

    protected String doInBackground(String... params) {
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getStatusLine().getStatusCode() != 401) {
            if (response != null) {
                File file = new File(FilePath.getTempFilePath() + cal.getTime());
                Log.d("dlAndWriteFile()", "File name: " + FilePath.getTempFilePath() + cal.getTime());
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
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
