package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ameraz.android.cipdfcapture.app.AsyncTasks.UploadFileTask;
import com.ameraz.android.cipdfcapture.app.SupportingClasses.APIQueries;
import com.ameraz.android.cipdfcapture.app.ExtendedClasses.GestureImageView;
import com.ameraz.android.cipdfcapture.app.SupportingClasses.FileUtility;
import com.ameraz.android.cipdfcapture.app.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by john.williams on 8/27/2014.
 * Upload_Fragment gets the image path from InternalGalleryFragment and displays it on the screen.
 * It then allows you to upload the document to the Content Server.
 */

public class Image_Upload_Fragment extends Fragment {

    private Uri fileUri;
    private GestureImageView imageView;
    private ImageButton imageButton;
    private EditText name;
    static Context context;
    private FileUtility fp;

    public static Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_upload_layout, container, false);
        initializeViews(rootView);
        setContext(getActivity());
        new APIQueries(getContext());
        uploadListener();
        //gets the data passed to it from InternalGalleryFragment and creates the uri.
        setUriAndImage();
        return rootView;
    }

    private void setUriAndImage() {
        Bundle bundle = this.getArguments();
        if (bundle.getString("fileName") != null) {
            String fileName = bundle.getString("fileName");
            File existingImage = new File(fp.getImageFilePath() + fileName);
            fileUri = Uri.fromFile(existingImage);
            name.setText(fileName);
        } else if (bundle.getString("stringUri") != null) {
            String stringUri = bundle.getString("stringUri");
            fileUri = Uri.parse(stringUri);
            name.setText(stringUri.substring(stringUri.lastIndexOf('/') + 1, stringUri.indexOf('.')));
            Log.d("setUriAndImage()", "value of uri:" + fileUri.toString());
        }
        //Using the Picasso library, loads the image onto the screen.
        setImage();
    }

    private void initializeViews(View rootView) {
        imageView = (GestureImageView) rootView.findViewById(R.id.upload_image_view);
        name = (EditText) rootView.findViewById(R.id.upload_name_input);
        imageButton = (ImageButton) rootView.findViewById(R.id.image_upload_button);
        fp = new FileUtility();
    }

    private void setImage() {
        Picasso.with(getContext())
                .load(fileUri)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    private void uploadListener() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadFileTask(getContext(), name, fileUri)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

}
