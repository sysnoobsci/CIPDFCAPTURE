package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.APIQueries;
import com.ameraz.android.cipdfcapture.app.AsyncTasks.DownloadFileTask;
import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMessageTask;
import com.ameraz.android.cipdfcapture.app.FilePath;
import com.ameraz.android.cipdfcapture.app.LogonSession;
import com.ameraz.android.cipdfcapture.app.QueryArguments;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.TempFileTracker;
import com.ameraz.android.cipdfcapture.app.Adapters.VersionInfoAdapter;
import com.ameraz.android.cipdfcapture.app.ViewLoader;
import com.joanzapata.pdfview.PDFView;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by adrian.meraz on 8/26/2014.
 */
public class DownloadView_Fragment extends Fragment {

    static View rootView;
    private EditText reportName;
    TextView txt1;
    TextView txt2;
    private ListView listView;
    private ImageButton searchButton;
    private ImageButton downloadButton;
    private PDFView pdfViewer;
    private TextView textViewer;
    private ImageView imageView;
    private LinearLayout enlargeImageGroup;
    static Context context;
    String topicIdUrl;
    String versionFormat;
    Boolean first_opened = true;
    int versionNumber;
    String versionDSID;
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

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionDSID() {
        return versionDSID;
    }

    public void setVersionDSID(String versionDSID) {
        this.versionDSID = versionDSID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_downloadview, container, false);
        setContext(getActivity());
        apiobj = new APIQueries(getContext());
        instantiateViews();
        setFonts();
        ringProgressDialog = new ProgressDialog(getContext());
        searchButtonListener();
        enlargeImgButtonListener();
        downloadButtonListener();
        spinnerItemListener();
        first_opened = false;//stops spinner code from prematurely executing - must be set to true after call to spinnerItemListener()
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
        listView = (ListView) rootView.findViewById(R.id.listView);
        pdfViewer = (PDFView) rootView.findViewById(R.id.pdfview);
        textViewer = (TextView) rootView.findViewById(R.id.textView2);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
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
        setVersionDSID(infoPieces[0]);
        versionInfo.add("Capture Timestamp\t\t\n" + infoPieces[1]);
        versionInfo.add("Bytes\t\t\n" + Integer.parseInt(infoPieces[2]) * 1024);
        versionInfo.add("Format\t\t\n" + infoPieces[3]);
        setVersionFormat(infoPieces[3]);//set format of doc
        versionInfo.add("Version\t\t\n" + infoPieces[4]);
        setVersionNumber(Integer.parseInt(infoPieces[4]));//set version number of doc
    }

    public void searchButton() throws Exception {
        LogonSession lsobj = new LogonSession(getContext());
        if (lsobj.tryLogin(getContext())) {
            Log.d("Message", "CI Login successful and ready to search for reports.");
            fillSpinner(apiobj);
        } else {//if login attempt fails from trying the CI server profile, prompt user to check profile
            ToastMessageTask.noConnectionMessage(getContext());
        }
    }

    private void spinnerItemListener() {
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, final View view,
                                       int pos, long id) {
                if(!first_opened){//stops code from prematurely executing
                    setVersionInfo(pos);//gets information about the particular version selected
                    String fullFilePathName = FilePath.getTempFilePath() + getVersionDSID()
                            + "." + getVersionFormat().toLowerCase();
                    new DownloadFileAndLoadView(pos,fullFilePathName).execute();
                }
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
                String vFormat = getVersionFormat();
                String fullFilename = FilePath.chooseDownloadFilePath(vFormat) + getVersionDSID() + vFormat.toLowerCase();
                DownloadFileTask dltask = new DownloadFileTask(topicIdUrl, FilePath.chooseDownloadFilePath(vFormat),
                        fullFilename, getContext());//download response and create a new file
                dltask.execute();
            }
        });
    }

    private void callIP_Fragment() {
        Bundle bundle = new Bundle();
        bundle.putString("retrieve_fileName", TempFileTracker.getTempFilePath(getVersionNumber()));
        bundle.putString("retrieve_fileFormat", getVersionFormat());
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
                            spinnerVerArrayList = APIQueries.getMetadata(listOfReportVersions, "VER");//get version numbers
                            tidArrayList = APIQueries.getMetadata(listOfReportVersions, "TID");//get tids
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

    private class DownloadFileAndLoadView extends AsyncTask<String, Void, String> {

        int pos;
        String fullFilePathName;

        private DownloadFileAndLoadView(int pos, String fullFilePathName) {
            this.pos = pos;
            this.fullFilePathName = fullFilePathName;
        }

        @Override
        protected String doInBackground(String... params) {
            topicIdUrl = apiobj.retrieveQuery(tidArrayList.get(pos));//get the right tid
            Log.d("spinnerItemListener()", "topicIdUrl value: " + topicIdUrl);
            DownloadFileTask dltask = new DownloadFileTask(topicIdUrl,
                    FilePath.getTempFilePath(), fullFilePathName, getContext());//store file in temp file path
            try {
                dltask.execute().get(30000, TimeUnit.MILLISECONDS);//download the file to a temp path, effectively caching it
            } catch (Exception e) {
                e.printStackTrace();
            }
            TempFileTracker.addTempFileToList(fullFilePathName, getVersionNumber());//add temp file and version number to list
            return "success";
        }

        @Override
        protected void onPostExecute(String result) {
            createVersInfoAdapter();//fill the adapter with the report version's info
            try {
                ViewLoader vl = new ViewLoader(getVersionFormat(),pdfViewer,textViewer,imageView,context);
                vl.loadFileIntoView(fullFilePathName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }



}
