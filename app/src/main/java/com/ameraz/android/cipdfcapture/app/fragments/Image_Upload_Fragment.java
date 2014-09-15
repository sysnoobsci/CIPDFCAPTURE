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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.ameraz.android.cipdfcapture.app.APIQueries;
import com.ameraz.android.cipdfcapture.app.ExtendedClasses.GestureImageView;
import com.ameraz.android.cipdfcapture.app.FilePath;
import com.ameraz.android.cipdfcapture.app.ImageToPDF;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.ToastMessageTask;
import com.ameraz.android.cipdfcapture.app.UploadProcess;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by john.williams on 8/27/2014.
 * Upload_Fragment gets the image path from InternalGalleryFragment and displays it on the screen.
 * It then allows you to upload the document to the Content Server.
 */

public class Image_Upload_Fragment extends Fragment {

    private Uri imageUri;
    private GestureImageView imageView;
    private ImageButton imageButton;
    private EditText description;
    private Spinner uploadOption;
    private ProgressDialog ringProgressDialog;
    static Context context;
    String incFileName;
    private boolean imageUpload;
    FilePath fp;

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
        setSpinnerAdapter();
        setUploadSpinnerListener();
        new APIQueries(getContext());
        ringProgressDialog = new ProgressDialog(getContext());
        setUploadProgressDialog();
        uploadListener();
        //gets the data passed to it from InternalGalleryFragment and creates the uri.
        setUriAndImage();
        return rootView;
    }

    private void setUploadSpinnerListener() {
        uploadOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        imageUpload = true;

                        break;
                    case 1:
                        imageUpload = false;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public String getOutFileName(){
         int end = incFileName.indexOf('.');
        return incFileName.substring(0,end);
    }

    private void setSpinnerAdapter() {
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.upload_type_choice, R.layout.spinner_background);
        typeAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        uploadOption.setAdapter(typeAdapter);
    }

    private void setUriAndImage() {
        Bundle bundle = this.getArguments();
        incFileName = bundle.getString("fileName");
        File existingImage = new File(fp.getImageFilePath() + incFileName);
        imageUri = Uri.fromFile(existingImage);
        //Using the Picasso library, loads the image onto the screen.
        setImage();
        description.setText(getOutFileName());
    }

    private void initializeViews(View rootView) {
        imageView = (GestureImageView) rootView.findViewById(R.id.upload_image_view);
        description = (EditText) rootView.findViewById(R.id.upload_name_input);
        uploadOption = (Spinner)rootView.findViewById(R.id.upload_option_spinner);
        imageButton = (ImageButton)rootView.findViewById(R.id.image_upload_button);
        fp = new FilePath();
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
    private void upload(){
        ringProgressDialog.show();
        new Thread() {
            public void run() {
                try {
                    UploadProcess upobj = new UploadProcess(getContext(), description, imageUri, ringProgressDialog);
                    upobj.uploadProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void uploadListener() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilePath fp = new FilePath();
                if(imageUpload){
                    upload();
                }else{
                    ImageToPDF itp = new ImageToPDF(fp.getImageFilePath()+incFileName,fp.getPDFFilePath()+getOutFileName()+ ".pdf");
                    if(itp.convertImagetoPDF()){
                        imageUri = itp.getImageUri();
                        Log.d(imageUri.toString(), "shrug");
                        upload();
                    }else{
                        ToastMessageTask.pdfConversionFailed(getContext());
                    }
                }
            }
        });
    }

}
