package com.ameraz.android.cipdfcapture.app;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ameraz.android.cipdfcapture.app.filebrowser.FileChooser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Upload_Fragment extends Fragment {

    private static final int REQUEST_PATH = 1;

    String curFileName;
    static View rootView;
    EditText edittext;
    Context maContext;
    static ArrayList<String> logonXmlTextTags;
    SharedPreferences preferences;
    ProgressDialog progress;

    Dialog loginDialog = null;

    final static private int LOGIN_TIMEOUT = 500;//time in milliseconds for login attempt to timeout

    public static ArrayList<String> getLogonXmlTextTags() {
        return logonXmlTextTags;
    }

    public static void setLogonXmlTextTags(ArrayList<String> logonXmlTextTags) {
        Upload_Fragment.logonXmlTextTags = logonXmlTextTags;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fileexplorer, container, false);
        maContext = getActivity();//get context from activity
        edittext = (EditText) rootView.findViewById(R.id.editText);
        rootView.findViewById(R.id.skipButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//set listener for browse button
                getfile(v);
            }
        });
        rootView.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//set listener for upload button
                try {
                    uploadButton();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        setupServerNodes();
        return rootView;
    }

    public void setupServerNodes(){
        new Thread(new Runnable() {
            public void run() {
                loginlogoff liloobj = new loginlogoff(maContext);
                try {
                    if (liloobj.tryLogin()) {
                        APIQueries apiobj = new APIQueries(getActivity());
                        if (apiobj.pingserver()) {//check if logged in
                            XmlParser xobj = new XmlParser();
                            ReqTask reqobj4 = new ReqTask(apiobj.listnodeQuery(), maContext);
                            try {
                                reqobj4.execute().get(LOGIN_TIMEOUT, TimeUnit.MILLISECONDS);
                            } catch (TimeoutException te) {
                                ToastMessageTask tmtask = new ToastMessageTask(maContext, "Connection to CI Server failed. Check" +
                                        "CI Connection Profile under Settings.");
                                tmtask.execute();
                            }
                            xobj.parseXMLfunc(reqobj4.getResult());
                            String xidresults = xobj.findTagText("xid");
                            String nameresults = xobj.findTagText("name");
                            Log.d("Variable", "xidresults value: " + xidresults);
                            Log.d("Variable", "nameresults value: " + nameresults);
                            //CONTINUE CODE HERE FOR ADDING SERVER NODES TO SPINNER - GET THE INFO FROM xobj.getTextTag();
                        }
                        //FIX***********
                    }
                }
                catch(Exception e){
                    Log.e("Error - setupServerNodes",e.toString());
                }

            }
        }).start();
    }

    public void getfile(View view){
        Intent intent1 = new Intent(getActivity(), FileChooser.class);
        startActivityForResult(intent1, REQUEST_PATH);
    }

    public void uploadButton() throws IOException, XmlPullParserException, InterruptedException,
            ExecutionException, TimeoutException {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        loginlogoff liloobj = new loginlogoff(maContext);
        APIQueries apiobj = new APIQueries(maContext);
        if (apiobj.pingserver()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Login successful and ready to upload file.");
            //********put in code for uploading file here***********
        }
        else {//if ping fails, selected ci profile will be used to log back in
            Log.d("Message", "Ping to CI server indicated no login session.");
            if(liloobj.tryLogin()) {
                Log.d("Message", "CI Login successful and ready to upload file.");
                //********put in code for uploading file here***********
            }
            else{//if login attempt fails from trying the CI server profile, prompt user to check it
                ToastMessageTask tmtask = new ToastMessageTask(maContext,"Connection to CI Server failed. Check" +
                        "CI Connection Profile under Settings.");
                tmtask.execute();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
        if (requestCode == REQUEST_PATH){
            if (resultCode == Activity.RESULT_OK) {
                curFileName = data.getStringExtra("GetFileName");
                edittext.setText(curFileName);
            }
        }
    }
}
