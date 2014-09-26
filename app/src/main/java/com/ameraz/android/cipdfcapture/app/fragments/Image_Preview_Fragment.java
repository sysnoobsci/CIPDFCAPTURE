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

/**
 * Created by adrian.meraz on 9/18/2014.
 */
public class Image_Preview_Fragment extends Fragment {

    static View rootView;
    private PDFView pdfViewer;
    private TextView textViewer;
    private ImageView imageViewer;
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
        pdfViewer = (PDFView) rootView.findViewById(R.id.pdfViewer);
        textViewer = (TextView) rootView.findViewById(R.id.textViewer);
        imageViewer = (ImageView) rootView.findViewById(R.id.imageViewer);
        saveButton = (ImageButton) rootView.findViewById(R.id.downloadButton);
    }

    public void loadImage() {
        Bundle bundle = this.getArguments();
        String uri = bundle.getString("retrieve_fileName");
        format = bundle.getString("retrieve_fileFormat");
        Log.d("filename= ", uri);
        FilePath fp = new FilePath();
        fileUri = Uri.parse("file://" + fp.getTempFilePath() + uri);
        filePath = fileUri.getPath();
        if(format == "PDF"){
            pdfViewer.setVisibility(View.VISIBLE);
            textViewer.setVisibility(View.GONE);
            imageViewer.setVisibility(View.GONE);
            setPDF();
        }else if(format == "TXT" || format == "XML" | format == "ASC"){
            pdfViewer.setVisibility(View.GONE);
            textViewer.setVisibility(View.VISIBLE);
            imageViewer.setVisibility(View.GONE);
            setText();
        }else{
            pdfViewer.setVisibility(View.GONE);
            textViewer.setVisibility(View.GONE);
            imageViewer.setVisibility(View.VISIBLE);
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
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(new File(filePath)));
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
                .load(Uri.fromFile(new File(filePath)))
                .fit()
                .centerInside()
                .into(imageViewer);
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
            new DownloadFileTaskTest(FilePath.chooseDownloadFilePath(format),fileUri.toString(),getActivity(),getContext())
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fileUri.toString());//download response and create a new file
            }
        });
    }


}
