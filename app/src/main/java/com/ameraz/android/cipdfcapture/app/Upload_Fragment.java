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
        XmlParser xobj = new XmlParser();
        APIQueries apiobj = new APIQueries(getActivity());
        ReqTask reqobj4 = new ReqTask(apiobj.pingQuery(), this.getClass().getName(), maContext);
        reqobj4.execute().get(LOGIN_TIMEOUT, TimeUnit.MILLISECONDS);
        xobj.parseXMLfunc(reqobj4.getResult());
        apiobj.isPingSuccessful(xobj.getTextTag());

        if (apiobj.getPingresult()) {//if the ping is successful(i.e. user logged in)
            //********put in code for uploading file here***********
        } else {//if ping fails, user must log in first
                cilogin();
        }


    }
    // Listen for results.
    public void cilogin(){
        loginDialog = new Dialog(getActivity());
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loginDialog.setContentView(R.layout.login_dialog);

        // Set GUI of loginDialog screen
        final EditText hostname = (EditText) loginDialog.findViewById(R.id.hostname);
        final EditText domain = (EditText) loginDialog.findViewById(R.id.domain);
        final EditText port = (EditText) loginDialog.findViewById(R.id.port);
        final EditText username = (EditText) loginDialog.findViewById(R.id.username);
        final EditText password = (EditText) loginDialog.findViewById(R.id.password);
        final Button cancel = (Button) loginDialog.findViewById(R.id.cancel_button);
        final Button loginButton = (Button) loginDialog.findViewById(R.id.login_button);

        loginDialog.show();//show the login dialog box
        //Closes app if they try to back out of dialog
        loginDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                loginDialog.dismiss();
                //may need to use fragment manager to swap out fragments here
            }
        });
        //Listener for loginDialog button
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                Log.d("Message", "Login button clicked");
                final loginlogoff liloobj = new loginlogoff(maContext);//passed in context of this activity
                liloobj.setHostname(hostname.getText().toString());
                liloobj.setDomain(domain.getText().toString());
                liloobj.setPortnumber(Integer.parseInt(port.getText().toString()));
                liloobj.setUsername(username.getText().toString());
                liloobj.setPassword(password.getText().toString());
                //progress = ProgressDialog.show(getActivity(), "Logging in...", "Please Wait", true);
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
                                loginlogoff lobj = new loginlogoff(maContext);
                                lobj.isLoginSuccessful(getLogonXmlTextTags());//check if login was successful
                                lobj.logonMessage();//show status of login
                                if (lobj.getLogin_successful()) {//if login is true,dismiss login screen
                                    loginDialog.dismiss();
                                }
                            }
                        });//end of UiThread
                    }
                }).start();
            }
        });
        //Listener for Cancel Button
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                loginDialog.dismiss();
            }
        });
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
