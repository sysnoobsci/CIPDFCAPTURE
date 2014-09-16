package com.ameraz.android.cipdfcapture.app.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.ameraz.android.cipdfcapture.app.GalleryAdapter;

/**
 * Created by adrian.meraz on 9/9/2014.
 */
public class SetGridTask extends AsyncTask {

    private Context context;
    private GalleryAdapter galleryAdapter;
    private LinearLayout galleryProgress;
    private GridView gridView;

    public SetGridTask(Context context, GalleryAdapter ga, LinearLayout gp, GridView gv) {
        setContext(context);
        setGalleryAdapter(ga);
        setGalleryProgress(gp);
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

    public LinearLayout getGalleryProgress() {
        return galleryProgress;
    }

    public void setGalleryProgress(LinearLayout galleryProgress) {
        this.galleryProgress = galleryProgress;
    }

    public GridView getGridView() {
        return gridView;
    }

    public void setGridView(GridView gridView) {
        this.gridView = gridView;
    }

    @Override
    protected void onPreExecute() {
        galleryProgress.setVisibility(View.VISIBLE);
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
        getGalleryProgress().setVisibility(View.INVISIBLE);
        this.cancel(true);
    }


}
