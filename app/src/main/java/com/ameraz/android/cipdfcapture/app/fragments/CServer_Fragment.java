package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.APIQueries;
import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMessageTask;
import com.ameraz.android.cipdfcapture.app.LogonSession;
import com.ameraz.android.cipdfcapture.app.QueryArguments;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.VersionInfoAdapter;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 8/26/2014.
 */
public class CServer_Fragment extends Fragment {

    static View rootView;
    private EditText reportName;
    TextView txt1;
    TextView txt2;
    private ListView listView;
    private ImageButton imageButton2;
    private WebView webView;
    static Context context;
    String resp;
    APIQueries apiobj = null;
    Spinner sItems;
    ArrayList<String> spinnerVerArrayL = new ArrayList<String>();
    ArrayList<String> tidArrayL = new ArrayList<String>();
    ArrayList<String> versionInfo = new ArrayList<String>();
    ArrayList<String> versInfo = new ArrayList<String>();
    ProgressDialog ringProgressDialog;
    SharedPreferences preferences;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        CServer_Fragment.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.csserver_fragment, container, false);
        setContext(getActivity());
        apiobj = new APIQueries(getContext());
        instantiateViews();
        setFonts();
        ringProgressDialog = new ProgressDialog(getContext());
        setSearchProgressDialog();
        searchButtonListener();
        Log.d("CServer_Fragment.onCreateView()", "start spinnerItemListener() call.");
        spinnerItemListener();
        return rootView;
    }

    public void instantiateViews() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        reportName = (EditText) rootView.findViewById(R.id.editText);
        reportName.setText(preferences.getString("report_preference", null));//set the filed to default report name if there is one
        txt1 = (TextView) rootView.findViewById(R.id.textView);
        txt2 = (TextView) rootView.findViewById(R.id.textView2);
        imageButton2 = (ImageButton) rootView.findViewById(R.id.imageButton2);
        sItems = (Spinner) rootView.findViewById(R.id.spinner);
        webView = (WebView) rootView.findViewById(R.id.webView);
        webView.setWebViewClient(new MyBrowser());
        listView = (ListView) rootView.findViewById(R.id.listView);
    }

    public void createVersInfoAdapter() {
        VersionInfoAdapter listAdapter = new VersionInfoAdapter(getContext(), R.layout.versioninfo_list, versionInfo);
        listView.setAdapter(listAdapter);
    }

    public void setFonts() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");
        txt1.setTypeface(font);
        txt2.setTypeface(font);
    }

    public void setVersionInfo(int versionSelected) {
        versionInfo.clear();//clear the list first to make sure it's clean
        String selection = versInfo.get(versionSelected);
        String[] infoPieces = selection.split(",");//0=dsid,1=cts,2=bytes,3=fmt,4=ver
        versionInfo.add("DSID\t\t\n" + infoPieces[0]);
        versionInfo.add("Created Timestamp\t\t\n" + infoPieces[1]);
        versionInfo.add("Bytes\t\t\n" + infoPieces[2]);
        versionInfo.add("Format\t\t\n" + infoPieces[3]);
        versionInfo.add("Version\t\t\n" + infoPieces[4]);
    }

    private void setSearchProgressDialog() {
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Searching for report ...");
    }

    public void searchButton() throws Exception {
        LogonSession lsobj = new LogonSession(getContext());
        ringProgressDialog.show();
        if (lsobj.tryLogin(getContext())) {
            Log.d("Message", "CI Login successful and ready to search for reports.");
            fillSpinner(apiobj);
            ringProgressDialog.dismiss();
        } else {//if login attempt fails from trying the CI server profile, prompt user to check profile
            ToastMessageTask.noConnectionMessage(getContext());
            ringProgressDialog.dismiss();
        }
    }


    private void spinnerItemListener() {
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, final View view,
                                       final int pos, long id) {
                setVersionInfo(pos);
                new Thread() {
                    public void run() {
                        resp = apiobj.retrieveQuery(tidArrayL.get(pos));//get the right tid
                        Log.d("spinnerItemListener()", "resp value: " + resp);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                open(view);
                                createVersInfoAdapter();//fill the adapter with the report version's info
                            }
                        });

                    }
                }.start();
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
            QueryArguments.addArg("sid," + LogonSession.getSid());
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
            ToastMessageTask tmtask = new ToastMessageTask(getContext(), "Error. Fill out Report Name field.");
            tmtask.execute();
        }
    }

    public void createSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerVerArrayL);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems.setAdapter(adapter);
    }

    public void open(View view) {
        String url = resp;
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//turns off hardware accelerated canvas
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setInitialScale(1);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(url);
        Log.d("CServer_Fragment.open()", "open() called.");
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
