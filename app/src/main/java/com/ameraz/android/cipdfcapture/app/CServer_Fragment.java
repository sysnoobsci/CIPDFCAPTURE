package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
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
    private ImageView imageView;
    private ImageButton imageButton2;
    Spinner sItems;
    List<String> spinnerVerArray =  new ArrayList<String>();
    ArrayList<String> versInfo = new ArrayList<String>();
    ProgressDialog ringProgressDialog;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater .inflate(R.layout.csserver_fragment, container, false);
        final APIQueries apiobj = new APIQueries(getActivity());
        reportName = (EditText) rootView.findViewById(R.id.editText);
        imageButton2 = (ImageButton) rootView.findViewById(R.id.imageButton2);
        dsid = (TextView) rootView.findViewById(R.id.textView6);
        bytes = (TextView) rootView.findViewById(R.id.textView7);
        fmt = (TextView) rootView.findViewById(R.id.textView8);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setFonts();
        searchButtonListener();
        sItems = (Spinner) rootView.findViewById(R.id.spinner);
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                setText(pos);
                //String urltosend = null;
                HttpPost poster;
                String convertPoster = null;
                MainActivity.argslist.add("res," + reportName.getText().toString());
                MainActivity.argslist.add("sid,"+ LoginLogoff.getSid());
                try {
                    //urltosend = apiobj.retrieveQuery(MainActivity.argslist);
                    //Log.d("Variable", urltosend);
                    poster = apiobj.retrieveQuery(MainActivity.argslist);
                    convertPoster = convertStreamToString(poster.getEntity().getContent());
                    Log.d("Variable","HTTP Entiry : " + convertPoster);
                    //InputStream is = httpEntity.getContent();
                    //String htmlContent = convertToString(is);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Picasso.with(getActivity())
                        .load(convertPoster)
                        .resize(500, 500)
                        //.placeholder(R.drawable.sw_placeholder)
                        .centerCrop()
                        .into(imageView);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
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

    public void setText(int versionSelected){
        String selection = versInfo.get(versionSelected);
        String[] infoPieces = selection.split(",");//0=dsid,1=bytes,2=fmt,3=ver
        dsid.setText(infoPieces[0]);
        bytes.setText(infoPieces[1]);
        fmt.setText(infoPieces[2]);
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

    private void searchButtonListener() {
        imageButton2.setOnClickListener(new View.OnClickListener() {
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

    public ArrayList<String> showItems(ArrayList<String> lvers, int sel){
        ArrayList<String> vers = new ArrayList<String>();
        for(String v : lvers){
            String[] pieces = v.split(",");
            vers.add(pieces[sel]);//0=dsid,1=bytes,2=fmt,3=ver
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
                        versInfo = apiobj.getVersionInfo(apiobj.listversionQuery(MainActivity.argslist));
                        spinnerVerArray = showItems(versInfo,3);//get version numbers via 3
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

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public void createSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerVerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems.setAdapter(adapter);
    }



}
