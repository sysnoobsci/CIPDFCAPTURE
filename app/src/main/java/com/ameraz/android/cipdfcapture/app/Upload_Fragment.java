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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    ArrayList<String> logonXmlTextTags;
    SharedPreferences preferences;
    ProgressDialog progress;
    Boolean first_open = true;//checks if it is the first time opening upload fragment

    Dialog loginDialog = null;

    final static private int LOGIN_TIMEOUT = 500;//time in milliseconds for login attempt to timeout

    public ArrayList<String> getLogonXmlTextTags() {
        return logonXmlTextTags;
    }

    public void setLogonXmlTextTags(ArrayList<String> logonXmlTextTags) {
        this.logonXmlTextTags = logonXmlTextTags;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater .inflate(R.layout.fragment_fileexplorer, container, false);
        maContext = getActivity();//get context from activity
        edittext = (EditText)rootView.findViewById(R.id.editText);
        rootView.findViewById(R.id.skipButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {//set listener for browse button
                getfile(v);
            }
        });
        rootView.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {//set listener for upload button
            try {
                uploadButton();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        });
        loginlogoff liloobj = new loginlogoff(maContext);
        setCiLoginInfo(liloobj);//set ciprofile from preferences list
        if(tryLogin()){
            //CONTINUE CODE HERE FOR ADDING SERVER NODES TO SPINNER
        }
        //FIX***********
        //Button save = (Button)rootView.findViewById(R.id.save);
        return rootView;
    }

    public void getfile(View view){
        Intent intent1 = new Intent(getActivity(), FileChooser.class);
        startActivityForResult(intent1, REQUEST_PATH);
    }

    public void uploadButton() throws IOException, XmlPullParserException, InterruptedException,
            ExecutionException, TimeoutException {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        XmlParser xobj = new XmlParser();
        APIQueries apiobj = new APIQueries(getActivity());
        ReqTask reqobj4 = new ReqTask(apiobj.pingQuery(), this.getClass().getName(), maContext);
        try{
            reqobj4.execute().get(LOGIN_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        catch(TimeoutException te){
            ToastMessageTask tmtask = new ToastMessageTask(maContext,"Connection to CI Server failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }
        xobj.parseXMLfunc(reqobj4.getResult());
        apiobj.isPingSuccessful(xobj.getTextTag());

        if (apiobj.getPingresult()) {//if the ping is successful(i.e. user logged in)
            //********put in code for uploading file here***********
            Log.d("Message", "CI Login successful and ready to upload file.");
        }
        else {//if ping fails, selected ci profile will be used to log back in
            Log.d("Message", "Ping to CI server indicated no login session.");
            if(tryLogin()) {
                Log.d("Message", "CI Login successful and ready to upload file.");
            }
            else{//if login attempt fails from trying the CI server profile, prompt user to check it
                ToastMessageTask tmtask = new ToastMessageTask(maContext,"Connection to CI Server failed. Check" +
                        "CI Connection Profile under Settings.");
                tmtask.execute();
                //ciloginpingfail();
                //********put in code for uploading file here***********
            }
        }
    }

 public void setCiLoginInfo(loginlogoff liloobj){
     DatabaseHandler dbh = new DatabaseHandler(maContext);
     preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
     String ciserver = preferences.getString("list_preference_ci_servers", "n/a");
     String ciserverResult = dbh.select_ci_server(ciserver);
     String[] parms = ciserverResult.split(",");
     try {
         liloobj.setHostname(parms[2]);
         liloobj.setDomain(parms[3]);
         liloobj.setPortnumber(Integer.parseInt(parms[4]));
         liloobj.setUsername(parms[5]);
         liloobj.setPassword(parms[6]);
     }
     catch(ArrayIndexOutOfBoundsException aiob){
         Log.e("Error", aiob.toString());
         ToastMessageTask tmtask = new ToastMessageTask(maContext,"Select a CI Profile under Settings.");
         tmtask.execute();
     }
 }

 public Boolean tryLogin(){
        Boolean loginTry = false;
        final loginlogoff liloobj = new loginlogoff(maContext);//passed in context of this activity
        setCiLoginInfo(liloobj);
        new Thread(new Runnable() {
            public void run() {
                APIQueries apiobj = new APIQueries(getActivity());
                final ReqTask reqobj = new ReqTask(apiobj.logonQuery(liloobj.getUsername(),
                        liloobj.getPassword(), null),//send login query to CI via asynctask
                        this.getClass().getName(), maContext);
                try {
                    reqobj.execute().get(LOGIN_TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    ToastMessageTask tmtask = new ToastMessageTask(maContext, "Logon attempt timed out.");
                    tmtask.execute();
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //progress.dismiss();
                        XmlParser xobj3 = new XmlParser();
                        try {
                            xobj3.parseXMLfunc(reqobj.getResult());
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("Variable", "reqobj.getResult() value is: " + reqobj.getResult());
                        setLogonXmlTextTags(xobj3.getTextTag());
                        //check if login worked
                        liloobj.isLoginSuccessful(getLogonXmlTextTags());//check if login was successful
                        liloobj.logonMessage();//show status of login
                    }
                });//end of UiThread
            }
        }).start();
        if (liloobj.getLogin_successful()){
            loginTry = true;
        }
        return loginTry;
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
