package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ameraz.android.cipdfcapture.app.APIQueries;
import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMessageTask;
import com.ameraz.android.cipdfcapture.app.AsyncTasks.DownloadFileTaskTest;
import com.ameraz.android.cipdfcapture.app.FilePath;
import com.ameraz.android.cipdfcapture.app.LogonSession;
import com.ameraz.android.cipdfcapture.app.QueryArguments;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.TempFileTracker;
import com.ameraz.android.cipdfcapture.app.Adapters.VersionInfoAdapter;
import com.ameraz.android.cipdfcapture.app.Version;
import com.ameraz.android.cipdfcapture.app.VersionInfo;
import com.ameraz.android.cipdfcapture.app.filebrowser.Item;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by adrian.meraz on 8/26/2014.
 */
public class DownloadView_Fragment extends Fragment {

    static View rootView;
    private EditText reportName;
    private ListView listView;
    private ImageButton searchButton;
    static Context context;
    String topicIdUrl;
    ArrayList<String> ctsArrayList = new ArrayList<String>();
    ArrayList<String> fmtArrayList = new ArrayList<String>();
    ArrayList<String> verArrayList = new ArrayList<String>();
    ArrayList<String> tidArrayList = new ArrayList<String>();
    ArrayList<String> versionLimitedInfoList = new ArrayList<String>();
    ArrayList<String> listOfReportVersions = new ArrayList<String>();
    ProgressDialog ringProgressDialog;
    SharedPreferences preferences;

    private ArrayList<Version> content;

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
        ringProgressDialog = new ProgressDialog(getContext());
        ListViewListener();
        searchButtonListener();
        return rootView;
    }

    public void instantiateViews() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        reportName = (EditText) rootView.findViewById(R.id.report_search_input);
        reportName.setText(preferences.getString("report_preference", null));//set the filed to default report name if there is one
        searchButton = (ImageButton) rootView.findViewById(R.id.searchButton);
        listView = (ListView) rootView.findViewById(R.id.version_list);
    }

    public void createListAdapter() {
        VersionInfoAdapter listAdapter = new VersionInfoAdapter(getContext(), content);
        listView.setAdapter(listAdapter);
    }

    public void setListingVersionsProgressDialog(){
        ringProgressDialog.setTitle("Listing Versions");
        ringProgressDialog.setMessage("Listing all versions of report " + reportName.getText());
    }

    private void ListViewListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                ToastMessageTask tmtask = new ToastMessageTask(getContext(),"ListViewListener() click detected");
                tmtask.execute();
                downloadTempFile(pos);
                //callIP_Fragment();
            }
        });
    }

    public void setVersionInfo(int versionSelected) {//sets up the information to be placed in listView
        String selection = listOfReportVersions.get(versionSelected);
        String[] infoPieces = selection.split(",");//0=dsid,1=cts,2=bytes,3=fmt,4=ver
        VersionInfo.setDsid(infoPieces[0]);
        VersionInfo.setCapture_timestamp(infoPieces[1]);
        VersionInfo.setBytes(Integer.parseInt(infoPieces[2]));
        VersionInfo.setFormat(infoPieces[3]);
        VersionInfo.setVersion(Integer.parseInt(infoPieces[4]));

    }

    private void downloadTempFile(int pos){
        APIQueries apiobj = new APIQueries(getContext());
        topicIdUrl = apiobj.retrieveQuery(tidArrayList.get(pos));//get the right tid
        setVersionInfo(pos);
        String fullFilePathName = FilePath.getTempFilePath() + VersionInfo.getDsid()
                + "." + VersionInfo.getFormat().toLowerCase();
        Log.d("DownloadFileAndLoadView", "topicIdUrl value: " + topicIdUrl);
        new DownloadFileTaskTest(FilePath.getTempFilePath(), fullFilePathName,getContext())
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, topicIdUrl);
        Log.d("DownloadFileAndLoadView", "DownloadFileTask finished executing");
        TempFileTracker.addTempFileToList(fullFilePathName, VersionInfo.getVersion());//add temp file and version number to list
    }

    public void searchButton() throws Exception {
        LogonSession lsobj = new LogonSession(getContext());
        if (lsobj.tryLogin(getContext())) {
            Log.d("Message", "CI Login successful and ready to search for reports.");
            new FillListViewTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);//don't serialize asynctask
        } else {//if login attempt fails from trying the CI server profile, prompt user to check profile
            ToastMessageTask.noConnectionMessage(getContext());
        }
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

    private class FillListViewTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            versionLimitedInfoList.clear();//clear the list first to make sure it's clean
            setListingVersionsProgressDialog();
            ringProgressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            content = null;
            content = new ArrayList<Version>();
            Log.d("FillListViewTask", "FillListViewTask asynctask starting execution.");
            APIQueries apiobj = new APIQueries(getContext());
            try {
                QueryArguments.addArg("res," + reportName.getText().toString().toUpperCase());
                QueryArguments.addArg("sid," + LogonSession.getSid());
                StringBuffer sbuffer = new StringBuffer();
                sbuffer.append(apiobj.listversionQuery(QueryArguments.getArgslist()));
                Log.d("FillListViewTask.doInBackground()","getting version info for the list");
                listOfReportVersions = apiobj.getVersionInfo(sbuffer.toString());//pass in xml response from earlier API call
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (listOfReportVersions != null) {
                ctsArrayList = APIQueries.getMetadata(listOfReportVersions, "CTS");//get capture timestamp
                fmtArrayList = APIQueries.getMetadata(listOfReportVersions, "FMT");//get format
                verArrayList = APIQueries.getMetadata(listOfReportVersions, "VER");//get version numbers
                tidArrayList = APIQueries.getMetadata(listOfReportVersions, "TID");//get version numbers
                StringBuilder combinedInfo = new StringBuilder();
                Log.d("FillListViewTask.doInBackground()","Starting iteration through versions");
                for(int i=0;i<ctsArrayList.size();i++){
                    content.add(new Version(ctsArrayList.get(i), fmtArrayList.get(i), verArrayList.get(i)));
/*                    combinedInfo.append(ctsArrayList.get(i) + "\t\t")
                                .append(fmtArrayList.get(i) + "\t\t")
                                .append(verArrayList.get(i) + "\t\t");
                    versionLimitedInfoList.add(combinedInfo.toString());
                    combinedInfo.setLength(0);*///clear out StringBuilder each iteration
                }
            }
            return "true";
        }
        @Override
        protected void onPostExecute(String result) {
            createListAdapter();
            ringProgressDialog.dismiss();
        }
    }

}
