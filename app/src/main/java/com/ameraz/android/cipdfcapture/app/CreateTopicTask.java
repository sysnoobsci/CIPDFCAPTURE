package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ameraz.android.cipdfcapture.app.filebrowser.FileChooser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by adrian.meraz on 8/19/2014.
 */
class CreateTopicTask  extends AsyncTask<String, Void, String> {

    String result = "No result";

    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost("http://www.yoursite.com/");

    private String query;
    private Context mContext;
    private static int taskID = 0;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Context getActContext() {
        return mContext;
    }

    public void setActContext(Context mContext) {
        this.mContext = mContext;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public CreateTopicTask(String query, Context context){
        setTaskID(this.taskID);//set unique ID for task
        setQuery(query);
        setActContext(context);
        taskID++;
    }
    @Override
    protected String doInBackground(String... args) {

        // Do your work here
        return "YEAH";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}


