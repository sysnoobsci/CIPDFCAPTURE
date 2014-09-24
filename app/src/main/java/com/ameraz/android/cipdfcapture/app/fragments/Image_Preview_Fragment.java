package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMessageTask;
import com.ameraz.android.cipdfcapture.app.MyBrowser;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.ViewLoader;
import com.joanzapata.pdfview.PDFView;

import java.io.File;
import java.io.IOException;

/**
 * Created by adrian.meraz on 9/18/2014.
 */
public class Image_Preview_Fragment extends Fragment {

    static View rootView;
    private PDFView pdfViewer;
    private TextView textViewer;
    private ImageView imageView;
    ImageButton saveButton;
    Context context;
    String uri;
    String format;


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_image_preview, container, false);
        setContext(getActivity());
        instantiateViews();
        loadImage();
        saveButtonListener();
        return rootView;
    }

    public void instantiateViews() {
        pdfViewer = (PDFView) rootView.findViewById(R.id.pdfview);
        textViewer = (TextView) rootView.findViewById(R.id.textView2);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        saveButton = (ImageButton) rootView.findViewById(R.id.download_and_save);
    }

    public void loadImage() {
        Bundle bundle = this.getArguments();
        uri = bundle.getString("retrieve_fileName");
        format = bundle.getString("retrieve_fileFormat");
        try {
            ViewLoader vl = new ViewLoader(format,pdfViewer,textViewer,imageView,context);
            vl.loadFileIntoView(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void saveButtonListener() {//searches for the report and displays the versions
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ToastMessageTask tmtask = new ToastMessageTask(getContext(), "Save button pressed");
                    tmtask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
