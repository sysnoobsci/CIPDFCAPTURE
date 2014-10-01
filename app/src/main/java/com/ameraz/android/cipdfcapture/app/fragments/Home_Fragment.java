package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
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
    ImageView cloudBackground;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        cloudBackground = (ImageView) rootView.findViewById(R.id.imageView2);
        setCloudBackground();
        setFonts();
        return rootView;
    }

    private void setCloudBackground() {
        Picasso.with(getActivity())
                .load(R.drawable.clouds_parlx_bg1)
                .fit()
                .centerInside()
                .into(cloudBackground);
    }

    public void setFonts() {
        TextView txt = (TextView) rootView.findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "GOTHIC.TTF");
        txt.setTypeface(font);
    }
}