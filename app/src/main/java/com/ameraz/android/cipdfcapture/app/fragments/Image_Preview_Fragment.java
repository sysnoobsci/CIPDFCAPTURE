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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.AsyncTasks.DownloadFileTaskTest;
import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMessageTask;
import com.ameraz.android.cipdfcapture.app.FilePath;
import com.ameraz.android.cipdfcapture.app.R;
import com.joanzapata.pdfview.PDFView;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by adrian.meraz on 9/18/2014.
 */
public class Image_Preview_Fragment extends Fragment {

    static View rootView;
    private PDFView pdfViewer;
    private TextView textViewer;
    private ImageView imageViewer;
    private ScrollView textScrollView;
    ImageButton saveButton;
    Context context;
    Uri fileUri;
    String fullFilePath;
    String format;
    int versionNumber;
    ArrayList<String> textFormats = new ArrayList<String>() {{
        add("ASC");
        add("TXT");
        add("XML");
    }};
    ArrayList<String> imageFormats = new ArrayList<String>() {{
        add("IMG");
        add("BMP");
        add("JPG");
        add("GIF");
        add("PNG");
        add("TIF");
    }};


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_image_preview, container, false);
        context = getActivity();
        instantiateViews();
        loadImage();
        saveButtonListener();
        return rootView;
    }

    public void instantiateViews() {
        pdfViewer = (PDFView) rootView.findViewById(R.id.pdfViewer);
        textViewer = (TextView) rootView.findViewById(R.id.textViewer);
        imageViewer = (ImageView) rootView.findViewById(R.id.imageViewer);
        saveButton = (ImageButton) rootView.findViewById(R.id.downloadButton);
        textScrollView = (ScrollView)rootView.findViewById(R.id.text_scroll_view);
    }

    public void loadImage() {
        Bundle bundle = this.getArguments();
        String uri = bundle.getString("retrieve_fileName");
        format = bundle.getString("retrieve_fileFormat").toUpperCase();
        versionNumber = Integer.parseInt(bundle.getString("retrieve_versionNumber"));
        Log.d("loadImage()","Value of format: " + format);
        Log.d("loadImage()", "Value of uri: " + uri);
        fileUri = Uri.parse("file://" + uri);
        fullFilePath = fileUri.getPath();
        if(format.equals("PDF")){
            pdfViewer.setVisibility(View.VISIBLE);
            textScrollView.setVisibility(View.GONE);
            imageViewer.setVisibility(View.GONE);
            setPDF();
            Log.d("loadImage()","setPDF() called");
        }else if(textFormats.contains(format)){
            pdfViewer.setVisibility(View.GONE);
            textScrollView.setVisibility(View.VISIBLE);
            imageViewer.setVisibility(View.GONE);
            setText();
            Log.d("loadImage()","setText() called");
        }else if(imageFormats.contains(format)){
            pdfViewer.setVisibility(View.GONE);
            textScrollView.setVisibility(View.GONE);
            imageViewer.setVisibility(View.VISIBLE);
            setImage();
            Log.d("loadImage()","setImage() called");
        }else{
            ToastMessageTask.invalidFileFormat(context);
        }

    }

    public void setPDF(){
        File pdfFile = new File(fullFilePath);
        pdfViewer.fromFile(pdfFile)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .load();
    }

    public void setText(){
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(new File(fullFilePath)));
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line)
                             .append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        textViewer.setText(stringBuilder.toString());
    }

    public void setImage(){
         Picasso.with(context)
                .load(Uri.fromFile(new File(fullFilePath)))
                .fit()
                .centerInside()
                .into(imageViewer);
    }

    private void saveButtonListener() {//searches for the report and displays the versions
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new DownloadFileTaskTest(FilePath.chooseDownloadFilePath(format),fullFilePath, versionNumber, getActivity())
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fileUri.toString());//download response and create a new file
            }
        });
    }


}
