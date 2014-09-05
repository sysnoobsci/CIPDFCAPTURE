package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by adrian.meraz on 5/29/2014.
 */
// A class that will run Toast messages in the main GUI context
public class ToastMessageTask extends AsyncTask<String, String, String> {
    static String toastMessage;
    static Context mContext;

    protected ToastMessageTask(Context mContext, String toastMessage) {
        this.mContext=mContext;
        this.toastMessage=toastMessage;
    }
    @Override
    protected String doInBackground(String... params) {
        return toastMessage;
    }

    protected void OnProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }
    // This is executed in the context of the main GUI thread
    protected void onPostExecute(String result){
        Toast toast = Toast.makeText(mContext, result, Toast.LENGTH_SHORT);
        toast.show();
    }

    protected static void noConnectionMessage(Context context){
        ToastMessageTask tmtask = new ToastMessageTask(context, "Connection to CI Server failed/timed out. Check" +
                "CI Connection Profile under Settings.");
        tmtask.execute();
    }

    protected static void reportNotValidMessage(Context context) {
        ToastMessageTask tmtask = new ToastMessageTask(context, "Report Name not found.");
        tmtask.execute();
    }
}