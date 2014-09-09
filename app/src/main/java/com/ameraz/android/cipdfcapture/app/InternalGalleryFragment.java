package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

/**
 * Created by john.williams on 8/26/2014.
 */
public class InternalGalleryFragment extends Fragment {

    private Context context;
    static GridView gridView;
    static GalleryAdapter ga;
    static LinearLayout galleryProgress;
    int width;
    int numColumns;
    SharedPreferences pref;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery, container, false);
        setContext(getActivity());
        ga = new GalleryAdapter(getContext());
        galleryProgress = (LinearLayout) rootView.findViewById(R.id.gallery_progress_layout);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        width = rootView.getWidth();
        Log.i("onCreateView()", "Value of width: " + width);
        setColumnWidth();
        SetGridTask sobj = new SetGridTask(getContext(), ga, galleryProgress, gridView);
        sobj.execute();
        gridViewListener();
        return rootView;
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public void setColumnWidth() {

        if (isTablet(getContext())) {
            numColumns = pref.getInt("gallery_preference", 0);
            if (numColumns == 0) {
                numColumns = 10;
            }
        } else {
            numColumns = pref.getInt("gallery_preference", 0);
            if (numColumns == 0) {
                numColumns = 3;
            }
        }
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        width = iDisplayWidth
                / numColumns;
        gridView.setColumnWidth(width);
        ga.setWidth(width);
    }

    void gridViewListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new UploadFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fileName", ga.getNames(position));
                fragment.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }


}
