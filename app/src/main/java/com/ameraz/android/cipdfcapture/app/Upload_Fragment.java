package com.ameraz.android.cipdfcapture.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Upload_Fragment extends Fragment {

    private static final int REQUEST_PATH = 1;

    String curFileName;
    static View rootView;
    EditText filenametext;
    EditText descriptiontext;
    Context maContext;
    static ArrayList<String> logonXmlTextTags;
    SharedPreferences preferences;
    ProgressDialog pd;

    final static private int NVPAIRS = 1;//number of nvpairs in createtopic api call
    final static private String tplid1 = "create.redmine1625";//time in milliseconds for createtopic attempt to timeout


    public static ArrayList<String> getLogonXmlTextTags() {
        return logonXmlTextTags;
    }

    public static void setLogonXmlTextTags(ArrayList<String> logonXmlTextTags) {
        Upload_Fragment.logonXmlTextTags = logonXmlTextTags;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fileexplorer, container, false);
        maContext = getActivity();//get context from activity
        filenametext = (EditText) rootView.findViewById(R.id.editText);
        descriptiontext = (EditText) rootView.findViewById(R.id.editText2);

        rootView.findViewById(R.id.browseButton).setOnClickListener(new View.OnClickListener() {
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
        return rootView;
    }

    public void getfile(View view){
        Intent intent1 = new Intent(getActivity(), FileChooser.class);
        startActivityForResult(intent1, REQUEST_PATH);
    }

    public void uploadButton() throws IOException, XmlPullParserException, InterruptedException,
            ExecutionException, TimeoutException {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        LoginLogoff liloobj = new LoginLogoff(maContext);
        final APIQueries apiobj = new APIQueries(maContext);
        final ProgressDialog ringProgressDialog = ProgressDialog.show(maContext, "Performing Action ...", "Uploading file ...", true);
        if (apiobj.pingQuery()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Login successful and ready to upload file.");
            //create a topic instance object
            if(!filenametext.getText().toString().isEmpty() || !descriptiontext.getText().toString().isEmpty()) {
                final String[] nvpairsarr = new String[NVPAIRS];
                nvpairsarr[0] = "name,"+ descriptiontext.getText().toString();
                final File newImage = new File(FileChooser.getFullFilePath());
                new Thread(){
                    public void run(){
                        try {
                            apiobj.createtopicQuery(tplid1, nvpairsarr, "y", LoginLogoff.getSid(),newImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ringProgressDialog.dismiss();
                    }
                }.start();
            }
            else{
                ringProgressDialog.dismiss();
                ToastMessageTask tmtask = new ToastMessageTask(maContext,"Error. Fill out all the fields.");
                tmtask.execute();
            }

        }
        else {//if ping fails, selected ci profile will be used to log back in
            Log.d("Message", "Ping to CI server  indicated no login session.");
            if(liloobj.tryLogin()) {
                Log.d("Message", "CI Login successful and ready to upload file.");
                if(!filenametext.getText().toString().isEmpty() || !descriptiontext.getText().toString().isEmpty()) {
                    final String[] nvpairsarr = new String[NVPAIRS];
                    nvpairsarr[0] = "name,"+ descriptiontext.getText().toString();
                    final File newImage = new File(FileChooser.getFullFilePath());
                    new Thread(){
                        public void run(){
                            try {
                                apiobj.createtopicQuery(tplid1, nvpairsarr, "y", LoginLogoff.getSid(),newImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ringProgressDialog.dismiss();
                        }
                    }.start();
                }
                else{
                    ringProgressDialog.dismiss();
                    ToastMessageTask tmtask = new ToastMessageTask(maContext,"Error. Fill out all the fields.");
                    tmtask.execute();
                }
            }
            else{//if login attempt fails from trying the CI server profile, prompt user to check profile
                ringProgressDialog.dismiss();
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
                filenametext.setText(curFileName);
            }
        }
    }
}
