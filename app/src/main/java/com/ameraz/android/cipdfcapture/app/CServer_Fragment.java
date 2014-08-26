package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 8/26/2014.
 */
public class CServer_Fragment extends Fragment {

    static View rootView;
    SharedPreferences preferences;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater .inflate(R.layout.csserver_fragment, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setFonts();
        return rootView;
    }

    public void setFonts(){
        TextView txt = (TextView) rootView.findViewById(R.id.textView);
        TextView txt1 = (TextView) rootView.findViewById(R.id.textView2);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        txt.setTypeface(font);
        txt1.setTypeface(font);
    }
}
