package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by adrian.meraz on 5/29/2014.
 */
// A class that will run Toast messages in the main GUI context
public class ToastMessageTask extends AsyncTask<String, String, String> {
    static String toastMessage;
    static Context mContext;

    protected ToastMessageTask(Context mContext, String toastMessage) {
        setmContext(mContext);
        setToastMessage(toastMessage);
    }

    public static String getToastMessage() {
        return toastMessage;
    }

    public static void setToastMessage(String toastMessage) {
        ToastMessageTask.toastMessage = toastMessage;
    }

    public static Context getmContext() {
        return mContext;
    }

    public static void setmContext(Context mContext) {
        ToastMessageTask.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {
        return toastMessage;
    }

    // This is executed in the context of the main GUI thread
    protected void onPostExecute(String result) {
        Log.d("ToastMessageTask.onPostExecute()", result);
        Toast toast = Toast.makeText(getmContext(), result, Toast.LENGTH_SHORT);
        toast.show();
    }

    protected static void noConnectionMessage(Context context) {
        ToastMessageTask tmtask = new ToastMessageTask(context, "Connection to CI Server failed/timed out. Check " +
                "CI Connection Profile under Settings.");
        tmtask.execute();
    }

    protected static void reportNotValidMessage(Context context) {
        ToastMessageTask tmtask = new ToastMessageTask(context, "Report Name not found.");
        tmtask.execute();
    }

    protected static void fillFieldMessage(Context context) {
        ToastMessageTask tmtask = new ToastMessageTask(context, "Error. Fill out all the fields.");
        tmtask.execute();
    }

    protected static void fileUploadStatus(Context context, Boolean flag) {
        if (flag) {
            Log.d("fileUploadStatus()", "File was successfully Uploaded.");
            ToastMessageTask tmtask = new ToastMessageTask(context, "File was successfully Uploaded.");
            tmtask.execute();
        } else {
            Log.d("fileUploadStatus()", "File upload failed.");
            ToastMessageTask tmtask = new ToastMessageTask(context, "File upload failed.");
            tmtask.execute();
        }
    }

    protected static void fileNotWritten(Context context) {
        ToastMessageTask tmtask = new ToastMessageTask(context, "Error writing file to local storage");
        tmtask.execute();
    }

    protected static void picNotTaken(Context context) {
        ToastMessageTask tmtask = new ToastMessageTask(context, "Error. No image has been taken yet.");
        tmtask.execute();
    }

    protected static void noProfileSelected(Context context) {
        ToastMessageTask tmtask = new ToastMessageTask(context, "Error. Select a connection profile.");
        tmtask.execute();
    }
}