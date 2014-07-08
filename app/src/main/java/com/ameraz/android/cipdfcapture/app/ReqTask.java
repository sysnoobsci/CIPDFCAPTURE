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


class ReqTask extends AsyncTask<String, Void, String> {

    String result = "No result";

    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost("http://www.yoursite.com/");

    private String query;
    private String className;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public ReqTask(String query, String className, Context context){
        setTaskID(this.taskID);//set unique ID for task
        setQuery(query);
        setClassName(className);
        Log.d("Variable", "ReqTask" + getTaskID() + " context value: " + context);
        setActContext(context);
        taskID++;
    }

    @Override
    protected void onPreExecute() {
        //dialog = ProgressDialog.show(getActivityContext(),"Logging in...","Please Wait",false);
    }

    @Override
    protected String doInBackground(String... args) {
        XmlParser xmlobj = new XmlParser();
        StringBuilder total = new StringBuilder();
        try {
            HttpPost httptemp = new HttpPost(getQuery());//form http req string and assign to httppost
            Log.d("Variable", getClassName() + ".java - query input result: " + getQuery());
            httppost = httptemp;
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
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastMessageTask tmtask = new ToastMessageTask(mContext,"Connection to CI Server failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }

        Log.d("Variable", "Caller " + getClassName() + ".java - total.toString() result: " + total.toString());
        if (!xmlobj.isXMLformat(total.toString())) {
            Log.e("XMLFormatError", getClassName() + ".java - XML is malformed");
        }
        setResult(total.toString());
        return total.toString();
    }

    protected void onPostExecute(String result) {
        Log.d("Variable", "Task Executed from " + this.getClass().getName() + " + result: " + result);
    }
}//end of ReqTask

