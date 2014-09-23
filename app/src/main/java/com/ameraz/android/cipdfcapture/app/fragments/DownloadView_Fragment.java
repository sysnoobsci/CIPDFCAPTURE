package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.APIQueries;
import com.ameraz.android.cipdfcapture.app.AsyncTasks.DownloadFileTask;
import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMessageTask;
import com.ameraz.android.cipdfcapture.app.LogonSession;
import com.ameraz.android.cipdfcapture.app.MyBrowser;
import com.ameraz.android.cipdfcapture.app.QueryArguments;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.VersionInfoAdapter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by adrian.meraz on 8/26/2014.
 */
public class DownloadView_Fragment extends Fragment {

    static View rootView;
    private EditText reportName;
    TextView txt1;
    TextView txt2;
    WebChromeClient webChromeClient;
    private ListView listView;
    private ImageButton searchButton;
    private ImageButton downloadButton;
    private WebView webView;
    private LinearLayout enlargeImageGroup;
    static Context context;
    String resp;
    String versionFormat;
    int position;
    APIQueries apiobj = null;
    Spinner sItems;
    ArrayList<String> spinnerVerArrayList = new ArrayList<String>();
    ArrayList<String> tidArrayList = new ArrayList<String>();
    ArrayList<String> versionInfo = new ArrayList<String>();
    ArrayList<String> listOfReportVersions = new ArrayList<String>();
    ProgressDialog ringProgressDialog;
    SharedPreferences preferences;


    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        DownloadView_Fragment.context = context;
    }

    public String getVersionFormat() {
        return versionFormat;
    }

    public void setVersionFormat(String versionFormat) {
        versionFormat = versionFormat.toUpperCase();//make sure it's uppercase first
        Log.d("setVersionFormat()", "versionFormat value: " + getVersionFormat());
        this.versionFormat = versionFormat;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.downloadview_fragment, container, false);
        setContext(getActivity());
        apiobj = new APIQueries(getContext());
        instantiateViews();
        setFonts();
        ringProgressDialog = new ProgressDialog(getContext());
        setSearchProgressDialog();
        searchButtonListener();
        enlargeImgButtonListener();
        downloadButtonListener();
        spinnerItemListener();
        return rootView;
    }

    public void instantiateViews() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        reportName = (EditText) rootView.findViewById(R.id.editText);
        reportName.setText(preferences.getString("report_preference", null));//set the filed to default report name if there is one
        txt1 = (TextView) rootView.findViewById(R.id.textView);
        txt2 = (TextView) rootView.findViewById(R.id.textView2);
        searchButton = (ImageButton) rootView.findViewById(R.id.searchButton);
        downloadButton = (ImageButton) rootView.findViewById(R.id.download_and_save);
        enlargeImageGroup = (LinearLayout) rootView.findViewById(R.id.grouped_Layout);
        sItems = (Spinner) rootView.findViewById(R.id.spinner);
        webView = (WebView) rootView.findViewById(R.id.webView);

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
        String selection = listOfReportVersions.get(versionSelected);
        String[] infoPieces = selection.split(",");//0=dsid,1=cts,2=bytes,3=fmt,4=ver
        versionInfo.add("DSID\t\t\n" + infoPieces[0]);
        versionInfo.add("Created Timestamp\t\t\n" + infoPieces[1]);
        versionInfo.add("Bytes\t\t\n" + infoPieces[2]);
        versionInfo.add("Format\t\t\n" + infoPieces[3]);
        setVersionFormat(infoPieces[3]);
        versionInfo.add("Version\t\t\n" + infoPieces[4]);
    }

    private void setSearchProgressDialog() {
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Searching for report ...");
    }

    private void setDownloadingViewDialog() {
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Downloading View ...");
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
                        position = pos;//store the position of the item clicked
                        resp = apiobj.retrieveQuery(tidArrayList.get(pos));//get the right tid
                        Log.d("spinnerItemListener()", "resp value: " + resp);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    open(webView);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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

    private void searchButtonListener() {//searches for the report and displays the versions
        searchButton.setOnClickListener(new View.OnClickListener() {
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

    private void enlargeImgButtonListener() {//enlarges the image that appears in the WebView
        enlargeImageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("enlargeImgButtonListener()", "enlargeImgButtonListener() clicked");
                try {
                    callIP_Fragment();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void downloadButtonListener() {//need to flesh out with download code
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("downloadButtonListener()", "downloadButtonListener() clicked");
                /*Intent a = new Intent(getActivity(), PDFViewActivity.class);
                startActivity(a);*/
                DownloadFileTask dltask = new DownloadFileTask(resp);//download response and create a new file
                dltask.execute();
            }
        });
    }

    private void callIP_Fragment() {
        Bundle bundle = new Bundle();
        bundle.putString("retrieve_url", resp);
        Fragment fragment = new Image_Preview_Fragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void fillSpinner(final APIQueries apiobj) {
        if (!reportName.getText().toString().isEmpty()) {
            new Thread() {
                public void run() {
                    try {
                        QueryArguments.addArg("res," + reportName.getText().toString().toUpperCase());
                        QueryArguments.addArg("sid," + LogonSession.getSid());
                        listOfReportVersions = apiobj.getVersionInfo(apiobj.listversionQuery(QueryArguments.getArgslist()));
                        if (listOfReportVersions != null) {
                            spinnerVerArrayList = APIQueries.getMetadata(listOfReportVersions, "VER");//get version numbers via 4
                            tidArrayList = APIQueries.getMetadata(listOfReportVersions, "TID");//get tids via 5
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerVerArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems.setAdapter(adapter);
    }

    public void open(final WebView webView) throws IOException {
        setWebViewSettings(webView);
        webView.loadUrl(resp);
    }

    public void setWebViewSettings(WebView webView) {
        webView.setWebViewClient(new MyBrowser(ringProgressDialog));
        //webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//turns off hardware accelerated canvas
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setInitialScale(1);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);//turn caching mode on
    }

    @Override
    public void onDestroy() {
        // Clear the cache (this clears the WebViews cache for the entire application)
        webView.clearCache(true);
        super.onDestroy();
    }

}
