package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ameraz.android.cipdfcapture.app.APIQueries;
import com.ameraz.android.cipdfcapture.app.ExtendedClasses.GestureImageView;
import com.ameraz.android.cipdfcapture.app.FilePath;
import com.ameraz.android.cipdfcapture.app.LogonSession;
import com.ameraz.android.cipdfcapture.app.QueryArguments;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.ToastMessageTask;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by john.williams on 8/27/2014.
 * Upload_Fragment gets the image path from InternalGalleryFragment and displays it on the screen.
 * It then allows you to upload the document to the Content Server.
 */

public class Upload_Fragment extends Fragment {

    private Uri imageUri;
    private GestureImageView imageView;
    private ImageButton imageButton;
    private EditText description;
    APIQueries apiobj = null;
    ProgressDialog ringProgressDialog;
    static Context context;

    final static private String tplid1 = "create.phonecapture";//time in milliseconds for createtopic attempt to timeout

    public static Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_single_layout, container, false);
        initializeViews(rootView);
        setContext(getActivity());
        apiobj = new APIQueries(getContext());
        ringProgressDialog = new ProgressDialog(getContext());
        setUploadProgressDialog();
        gridImageListener();
        //gets the data passed to it from InternalGalleryFragment and creates the uri.
        setUriAndImage();
        return rootView;
    }

    private void setUriAndImage() {
        Bundle bundle = this.getArguments();
        String fileName = bundle.getString("fileName");
        FilePath fp = new FilePath();
        File existingImage = new File(fp.getFilePath() + fileName);
        imageUri = Uri.fromFile(existingImage);
        //Using the Picasso library, loads the image onto the screen.
        setImage();
        description.setText(fileName);
    }

    private void initializeViews(View rootView) {
        imageView = (GestureImageView) rootView.findViewById(R.id.gallery_single_image_view);
        imageButton = (ImageButton) rootView.findViewById(R.id.gallery_single_image_upload_button);
        description = (EditText) rootView.findViewById(R.id.gallery_single_image_description_text);
    }

    private void setUploadProgressDialog() {
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Uploading file ...");
    }

    private void setImage() {
        Picasso.with(getContext())
                .load(imageUri)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    private void gridImageListener() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringProgressDialog.show();
                new Thread() {
                    public void run() {
                        try {
                            uploadButton();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    public void uploadButton() throws Exception {
        LogonSession lsobj = new LogonSession(getContext());
        if (uploadCheck(description, imageUri)) {
            Log.d("uploadButton()", "getContext() value: " + getContext());
            Boolean logonStatus = lsobj.tryLogin(getContext());
            if (logonStatus) {
                Log.d("Message", "CI Login successful and ready to upload file.");
                createTopic();//create a topic instance object
            } else {
                Log.d("Message", "CI Login failed. Unable to load file.");
            }
        }
        ringProgressDialog.dismiss();
    }


    void createTopic() {
        QueryArguments.addArg("tplid," + tplid1);
        QueryArguments.addArg("name," + description.getText().toString());
        QueryArguments.addArg("detail,y");
        QueryArguments.addArg("sid," + LogonSession.getSid());
        QueryArguments.addArg(imageUri);
        try {
            apiobj.createtopicQuery(QueryArguments.getArgslist());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ringProgressDialog.dismiss();
    }

    Boolean uploadCheck(EditText description, Uri imageUri) {
        if (imageUri == null) {//checks if image taken yet
            ToastMessageTask.picNotTaken(getContext());
            return false;
        }
        if (String.valueOf(description.getText()).isEmpty()) {
            ToastMessageTask.fillFieldMessage(getContext());
            return false;
        }
        return true;//if pic was taken and there is a non-empty description, return true
    }
}
