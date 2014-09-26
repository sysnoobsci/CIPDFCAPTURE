package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ameraz.android.cipdfcapture.app.R;

/**
 * Created by john.williams on 9/26/2014.
 */
public class File_Downloader extends Fragment {

    static View rootView;
    private Context context;
    private ListView list;
    private EditText reportName;
    private ImageButton searchButton;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.file_downloader, container, false);
        setContext(getActivity());
        instantiateViews();
        return rootView;
    }

    private void instantiateViews() {

    }
}
