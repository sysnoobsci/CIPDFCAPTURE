package com.ameraz.android.cipdfcapture.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;


/**
 * Created by john.williams on 8/28/2014.
 */
public class Upload extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_container);
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            FragmentManager fragmentManager = getFragmentManager();
            UploadFragment fragment = new UploadFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.upload_container, fragment)
                    .commit();
        }
    }//end of oncreate

}
