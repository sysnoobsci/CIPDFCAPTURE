package com.ameraz.android.cipdfcapture.app.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.ameraz.android.cipdfcapture.app.Adapters.GalleryAdapter;

/**
 * Created by adrian.meraz on 9/9/2014.
 */
public class SetGridTask extends AsyncTask {

    private Context context;
    private GalleryAdapter galleryAdapter;
    private LinearLayout galleryProgress;
    private GridView gridView;

    public SetGridTask(Context context, GalleryAdapter ga, GridView gv) {
        this.context = context;
        this.galleryAdapter = ga;
        this.gridView = gv;
    }
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Object doInBackground(Object[] params) {
        galleryAdapter.setContext(context);
        galleryAdapter.setUriArray();
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        gridView.setAdapter(galleryAdapter);
        galleryAdapter.notifyDataSetChanged();
        this.cancel(true);
    }


}
