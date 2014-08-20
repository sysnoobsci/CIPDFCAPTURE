package com.ameraz.android.cipdfcapture.app;

/**
 * Created by adrian.meraz on 6/24/2014.
 */
        import android.app.ProgressDialog;
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
        import java.net.UnknownHostException;


public class ReqTask extends AsyncTask<String, Void, String> {


    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;
    private String result;
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

    public ReqTask(String query, Context context){
        setTaskID(this.taskID);//set unique ID for task
        setQuery(query);
        setActContext(context);
        taskID++;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... args) {
        XmlParser xmlobj = new XmlParser();
        StringBuilder total = new StringBuilder();
        try {
            Log.d("Variable", "query input value: " + getQuery());
            httppost  = new HttpPost(getQuery());//form http req string and assign to httppost
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            InputStream is = buf.getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Variable", "total.toString() result: " + total.toString());
        if (!xmlobj.isXMLformat(total.toString())) {
            Log.e("XMLFormatError", "XML is malformed");
        }
        setResult(total.toString());
        return total.toString();
    }

    protected void onPostExecute(String result) {
        Log.d("Variable", "onPostExecute result: " + result);
    }
}//end of ReqTask

