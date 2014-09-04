package com.ameraz.android.cipdfcapture.app;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
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

    GridView gridView;
    GalleryAdapter ga = null;
    int width;
    LinearLayout galleryProgress;
    SharedPreferences pref;
    int numColumns = 0;
    private static final int TABLET_COLUMNS = 10;
    private static final int PHONE_COLUMNS = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery, container, false);
        gridView = (GridView)rootView.findViewById(R.id.gridView);
        ga = new GalleryAdapter(getActivity());
        galleryProgress = (LinearLayout)rootView.findViewById(R.id.gallery_progress_layout);
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int width = rootView.getWidth();
        setColumnWidth();
        Log.d("Variable", "value of width: " + String.valueOf(width));
        try {
            new setGrid().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastMessageTask tmtask = new ToastMessageTask(getActivity(),"Image Uri: " + ga.getNames(position));
                tmtask.execute();
                FilePath fp = new FilePath();
                String imageUriString = "file://" + fp.getFilePath() + ga.getNames(position);
                Fragment fragment = new UploadFragment();
                Bundle bundle = new Bundle();
                bundle.putString("inc_string", imageUriString);
                fragment.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public void setColumnWidth(){
        if (isTablet(getActivity())) {
            numColumns = pref.getInt("gallery_preference", 0);
            if(numColumns == 0){
                numColumns = TABLET_COLUMNS;
            }
        }else{
            numColumns = pref.getInt("gallery_preference", 0);
            if(numColumns == 0){
                numColumns = PHONE_COLUMNS;
            }
        }
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        width = iDisplayWidth / numColumns;
        gridView.setColumnWidth(width);
        Log.d("Variable", "Value of width: " + width);
        ga.setWidth(width);
    }

    private class setGrid extends AsyncTask{

        @Override
        protected void onPreExecute(){
            galleryProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            ga.setMaContext(getActivity());
            ga.setUriArray();
            return null;
        }

        @Override
        protected void onPostExecute(Object result){
            gridView.setAdapter(ga);
            ga.notifyDataSetChanged();
            galleryProgress.setVisibility(View.INVISIBLE);
            this.cancel(true);
        }
    }
}
