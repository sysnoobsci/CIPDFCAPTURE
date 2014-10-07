package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMsgTask;
import com.ameraz.android.cipdfcapture.app.SupportingClasses.FileUtility;
import com.ameraz.android.cipdfcapture.app.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by john.williams on 9/15/2014.
 */
public class Camera_Capture_Fragment extends Fragment {

    private Uri imageUri;
    static Context context;
    private String fileName;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Capture_Fragment.context = context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (createFile()) {
            startCamera();
        }
    }

    private boolean createFile() {
        FileUtility fp = new FileUtility();
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            fileName = "sys_image" + System.currentTimeMillis() + ".jpg";
            String incImage = fp.getImageFilePath() + fileName;
            File newImage = new File(incImage);
            try {
                if (!newImage.exists()) {
                    newImage.getParentFile().mkdirs();
                    newImage.createNewFile();
                    imageUri = Uri.fromFile(newImage);
                }
            } catch (IOException e) {
                ToastMsgTask.fileNotWritten(getContext());
                Log.e("File: ", "Could not create file.", e);
                return false;
            }
        } else {
            ToastMsgTask.fileNotWritten(getContext());
            Log.e("File: ", "Storage not mounted.");
            return false;
        }
        return true;
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            Log.d("onActivityResult ", imageUri.toString());
            Fragment fragment = new Image_Upload_Fragment();
            Bundle bundle = new Bundle();
            bundle.putString("fileName", fileName);
            fragment.setArguments(bundle);
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }


}
