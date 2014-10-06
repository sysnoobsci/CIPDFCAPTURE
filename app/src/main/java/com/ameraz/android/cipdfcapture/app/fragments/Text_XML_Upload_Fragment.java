package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Activity;
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
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.SupportingClasses.UploadProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by John on 9/13/2014.
 */
public class Text_XML_Upload_Fragment extends Fragment {

    private Context context;
    private TextView editText;
    private EditText name;
    private ImageButton uploadButton;
    private ProgressDialog ringProgressDialog;
    private Uri fileUri;

    public void setContext(Activity activity){
        this.context = activity;
    }

    public Context getContext(){
        return this.context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.txt_xml_upload_layout, container, false);
        setContext(getActivity());
        initializeViews(rootView);
        getFileUri();
        setUploadListener();
        return rootView;
    }

    private void setUploadListener() {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    private void initializeViews(View rootView) {
        editText = (TextView)rootView.findViewById(R.id.reportName);
        name = (EditText)rootView.findViewById(R.id.txt_xml_name);
        uploadButton = (ImageButton)rootView.findViewById(R.id.txt_xml_upload_button);
        ringProgressDialog = new ProgressDialog(getContext());
        setUploadProgressDialog();
    }

    private void setUploadProgressDialog() {
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Uploading file ...");
    }

    private void getFileUri() {
        Bundle bundle = this.getArguments();
        String stringUri = bundle.getString("stringUri");
        fileUri = Uri.parse(stringUri);
        readFile(stringUri.substring(stringUri.indexOf('/')+3,stringUri.length()));
        name.setText(stringUri.substring(stringUri.lastIndexOf('/')+1,stringUri.indexOf('.')));
    }

    public void readFile(String stringUri) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(new File(stringUri)));
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        } catch (FileNotFoundException e) {
            Log.d("Error= ", e.toString());
        } catch (IOException e) {
            Log.d("Error= ", e.toString());
        }
        editText.setText(stringBuilder.toString());
    }

    private void upload() {
        ringProgressDialog.show();
        new Thread() {
            public void run() {
                try {
                    UploadProcess upobj = new UploadProcess(getContext(), name, fileUri, ringProgressDialog);
                    upobj.uploadProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
