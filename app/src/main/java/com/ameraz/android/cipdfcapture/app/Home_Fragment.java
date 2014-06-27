package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 6/3/2014.
 */
public class Home_Fragment extends Fragment {
    static View rootView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater .inflate(R.layout.home_fragment, container, false);
        return rootView;
    }
    public void setText(String date){
        TextView textView = (TextView) getView().findViewById(R.id.textView2);
        textView.setText(", you were last here\n" + date);
    }
}