package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ameraz.android.cipdfcapture.app.FilePath;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.filebrowser.FileChooser;

/**
 * Created by John Williams.
 *
 * This fragment will create a file browser that will return documents that can be uploaded to Content Integrator.
 *
 */
public class Capture_Docs_Fragment extends Fragment {
    static Context context;


    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Capture_Fragment.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capture_fragment, container, false);
        getFile(rootView);

        initializeViews(rootView);
        setContext(getActivity());
        return rootView;
    }

    public void getFile(View view){

        Intent getMyFile = new Intent(context, FileChooser.class);
        startActivityForResult(getMyFile,0);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See which child activity is calling us back.
        if (requestCode == 0) {
            String filePath = data.getStringExtra("GetFilePath");
            String fileName = data.getStringExtra("GetFileName");
        }
    }

    private void initializeViews(View rootView) {
    }
}



