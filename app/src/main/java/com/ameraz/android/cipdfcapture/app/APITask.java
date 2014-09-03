package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by The Bat Cave on 8/19/2014.
 */
public class APITask extends AsyncTask<String, Void, String> {


    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;

    private static BufferedHttpEntity bufhttpEntity;
    private static HttpResponse httpResponse;
    private static String response;
    private static String query;
    private static Context mContext;
    private static HttpEntity entity;
    private static int taskID = 0;

    public static BufferedHttpEntity getBufhttpEntity() {
        return bufhttpEntity;
    }

    public static void setBufhttpEntity(BufferedHttpEntity bufhttpEntity) {
        APITask.bufhttpEntity = bufhttpEntity;
    }

    public static HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public static void setHttpResponse(HttpResponse httpResponse) {
        APITask.httpResponse = httpResponse;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String result) {
        this.response = result;
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

    public HttpEntity getEntity() {
        return entity;
    }

    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public APITask(String query, HttpEntity entity, Context context){
        setTaskID(this.taskID);//set unique ID for task
        setQuery(query);
        setEntity(entity);
        setActContext(context);
        taskID++;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... args) {
        StringBuilder total = new StringBuilder();
        httppost = new HttpPost(getQuery());
        httppost.setEntity(getEntity());
        try {
            HttpResponse response = httpclient.execute(httppost);
            setHttpResponse(response);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            setBufhttpEntity(buf);
            InputStream is = buf.getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setResponse(total.toString());
        return total.toString();
    }

    protected void onPostExecute(String result) {
        Log.d("Variable", "APITasks[" + getTaskID() + "].onPostExecute response: " + getResponse());
    }
}//end of ReqTask
