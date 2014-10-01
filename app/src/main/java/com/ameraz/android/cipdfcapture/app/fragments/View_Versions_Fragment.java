package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
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
import com.ameraz.android.cipdfcapture.app.FileUtility;
import com.ameraz.android.cipdfcapture.app.LogonSession;
import com.ameraz.android.cipdfcapture.app.QueryArguments;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.Adapters.VersionInfoAdapter;
import com.ameraz.android.cipdfcapture.app.Version;
import com.ameraz.android.cipdfcapture.app.VersionInfo;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 8/26/2014.
 */
public class View_Versions_Fragment extends Fragment {

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
        View_Versions_Fragment.context = context;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("View_Versions_Fragment","onActivityCreated called.");
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
            ctsArrayList = savedInstanceState.getStringArrayList("ctsArrayList");
            fmtArrayList = savedInstanceState.getStringArrayList("fmtArrayList");
            verArrayList = savedInstanceState.getStringArrayList("verArrayList");
            for(int i=0;i<ctsArrayList.size();i++){
                content.add(new Version(ctsArrayList.get(i), fmtArrayList.get(i), verArrayList.get(i)));
            }
            createListAdapter(content);
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {//save fragment state
        Log.d("View_Versions_Fragment","onSaveInstanceState called.");
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("ctsArrayList", ctsArrayList);
        outState.putStringArrayList("fmtArrayList", fmtArrayList);
        outState.putStringArrayList("verArrayList", verArrayList);
    }

    public void instantiateViews() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        reportName = (EditText) rootView.findViewById(R.id.report_search_input);
        reportName.setText(preferences.getString("report_preference", null));//set the filed to default report name if there is one
        searchButton = (ImageButton) rootView.findViewById(R.id.searchButton);
        listView = (ListView) rootView.findViewById(R.id.version_list);
    }

    public void createListAdapter(ArrayList<Version> content) {
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
                downloadTempFile(pos);
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
        String fullFilePathName = FileUtility.getTempFilePath() + VersionInfo.getDsid()
                + "." + VersionInfo.getFormat().toLowerCase();
        Log.d("View_Versions_Fragment", "topicIdUrl value: " + topicIdUrl);
        new DownloadFileTaskTest(FileUtility.getTempFilePath(), fullFilePathName, VersionInfo.getVersion(), getActivity())
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, topicIdUrl, "IPFragment");//will call IPFragment after completing execution
        Log.d("View_Versions_Fragment", "DownloadFileTask finished executing");
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
                Log.d("FillListViewTask.doInBackground()","Starting iteration through versions");
                for(int i=0;i<ctsArrayList.size();i++){
                    content.add(new Version(ctsArrayList.get(i), fmtArrayList.get(i), verArrayList.get(i)));
                }
            }
            return "true";
        }
        @Override
        protected void onPostExecute(String result) {
            createListAdapter(content);
            ringProgressDialog.dismiss();
        }
    }

}
