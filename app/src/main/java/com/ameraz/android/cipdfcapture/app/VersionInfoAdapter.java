package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 9/17/2014.
 */
public class VersionInfoAdapter extends ArrayAdapter {

    private Context context;
    private int id;
    private ArrayList<String> items;

    public VersionInfoAdapter(Context context, int textViewResourceId, ArrayList<String> arrlist) {
        super(context, textViewResourceId, arrlist);
        this.context = context;
        id = textViewResourceId;
        items = arrlist;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        View mView = v;
        if (mView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView text = (TextView) mView.findViewById(R.id.textView);

        if (items.get(position) != null) {
            text.setTextColor(Color.WHITE);
            text.setText(items.get(position));
            text.setBackgroundColor(#0078c9);
            int color = Color.argb(200, 255, 64, 64);
            text.setBackgroundColor(color);
        }
        return mView;
    }
}
