package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by The Bat Cave on 6/2/2014.
 */
public class DownloadPDF_Fragment extends Fragment {

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater .inflate(R.layout.download_fragment, container, false);
        return rootView;
    }
}