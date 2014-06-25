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
    public static void setText(ArrayList <String> dateTime){
        TextView textView = (TextView) rootView.findViewById(R.id.textView2);
        TextView textView2 = (TextView) rootView.findViewById(R.id.textView3);
        String month = dateTime.get(2).substring(4,6);
        String day = dateTime.get(2).substring(6,8);
        String year = dateTime.get(2).substring(0,4);
        String hour = dateTime.get(2).substring(8,10);
        String minute = dateTime.get(2).substring(10,12);
        String second = dateTime.get(2).substring(12,14);
        textView.setText(dateTime.get(0) + ", you were last here\n" + month + "/" + day + "/" + year
                    + "  " + hour + ":" + minute + ":" + second + ".");
        textView2.setText(dateTime.get(0) + " - " + "(" + dateTime.get(3) + ")");
    }
}