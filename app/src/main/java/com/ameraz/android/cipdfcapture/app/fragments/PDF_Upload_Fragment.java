package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.ameraz.android.cipdfcapture.app.APIQueries;
import com.ameraz.android.cipdfcapture.app.R;

/**
 * Created by John on 9/13/2014.
 */
public class PDF_Upload_Fragment extends Fragment {

    private static Context context;
    private ProgressDialog ringProgressDialog;
    private WebView pdfViewer;
    //private ImageButton editButton;
    //private ImageButton uploadButton;
    private String name;
    private Uri fileUri;

    public static Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pdf_upload_layout, container, false);
        initializeViews(rootView);
        setContext(getActivity());
        new APIQueries(getContext());
        ringProgressDialog = new ProgressDialog(getContext());
        setUploadProgressDialog();
        uploadListener();
        editListener();
        //gets the data passed to it from InternalGalleryFragment and creates the uri.
        setUriAndPreview();
        return rootView;
    }

    private void editListener() {

    }

    private void initializeViews(View rootView) {
        pdfViewer = (WebView)rootView.findViewById(R.id.pdf_view);
        //editButton = (ImageButton)rootView.findViewById(R.id.pdf_edit_button);
        //uploadButton = (ImageButton)rootView.findViewById(R.id.pdf_upload_button);
    }

    private void uploadListener() {

    }

    private void setUriAndPreview() {
        Bundle bundle = this.getArguments();
        String stringUri = bundle.getString("stringUri");
        Log.d("StringUri= ", stringUri);
        fileUri = Uri.parse(stringUri);
        Log.d("StringUri= ", stringUri);
        name = stringUri.substring(stringUri.lastIndexOf('/') + 1, stringUri.indexOf('.'));
        //description.setText(stringUri.substring(stringUri.lastIndexOf('/') + 1, stringUri.indexOf('.')));
        setImage();
    }

    private void setImage() {
        WebSettings settings = pdfViewer.getSettings();
        settings.setJavaScriptEnabled(true);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN) //required for running javascript on android 4.1 or later
        {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        settings.setBuiltInZoomControls(true);

        pdfViewer.setWebChromeClient(new WebChromeClient());

        //Uri path = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/data/test.pdf");
        pdfViewer.loadUrl("http://docs.google.com/gview?embedded=true&url=" + fileUri);
    }

    private void setUploadProgressDialog() {
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Uploading file ...");
    }

    private void upload(){
        ringProgressDialog.show();
        new Thread() {
            public void run() {
                try {
                    //UploadProcess upobj = new UploadProcess(getContext(), description, fileUri, ringProgressDialog);
                    //upobj.uploadProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
