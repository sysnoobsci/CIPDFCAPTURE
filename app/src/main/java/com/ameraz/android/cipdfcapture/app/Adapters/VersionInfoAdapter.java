package com.ameraz.android.cipdfcapture.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.Version;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 9/17/2014.
 */
public class VersionInfoAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Version> data = null;

    public VersionInfoAdapter(Context context, ArrayList<Version> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        View mView = v;
        if (mView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(R.layout.versioninfo_list, null);
        }

        TextView type = (TextView) mView.findViewById(R.id.download_type);
        TextView createdDate = (TextView)mView.findViewById(R.id.download_createdOn);
        TextView version = (TextView)mView.findViewById(R.id.download_version);

        Version versions = data.get(position);
        type.setText(versions.getType());
        createdDate.setText(versions.getDate());
        version.setText(versions.getVersionNum());



        return mView;
    }
}
