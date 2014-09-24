package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.APIQueries;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.UploadProcess;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;

import static java.lang.String.format;

/**
 * Created by John Williams
 * This fragment receives a fileUri and shows a preview of the PDF to be uploaded.
 * It then provides the process for uploading the PDF.
 */
public class PDF_Upload_Fragment extends Fragment implements OnPageChangeListener {

    private static Context context;
    private ProgressDialog ringProgressDialog;
    private PDFView pdfView;
    private ImageButton editButton;
    private EditText nameView;
    private ImageButton uploadButton;
    private TextView pageCountView;
    private String name;
    private Uri fileUri;
    private File pdf;
    private int pageNumber;
    private boolean isVisible;

    public static Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
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
        setUriAndPreview();
        return rootView;
    }

    private void editListener() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewVisible()){
                    nameView.setVisibility(View.INVISIBLE);
                }else {
                    nameView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initializeViews(View rootView) {
        pdfView = (PDFView) rootView.findViewById(R.id.pdf_view);
        editButton = (ImageButton)rootView.findViewById(R.id.pdf_edit_button);
        uploadButton = (ImageButton)rootView.findViewById(R.id.pdf_upload_button);
        nameView = (EditText)rootView.findViewById(R.id.pdf_name_input);
        pageCountView = (TextView)rootView.findViewById(R.id.pdf_page_counter);
        isVisible = false;
        pageNumber = 1;
    }

    private boolean viewVisible(){
        if(isVisible){
            isVisible = false;
            return true;
        }else{
            isVisible = true;
            return false;
        }
    }

    private void uploadListener() {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    private void setUriAndPreview() {
        Bundle bundle = this.getArguments();
        String stringUri = bundle.getString("stringUri");
        Log.d("setUriAndPreview()", "Value of stringUri: " + stringUri);
        fileUri = Uri.parse(stringUri);
        Log.d("setUriAndPreview()", "Value of stringUri: " + stringUri);
        name = stringUri.substring(stringUri.lastIndexOf('/') + 1, stringUri.indexOf('.'));
        pdf = new File(fileUri.getPath());
        nameView.setText(stringUri.substring(stringUri.lastIndexOf('/') + 1, stringUri.indexOf('.')));
        setImage();
    }

    private void setImage() {
        pdfView.fromFile(pdf)//test loading frim
                .defaultPage(pageNumber)
                .onPageChange(this)
                .load();
        pageCountView.setText(Integer.toString(pageNumber));
    }

    private void setUploadProgressDialog() {
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Uploading file ...");
    }

    private void upload() {
        ringProgressDialog.show();
        new Thread() {
            public void run() {
                try {
                    UploadProcess upobj = new UploadProcess(getContext(), nameView, fileUri, ringProgressDialog);
                    upobj.uploadProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        pageCountView.setText(Integer.toString(page));
        //setTitle(format("%s %s / %s", name, page, pageCount));
    }
}
