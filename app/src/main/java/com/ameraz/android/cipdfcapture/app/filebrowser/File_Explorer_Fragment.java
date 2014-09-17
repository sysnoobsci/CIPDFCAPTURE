package com.ameraz.android.cipdfcapture.app.filebrowser;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ameraz.android.cipdfcapture.app.R;

import java.io.File;

/**
 * Created by john.williams on 9/17/2014.
 *
 */
public class File_Explorer_Fragment extends Fragment {

    private File rootDirectory;
    private Context context;

    public void setContext(Activity activity){
        context = activity;
    }

    public Context getContext(){
        return this.context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capture_fragment, container, false);
        setContext(getActivity());
        rootDirectory = new File(Environment.getExternalStorageDirectory().getPath());
        fillDirectory();

        return rootView;
    }

    private void fillDirectory() {

    }
}
