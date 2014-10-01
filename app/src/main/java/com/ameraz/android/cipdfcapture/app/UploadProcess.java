package com.ameraz.android.cipdfcapture.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMessageTask;

/**
 * Created by adrian.meraz on 9/11/2014.
 */
public class UploadProcess {

    Context context;
    EditText description;
    Object file2upload;
    ProgressDialog ringProgressDialog;
    String topicTemplateName;
    SharedPreferences preferences;
    APIQueries apiobj;
    private boolean success;

    public UploadProcess(Context context, EditText description, Object file2upload,
                         ProgressDialog ringProgressDialog) {
        setContext(context);
        setDescription(description);
        setFile2upload(file2upload);
        setRingProgressDialog(ringProgressDialog);
        apiobj = new APIQueries(getContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        topicTemplateName = preferences.getString("camName_preference", null);
    }
    public boolean getSuccess(){
        return success;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public EditText getDescription() {
        return description;
    }

    public void setDescription(EditText description) {
        this.description = description;
    }

    public Object getFile2upload() {
        return file2upload;
    }

    public void setFile2upload(Object file2upload) {
        this.file2upload = file2upload;
        Log.d("Upload Process ImageUri= ", file2upload.toString());
    }

    public ProgressDialog getRingProgressDialog() {
        return ringProgressDialog;
    }

    public void setRingProgressDialog(ProgressDialog ringProgressDialog) {
        this.ringProgressDialog = ringProgressDialog;
    }

    public void uploadProcess() throws Exception {
        Log.d("uploadProcess()", "uploadProcess() called.");
        LogonSession lsobj = new LogonSession(getContext());
        if (uploadCheck(getDescription(), getFile2upload())) {
            Log.d("uploadProcess()", "getContext() value: " + getContext());
            Boolean logonStatus = lsobj.tryLogin(getContext());
            if (logonStatus) {
                Log.d("Message", "CI Login successful and ready to upload file.");
                createTopic();//create a topic instance object
            } else {
                Log.d("Message", "CI Login failed. Unable to load file.");
            }
        }
        ringProgressDialog.dismiss();
    }

    void createTopic(){
        if (topicTemplateName != null) {

            QueryArguments.addArg("tplid," + topicTemplateName);
            QueryArguments.addArg("name," + description.getText().toString());
            QueryArguments.addArg("detail,y");
            QueryArguments.addArg("sid," + LogonSession.getSid());
            QueryArguments.addArg(file2upload);
            Log.d("Upload Process ImageUri= ", file2upload.toString());
            try {
                success = apiobj.createtopicQuery(QueryArguments.getArgslist());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastMessageTask.noValidTopicTemplateSpecified(getContext());
        }
        ringProgressDialog.dismiss();
    }

    Boolean uploadCheck(EditText description, Object file2upload) {
        if (file2upload == null) {//checks if image taken yet - or if object is valid if not an image
            ToastMessageTask.picNotTaken(getContext());
            return false;
        }
        if (String.valueOf(description.getText()).isEmpty()) {
            ToastMessageTask.fillFieldMessage(getContext());
            return false;
        }
        Log.d("uploadCheck()", "upload check passed.");
        return true;//if pic was taken and there is a non-empty description, return true
    }
}
