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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMessageTask;
import com.ameraz.android.cipdfcapture.app.FilePath;
import com.ameraz.android.cipdfcapture.app.MyBrowser;
import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.ViewLoader;
import com.joanzapata.pdfview.PDFView;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
    Uri fileUri;
    String filePath;
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
        String uri = bundle.getString("retrieve_fileName");
        Log.d("filename= ", uri);
        FilePath fp = new FilePath();
        fileUri = Uri.parse("file://" + fp.getTempFilePath() + uri);
        filePath = fileUri.getPath();
        format = bundle.getString("retrieve_fileFormat");
        if(format == "PDF"){
            pdfViewer.setVisibility(View.VISIBLE);
            textViewer.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            setPDF();
        }else if(format == "TXT" || format == "XML" | format == "ASC"){
            pdfViewer.setVisibility(View.GONE);
            textViewer.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            setText();
        }else{
            pdfViewer.setVisibility(View.GONE);
            textViewer.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            setImage();
        }
    }

    public void setPDF(){
        File pdfFile = new File(filePath);
        pdfViewer.fromFile(pdfFile)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .load();
    }

    public void setText(){
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(new File(filePath)));
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textViewer.setText(stringBuilder.toString());
    }

    public void setImage(){
        Picasso.with(context)
                .load(Uri.fromFile(new File(filePath)))
                .fit()
                .centerInside()
                .into(imageView);
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
