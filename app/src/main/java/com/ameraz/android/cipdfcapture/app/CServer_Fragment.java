package com.ameraz.android.cipdfcapture.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by adrian.meraz on 8/26/2014.
 */
public class CServer_Fragment extends Fragment {

    static View rootView;
    SharedPreferences preferences;
    private EditText reportName;
    private TextView dsid;
    private TextView bytes;
    private TextView fmt;
    private ImageButton imgb;
    private ImageButton imgb2;
    List<String> spinnerVerArray =  new ArrayList<String>();
    ProgressDialog ringProgressDialog;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater .inflate(R.layout.csserver_fragment, container, false);
        reportName = (EditText) rootView.findViewById(R.id.editText);
        dsid = (TextView) rootView.findViewById(R.id.textView6);
        bytes = (TextView) rootView.findViewById(R.id.textView7);
        fmt = (TextView) rootView.findViewById(R.id.textView8);
        imgb = (ImageButton) rootView.findViewById(R.id.imageButton2);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setFonts();
        searchButtonListener();
        return rootView;
    }

    public void setFonts(){
        TextView txt1 = (TextView) rootView.findViewById(R.id.textView);
        TextView txt2 = (TextView) rootView.findViewById(R.id.textView2);
        TextView txt3 = (TextView) rootView.findViewById(R.id.textView3);
        TextView txt4 = (TextView) rootView.findViewById(R.id.textView4);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        txt1.setTypeface(font);
        txt2.setTypeface(font);
        txt3.setTypeface(font);
        txt4.setTypeface(font);
    }
    public void searchButton() throws IOException, XmlPullParserException, InterruptedException,
            ExecutionException, TimeoutException {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        LoginLogoff liloobj = new LoginLogoff(getActivity());
        final APIQueries apiobj = new APIQueries(getActivity());
        ringProgressDialog = ProgressDialog.show(getActivity(), "Performing Action ...",
                "Searching for report ...", true);
        MainActivity.argslist.add(LoginLogoff.getSid());
        if (apiobj.pingQuery(MainActivity.argslist)) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Login successful and ready to search for reports.");
            //create a topic instance object
            fillSpinner(apiobj);
        }
        else {//if ping fails, selected ci profile will be used to log back in
            Log.d("Message", "Ping to CI server indicated no login session.");
            if(liloobj.tryLogin()) {
                Log.d("Message", "CI Login successful and ready to search for reports.");
                fillSpinner(apiobj);
            }
            else{//if login attempt fails from trying the CI server profile, prompt user to check profile
                ringProgressDialog.dismiss();
                ToastMessageTask tmtask = new ToastMessageTask(getActivity(),"Connection to CI Server failed. Check" +
                        "CI Connection Profile under Settings.");
                tmtask.execute();
            }
        }
    }
    public ArrayList<String> showItems(ArrayList<String> lvers, int sel){
        ArrayList<String> vers = new ArrayList<String>();
        for(String v : lvers){
            String[] pieces = v.split(",");
            vers.add(pieces[sel]);//1=dsid,2=bytes,3=fmt,4=ver
        }
        return vers;
    }

    public void fillSpinner(final APIQueries apiobj){
        if(!reportName.getText().toString().isEmpty()) {
            MainActivity.argslist.add("res," + reportName.getText().toString());
            MainActivity.argslist.add("sid,"+ LoginLogoff.getSid());
            new Thread() {
                public void run() {
                    try {
                        spinnerVerArray = showItems(apiobj.listversionQuery(MainActivity.argslist),4);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createSpinner();
                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    ringProgressDialog.dismiss();
                }

            }.start();
        }

        else{
            ringProgressDialog.dismiss();
            ToastMessageTask tmtask = new ToastMessageTask(getActivity(),"Error. Fill out Report Name field.");
            tmtask.execute();
        }
    }

    public void createSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerVerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) rootView.findViewById(R.id.spinner);
        sItems.setAdapter(adapter);
    }

    private void searchButtonListener() {
        imgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    searchButton();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
