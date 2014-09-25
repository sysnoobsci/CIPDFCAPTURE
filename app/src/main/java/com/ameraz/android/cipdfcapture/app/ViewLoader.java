package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.pdfview.PDFView;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by The Bat Cave on 9/24/2014.
 */
public class ViewLoader {

    static String versionFormat;
    static PDFView pdfViewer;
    static TextView textViewer;
    static ImageView imageView;
    static Context context;

    public ViewLoader(String versionFormat, PDFView pdfViewer, TextView textViewer, ImageView imageView, Context context) {
        this.versionFormat = versionFormat;
        this.pdfViewer = pdfViewer;
        this.textViewer = textViewer;
        this.imageView = imageView;
        this.context = context;
    }

    public static void loadFileIntoView(String fullFilePath) throws IOException {
        try {
            if (versionFormat.equals("PDF")) {//if format is PDF
                File pdfFile = new File(fullFilePath);
                viewVisibilityChecker();

                pdfViewer.fromFile(pdfFile)
                        .defaultPage(1)
                        .showMinimap(false)
                        .enableSwipe(true)
                        .load();

            } else if (versionFormat.equals("TXT") || versionFormat.equals("ASC") ||
                    versionFormat.equals("XML")) {//if format is ascii-text
                viewVisibilityChecker();
                BufferedReader r = new BufferedReader(new FileReader(fullFilePath));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                textViewer.setText(total.toString());
            } else {//if format is an image
                viewVisibilityChecker();
                Picasso.with(context)
                        .load(Uri.fromFile(new File(fullFilePath)))
                        .fit()
                        .centerInside()
                        .into(imageView);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void viewVisibilityChecker(){//makes views visible depending on the file format
        if(versionFormat.equals("PDF")){
            pdfViewer.setVisibility(View.VISIBLE);//makes pdfviewer visible
            textViewer.setVisibility(View.GONE);//removes textView from view
            imageView.setVisibility(View.GONE);//removes imageView from view
        }
        else if(versionFormat.equals("TXT") || versionFormat.equals("ASC") ||
                versionFormat.equals("XML")) {
            pdfViewer.setVisibility(View.GONE);
            textViewer.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }
        else{
            pdfViewer.setVisibility(View.GONE);
            textViewer.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}
