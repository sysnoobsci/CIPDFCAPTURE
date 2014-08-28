package com.ameraz.android.cipdfcapture.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

/**
 * Created by john.williams on 8/28/2014.
 */
public class Upload extends Activity {
    Uri imageUri;
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

/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            imageUri = data.getData();
            Log.d("Upload Fragment - onActivityResult ", imageUri.toString());
        }
    }
    public Uri getImageUri(){
        return imageUri;
    }*/
}
