package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.R;
import com.squareup.picasso.Picasso;

/**
 * Created by adrian.meraz on 6/3/2014.
 */
public class Home_Fragment extends Fragment {
    static View rootView;
    SharedPreferences preferences;
    ImageView cloudBackground;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment, container, false);
        preferences = getActivity().getSharedPreferences("timestamp", Context.MODE_PRIVATE);
        cloudBackground = (ImageView) rootView.findViewById(R.id.imageView2);
        setCloudBackground();
        setFonts();
        setText(preferences.getString("pref_date", "n/a"));
        return rootView;
    }

    private void setCloudBackground() {
        Picasso.with(getActivity())
                .load(R.drawable.clouds_parlx_bg1)
                .fit()
                .centerInside()
                .into(cloudBackground);
    }

    public void setText(String date) {
        TextView textView = (TextView) rootView.findViewById(R.id.textView2);
        textView.setText("You were last here\n" + date);
    }

    public void setFonts() {
        TextView txt = (TextView) rootView.findViewById(R.id.textView);
        TextView txt1 = (TextView) rootView.findViewById(R.id.textView2);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        txt.setTypeface(font);
        txt1.setTypeface(font);

    }
}