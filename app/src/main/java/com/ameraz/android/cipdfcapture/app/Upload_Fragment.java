package com.ameraz.android.cipdfcapture.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.ameraz.android.cipdfcapture.app.filebrowser.FileChooser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Upload_Fragment extends Fragment {

    private static final int REQUEST_PATH = 1;

    String curFileName;
    static View rootView;
    EditText filenametext;
    EditText reportnametext;
    Context maContext;
    static ArrayList<String> logonXmlTextTags;
    ArrayList<String> xidList;
    ArrayList<String> nameList;
    SharedPreferences preferences;


    final static private int LOGIN_TIMEOUT = 500;//time in milliseconds for login attempt to timeout
    final static private int CT_TIMEOUT = 500;//time in milliseconds for createtopic attempt to timeout
    final static private int NVPAIRS = 2;//number of nvpairs in createtopic api call
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
        reportnametext = (EditText) rootView.findViewById(R.id.editText2);

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
        //getServerNodesSpinner();

        return rootView;
    }
    /*
    public void getServerNodesSpinner(){
        new Thread(new Runnable() {
            public void run() {
                loginlogoff liloobj = new loginlogoff(maContext);

                try {
                    if (liloobj.tryLogin()) {
                        APIQueries apiobj = new APIQueries(getActivity());
                        if (apiobj.pingserver()) {//check if logged in
                            XmlParser xobj = new XmlParser();
                            ReqTask reqobj4 = new ReqTask(apiobj.listnodeQuery(loginlogoff.getSid()), maContext);
                            try {
                                reqobj4.execute().get(LOGIN_TIMEOUT, TimeUnit.MILLISECONDS);
                            } catch (TimeoutException te) {
                                ToastMessageTask tmtask = new ToastMessageTask(maContext, "Connection to CI Server failed. Check" +
                                        "CI Connection Profile under Settings.");
                                tmtask.execute();
                            }
                            xobj.parseXMLfunc(reqobj4.getResult());
                            String xidresults = xobj.findTagText("xid",reqobj4.getResult());
                            String nameresults = xobj.findTagText("name",reqobj4.getResult());
                            Log.d("Variable", "xidresults value: " + xidresults);
                            Log.d("Variable", "nameresults value: " + nameresults);
                            xidList = stringSplitter(xidresults);
                            nameList = stringSplitter(nameresults);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sp1 = (Spinner) rootView.findViewById(R.id.spinner);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(maContext, android.R.layout.simple_spinner_item, nameList);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    sp1.setAdapter(adapter);
                                }
                            });

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    */
    public ArrayList<String> stringSplitter(String splitee){
        ArrayList<String> arrList = new ArrayList<String>();
        String[] strArr = splitee.split(",");
        for(String element : strArr){
            if(!element.equals("")){//if the element is not empty, add it
                arrList.add(element);
                Log.d("Arrlist", "element value: " + element);
            }
        }
        return arrList;
    }

    public void getfile(View view){
        Intent intent1 = new Intent(getActivity(), FileChooser.class);
        startActivityForResult(intent1, REQUEST_PATH);
    }

    public String getTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeStamp = dateFormat.format(new Date()); // Find todays date
        return currentTimeStamp;
    }

    public void uploadButton() throws IOException, XmlPullParserException, InterruptedException,
            ExecutionException, TimeoutException {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        TopicInstance tiobj;
        XmlParser xobj = new XmlParser();
        loginlogoff liloobj = new loginlogoff(maContext);
        APIQueries apiobj = new APIQueries(maContext);
        if (apiobj.pingserver()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Login successful and ready to upload file.");
            //create a topic instance object
            if(!filenametext.getText().toString().isEmpty() || !reportnametext.getText().toString().isEmpty()) {
                tiobj = new TopicInstance(reportnametext.getText().toString(),FileChooser.getFullFilePath());
                String[] nvpairsarr = new String[NVPAIRS];
                nvpairsarr[0] = "file,"+tiobj.getFilebuffer();
                nvpairsarr[1] = "name,"+tiobj.getRname();
                ReqTask reqobj4 = new ReqTask(apiobj.createtopicQuery(tplid1, nvpairsarr, "y"),maContext);
                try {
                    reqobj4.execute().get(CT_TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (TimeoutException te) {
                    ToastMessageTask tmtask = new ToastMessageTask(maContext, "Create topic call failed. Check" +
                            "CI Connection Profile under Settings.");
                    tmtask.execute();
                }
                xobj.parseXMLfunc(reqobj4.getResult());
                if(xobj.goodRC(xobj.getXmlstring())){//if return codes are good, it was successful
                    ToastMessageTask tmtask = new ToastMessageTask(maContext,"File was successfully Uploaded.");
                    tmtask.execute();
                }
                else{
                    ToastMessageTask tmtask = new ToastMessageTask(maContext,"File upload failed.");
                    tmtask.execute();
                }
            }
            else{
                ToastMessageTask tmtask = new ToastMessageTask(maContext,"Error. Fill out all the fields.");
                tmtask.execute();
            }

        }
        else {//if ping fails, selected ci profile will be used to log back in
            Log.d("Message", "Ping to CI server indicated no login session.");
            if(liloobj.tryLogin()) {
                Log.d("Message", "CI Login successful and ready to upload file.");
                if(!filenametext.getText().toString().isEmpty() || !reportnametext.getText().toString().isEmpty()) {
                    tiobj = new TopicInstance(reportnametext.getText().toString(),FileChooser.getFullFilePath());
                    String[] nvpairsarr = new String[NVPAIRS];
                    nvpairsarr[0] = "file,"+tiobj.getFilebuffer();
                    nvpairsarr[1] = "name,"+tiobj.getRname();
                    ReqTask reqobj4 = new ReqTask(apiobj.createtopicQuery(tplid1, nvpairsarr, "y"),maContext);
                    try {
                        reqobj4.execute().get(CT_TIMEOUT, TimeUnit.MILLISECONDS);
                    } catch (TimeoutException te) {
                        ToastMessageTask tmtask = new ToastMessageTask(maContext, "Create topic call failed. Check" +
                                "CI Connection Profile under Settings.");
                        tmtask.execute();
                    }
                    xobj.parseXMLfunc(reqobj4.getResult());
                    if(xobj.goodRC(xobj.getXmlstring())){//if return codes are good, it was successful
                        ToastMessageTask tmtask = new ToastMessageTask(maContext,"File was successfully Uploaded.");
                        tmtask.execute();
                    }
                    else{
                        ToastMessageTask tmtask = new ToastMessageTask(maContext,"File upload failed.");
                        tmtask.execute();
                    }
                }
                else{
                    ToastMessageTask tmtask = new ToastMessageTask(maContext,"Error. Fill out all the fields.");
                    tmtask.execute();
                }
                //********put in code for uploading file here***********
            }
            else{//if login attempt fails from trying the CI server profile, prompt user to check profile
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
