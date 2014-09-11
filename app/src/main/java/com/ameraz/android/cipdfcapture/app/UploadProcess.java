package com.ameraz.android.cipdfcapture.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by adrian.meraz on 9/11/2014.
 */
public class UploadProcess {

    Context context;
    EditText description;
    Uri imageUri;
    ProgressDialog ringProgressDialog;
    String topicTemplateName;
    SharedPreferences preferences;
    APIQueries apiobj;

    public UploadProcess(Context context, EditText description, Uri imageUri,
                         ProgressDialog ringProgressDialog) {
        setContext(context);
        setDescription(description);
        setImageUri(imageUri);
        setRingProgressDialog(ringProgressDialog);
        apiobj = new APIQueries(getContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        topicTemplateName = preferences.getString("camName_preference", null);
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

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public ProgressDialog getRingProgressDialog() {
        return ringProgressDialog;
    }

    public void setRingProgressDialog(ProgressDialog ringProgressDialog) {
        this.ringProgressDialog = ringProgressDialog;
    }

    public void uploadProcess() throws Exception {
        LogonSession lsobj = new LogonSession(getContext());
        if (uploadCheck(getDescription(), getImageUri())) {
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

    void createTopic() throws Exception {
        if (topicTemplateName != null) {

            QueryArguments.addArg("tplid," + topicTemplateName);
            QueryArguments.addArg("name," + description.getText().toString());
            QueryArguments.addArg("detail,y");
            QueryArguments.addArg("sid," + LogonSession.getSid());
            QueryArguments.addArg(imageUri);
            try {
                apiobj.createtopicQuery(QueryArguments.getArgslist());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastMessageTask.noValidTopicTemplateSpecified(getContext());
        }
        ringProgressDialog.dismiss();
    }

    Boolean uploadCheck(EditText description, Uri imageUri) {
        if (imageUri == null) {//checks if image taken yet
            ToastMessageTask.picNotTaken(getContext());
            return false;
        }
        if (String.valueOf(description.getText()).isEmpty()) {
            ToastMessageTask.fillFieldMessage(getContext());
            return false;
        }
        return true;//if pic was taken and there is a non-empty description, return true
    }
}
