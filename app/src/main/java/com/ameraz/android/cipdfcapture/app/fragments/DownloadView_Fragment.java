package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ameraz.android.cipdfcapture.app.DownloadFileTaskTest;
import com.ameraz.android.cipdfcapture.app.FilePath;
import com.ameraz.android.cipdfcapture.app.LogonSession;
import com.ameraz.android.cipdfcapture.app.QueryArguments;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.TempFileTracker;
import com.ameraz.android.cipdfcapture.app.Adapters.VersionInfoAdapter;
import com.ameraz.android.cipdfcapture.app.VersionInfo;
import com.ameraz.android.cipdfcapture.app.ViewLoader;
import com.joanzapata.pdfview.PDFView;

import junit.runner.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by adrian.meraz on 8/26/2014.
 */
public class DownloadView_Fragment extends Fragment {

    static View rootView;
    private EditText reportName;
    private ListView listView;
    private ImageButton searchButton;
    private ImageButton downloadButton;
    private PDFView pdfViewer;
    private TextView textViewer;
    private TextView txt1;
    private TextView txt2;
    private ImageView imageView;
    private LinearLayout enlargeImageGroup;
    static Context context;
    String topicIdUrl;
    Spinner sItems;
    ArrayList<String> spinnerVerArrayList = new ArrayList<String>();
    ArrayList<String> tidArrayList = new ArrayList<String>();
    ArrayList<String> versionInfoList = new ArrayList<String>();
    ArrayList<String> listOfReportVersions = new ArrayList<String>();
    ProgressDialog ringProgressDialog;
    SharedPreferences preferences;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        DownloadView_Fragment.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_downloadview, container, false);
        setContext(getActivity());
        instantiateViews();
        setFonts();
        ringProgressDialog = new ProgressDialog(getContext());
        searchButtonListener();
        enlargeImgButtonListener();
        downloadButtonListener();
        spinnerItemListener();
        return rootView;
    }

    public void instantiateViews() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        reportName = (EditText) rootView.findViewById(R.id.reportName);
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
        VersionInfoAdapter listAdapter = new VersionInfoAdapter(getContext(), R.layout.versioninfo_list, versionInfoList);
        listView.setAdapter(listAdapter);
    }

    public void createSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerVerArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems.setAdapter(adapter);
    }

    public void setFonts() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");
        txt1.setTypeface(font);
        txt2.setTypeface(font);
    }

    public void setVersionInfo(int versionSelected) {//sets up the information to be placed in listView
        versionInfoList.clear();//clear the list first to make sure it's clean
        String selection = listOfReportVersions.get(versionSelected);
        String[] infoPieces = selection.split(",");//0=dsid,1=cts,2=bytes,3=fmt,4=ver
        versionInfoList.add("DSID\t\t\n" + infoPieces[0]);
        versionInfoList.add("Capture Timestamp\t\t\n" + infoPieces[1]);
        versionInfoList.add("Bytes\t\t\n" + Integer.parseInt(infoPieces[2]) * 1024);
        versionInfoList.add("Format\t\t\n" + infoPieces[3]);
        versionInfoList.add("Version\t\t\n" + infoPieces[4]);
        VersionInfo.setDsid(infoPieces[0]);
        VersionInfo.setCapture_timestamp(infoPieces[1]);
        VersionInfo.setBytes(Integer.parseInt(infoPieces[2]));
        VersionInfo.setFormat(infoPieces[3]);
        VersionInfo.setVersion(Integer.parseInt(infoPieces[4]));

    }

    public void setListingVersionsProgressDialog(){
        ringProgressDialog.setTitle("Listing Versions");
        ringProgressDialog.setMessage("Listing all versions of report " + reportName.getText());
    }

    public void searchButton() throws Exception {
        LogonSession lsobj = new LogonSession(getContext());
        if (lsobj.tryLogin(getContext())) {
            Log.d("Message", "CI Login successful and ready to search for reports.");
            APIQueries apiobj = new APIQueries(getContext());
            fillSpinner(apiobj);
        } else {//if login attempt fails from trying the CI server profile, prompt user to check profile
            ToastMessageTask.noConnectionMessage(getContext());
        }
    }

    private void spinnerItemListener() {
        sItems.setTag(R.id.position, 0);
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, final View view,
                                       int pos, long id) {
                if((Integer) sItems.getTag(R.id.position) != pos){//stops code from prematurely executing
                    setVersionInfo(pos);//gets information about the particular version selected
                    String fullFilePathName = FilePath.getTempFilePath() + VersionInfo.getDsid()
                            + "." + VersionInfo.getFormat().toLowerCase();
                    //DownloadFileAndLoadView dfLoadView = new DownloadFileAndLoadView(pos,fullFilePathName);
                    APIQueries apiobj = new APIQueries(getContext());
                    topicIdUrl = apiobj.retrieveQuery(tidArrayList.get(pos));//get the right tid
                    Log.d("DownloadFileAndLoadView", "topicIdUrl value: " + topicIdUrl);
                    try {
                        new DownloadFileTaskTest(FilePath.getTempFilePath(), fullFilePathName,getContext())
                                .execute(topicIdUrl).get(20000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("DownloadFileAndLoadView", "DownloadFileTask finished executing");
                    TempFileTracker.addTempFileToList(fullFilePathName, VersionInfo.getVersion());//add temp file and version number to list
                    createVersInfoAdapter();//fill the adapter with the report version's info
                    try {
                        ViewLoader vl = new ViewLoader(VersionInfo.getFormat(),pdfViewer,textViewer,imageView,context);
                        vl.loadFileIntoView(fullFilePathName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //dfLoadView.execute();
                }
                sItems.setTag(R.id.position, pos);
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
                String vFormat = VersionInfo.getFormat();
                String fullFilename = FilePath.chooseDownloadFilePath(vFormat) + VersionInfo.getDsid() + vFormat.toLowerCase();
                new DownloadFileTask(FilePath.chooseDownloadFilePath(vFormat),
                        fullFilename, getContext()).execute(topicIdUrl);//download response and create a new file
            }
        });
    }

    private void callIP_Fragment() {
        Bundle bundle = new Bundle();
        bundle.putString("retrieve_fileName", TempFileTracker.getTempFilePath(VersionInfo.getVersion()));
        bundle.putString("retrieve_fileFormat", VersionInfo.getFormat());
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
            setListingVersionsProgressDialog();
            ringProgressDialog.show();
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
        }
        else {
            ringProgressDialog.dismiss();
            ToastMessageTask.fillFieldMessage(getContext());
        }
    }



    private class DownloadFileAndLoadView extends AsyncTask<String, Void, String> {

        int pos;
        String fullFilePathName;

        private DownloadFileAndLoadView(int pos, String fullFilePathName) {
            this.pos = pos;
            this.fullFilePathName = fullFilePathName;
        }

        @Override
        protected void onPreExecute() {
            Log.d("DownloadFileAndLoadView", "DownloadFileAndLoadView asynctask starting execution.");
        }

        @Override
        protected String doInBackground(String... params) {
            APIQueries apiobj = new APIQueries(getContext());
            topicIdUrl = apiobj.retrieveQuery(tidArrayList.get(pos));//get the right tid
            Log.d("DownloadFileAndLoadView", "topicIdUrl value: " + topicIdUrl);
            try {
                //new DownloadFileTask(FilePath.getTempFilePath(), fullFilePathName,
                //        getContext()).execute(topicIdUrl).get(20000, TimeUnit.MILLISECONDS);//store file in temp file path
                //new DownloadFileTask(topicIdUrl,FilePath.getTempFilePath(), fullFilePathName,
                //       getContext()).execute();
                new DownloadFileTaskTest(FilePath.getTempFilePath(), fullFilePathName,getContext())
                        .execute(topicIdUrl).get(20000, TimeUnit.MILLISECONDS);
                //new DownloadFileTaskTest(FilePath.getTempFilePath(), fullFilePathName,getContext())
                //       .execute(topicIdUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("DownloadFileAndLoadView", "DownloadFileTask finished executing");
            TempFileTracker.addTempFileToList(fullFilePathName, VersionInfo.getVersion());//add temp file and version number to list
            return "success";
        }

        @Override
        protected void onPostExecute(String result) {
            createVersInfoAdapter();//fill the adapter with the report version's info
            try {
                ViewLoader vl = new ViewLoader(VersionInfo.getFormat(),pdfViewer,textViewer,imageView,context);
                vl.loadFileIntoView(fullFilePathName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
