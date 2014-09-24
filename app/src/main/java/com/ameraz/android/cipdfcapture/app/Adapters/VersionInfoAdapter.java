package com.ameraz.android.cipdfcapture.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.R;

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
            text.setText(items.get(position));
        }
        return mView;
    }
}
