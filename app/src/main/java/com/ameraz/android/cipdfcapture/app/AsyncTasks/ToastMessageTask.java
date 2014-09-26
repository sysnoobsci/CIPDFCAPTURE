package com.ameraz.android.cipdfcapture.app.AsyncTasks;

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

    public ToastMessageTask(Context mContext, String toastMessage) {
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

    protected String doInBackground(String... params) {
        return getToastMessage();
    }

    // This is executed in the context of the main GUI thread
    protected void onPostExecute(String result) {
        Toast toast = Toast.makeText(getmContext(), getToastMessage(), Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void noConnectionMessage(Context context) {
        setmContext(context);
        ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Connection to CI Server failed/timed out. Check " +
                "CI Connection Profile under Settings.");
        tmtask.execute();
    }

    public static void reportNotValidMessage(Context context) {
        setmContext(context);
        ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Report Name not found.");
        tmtask.execute();
    }

    public static void fillFieldMessage(Context context) {
        setmContext(context);
        ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Error. Fill out all the fields.");
        tmtask.execute();
    }

    public static void fileUploadStatus(Context context, Boolean flag) {
        setmContext(context);
        if (flag) {
            Log.d("fileUploadStatus()", "File was successfully Uploaded.");
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "File was successfully Uploaded.");
            tmtask.execute();
        } else {
            Log.d("fileUploadStatus()", "File upload failed.");
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "File upload failed.");
            tmtask.execute();
        }
    }

    public static void fileNotWritten(Context context) {
        setmContext(context);
        ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Error writing file to local storage");
        tmtask.execute();
    }

    public static void picNotTaken(Context context) {
        setmContext(context);
        ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Error. No image has been taken yet.");
        tmtask.execute();
    }

    public static void noProfileSelected(Context context) {
        setmContext(context);
        ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Error. Select a connection profile.");
        tmtask.execute();
    }

    public static void noValidTopicTemplateSpecified(Context context) {
        setmContext(context);
        ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Error. Topic template id is invalid. Check settings.");
        tmtask.execute();
    }

    public static void pdfConversionFailed(Context context) {
        setmContext(context);
        ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Error. PDF conversion failed.");
        tmtask.execute();
    }

    public static void downloadFileSuccessful(Context context) {
        setmContext(context);
        ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "File has been downloaded successfully.");
        tmtask.execute();
    }

    public static void downloadFileStarted(Context context) {
        setmContext(context);
        ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Beginning download of file.");
        tmtask.execute();
    }

    public static void downloadTempFileStarted(Context context) {
        setmContext(context);
        ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Beginning download of temp file.");
        tmtask.execute();
    }


}