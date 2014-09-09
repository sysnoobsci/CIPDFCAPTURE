package com.ameraz.android.cipdfcapture.app;

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
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian.meraz on 8/26/2014.
 */
public class CServer_Fragment extends Fragment {

    static View rootView;
    SharedPreferences preferences;
    private EditText reportName;
    private TextView dsid;
    private TextView cts;
    private TextView bytes;
    private TextView fmt;
    private ImageView imageView;
    private ImageButton imageButton2;
    private WebView webView;
    APIQueries apiobj = null;
    Spinner sItems;
    List<String> spinnerVerArrayL = new ArrayList<String>();
    List<String> tidArrayL = new ArrayList<String>();
    ArrayList<String> versInfo = new ArrayList<String>();
    ProgressDialog ringProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.csserver_fragment, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        apiobj = new APIQueries(getActivity());
        instantiateViews();
        setFonts();
        searchButtonListener();
        spinnerItemListener();
        return rootView;
    }

    public void instantiateViews() {
        reportName = (EditText) rootView.findViewById(R.id.editText);
        imageButton2 = (ImageButton) rootView.findViewById(R.id.imageButton2);
        dsid = (TextView) rootView.findViewById(R.id.textView6);
        cts = (TextView) rootView.findViewById(R.id.textView10);
        bytes = (TextView) rootView.findViewById(R.id.textView7);
        fmt = (TextView) rootView.findViewById(R.id.textView8);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        sItems = (Spinner) rootView.findViewById(R.id.spinner);
    }

    public void setFonts() {
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

    public void setText(int versionSelected) {
        String selection = versInfo.get(versionSelected);
        String[] infoPieces = selection.split(",");//0=dsid,1=cts,2=bytes,3=fmt,4=ver
        dsid.setText(infoPieces[0]);
        cts.setText(infoPieces[1]);
        bytes.setText(infoPieces[2]);
        fmt.setText(infoPieces[3]);
    }

    public void searchButton() throws Exception {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        loginlogoff liloobj = new loginlogoff(getActivity());
        ringProgressDialog = ProgressDialog.show(getActivity(), "Performing Action ...",
                "Searching for report ...", true);
        QueryArguments.addArg(loginlogoff.getSid());
        if (apiobj.pingQuery(QueryArguments.getArgslist())) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Login successful and ready to search for reports.");
            fillSpinner(apiobj);
            ringProgressDialog.dismiss();
        } else {//if ping fails, selected ci profile will be used to log back in
            Log.d("Message", "Ping to CI server indicated no login session.");
            if (liloobj.tryLogin(getActivity())) {
                Log.d("Message", "CI Login successful and ready to search for reports.");
                fillSpinner(apiobj);
                ringProgressDialog.dismiss();
            } else {//if login attempt fails from trying the CI server profile, prompt user to check profile
                ToastMessageTask.noConnectionMessage(getActivity());
                ringProgressDialog.dismiss();
            }
        }
    }

    private void spinnerItemListener() {
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                setText(pos);
                String resp = apiobj.retrieveQuery(tidArrayL.get(pos));//get the right tid
                Picasso.with(getActivity()).setDebugging(true);
                Picasso.with(getActivity())
                        .load(resp)
                        .resize(500, 500)
                                //.placeholder(R.drawable.sw_placeholder)
                        .centerCrop()
                        .into(imageView);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
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

    public void fillSpinner(final APIQueries apiobj) {
        if (!reportName.getText().toString().isEmpty()) {
            QueryArguments.addArg("res," + reportName.getText().toString().toUpperCase());
            QueryArguments.addArg("sid," + loginlogoff.getSid());
            new Thread() {
                public void run() {
                    try {
                        versInfo = apiobj.getVersionInfo(apiobj.listversionQuery(QueryArguments.getArgslist()));
                        if (versInfo != null) {
                            spinnerVerArrayL = APIQueries.showItems(versInfo, 4);//get version numbers via 4
                            tidArrayL = APIQueries.showItems(versInfo, 5);//get tids via 5
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createSpinner();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ringProgressDialog.dismiss();
                }

            }.start();
        } else {
            ringProgressDialog.dismiss();
            ToastMessageTask tmtask = new ToastMessageTask(getActivity(), "Error. Fill out Report Name field.");
            tmtask.execute();
        }
    }

    public void createSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerVerArrayL);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems.setAdapter(adapter);
    }


}
