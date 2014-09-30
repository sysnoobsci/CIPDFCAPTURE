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
        setContext(context);
        setGalleryAdapter(ga);
        setGridView(gv);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public GalleryAdapter getGalleryAdapter() {
        return galleryAdapter;
    }

    public void setGalleryAdapter(GalleryAdapter galleryAdapter) {
        this.galleryAdapter = galleryAdapter;
    }

    public GridView getGridView() {
        return gridView;
    }

    public void setGridView(GridView gridView) {
        this.gridView = gridView;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Object doInBackground(Object[] params) {
        getGalleryAdapter().setContext(getContext());
        getGalleryAdapter().setUriArray();
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        getGridView().setAdapter(getGalleryAdapter());
        getGalleryAdapter().notifyDataSetChanged();
        this.cancel(true);
    }


}