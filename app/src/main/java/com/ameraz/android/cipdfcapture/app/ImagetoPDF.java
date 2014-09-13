package com.ameraz.android.cipdfcapture.app;

import android.net.Uri;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by john.williams on 9/12/2014.
 */
public class ImageToPDF {
    String input;
    String output;

    public ImageToPDF(String input, String output) {
        this.input = input;
        this.output = output;
    }
    public boolean convertImagetoPDF() {
        Document document = new Document();
        File newImage = new File(output);
        if (!newImage.exists()) {
            newImage.getParentFile().mkdirs();
            try {
                newImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
            try {
                FileOutputStream fos = new FileOutputStream(newImage);
                PdfWriter writer = PdfWriter.getInstance(document, fos);
                writer.open();
                document.open();
                document.add(Image.getInstance(input));
                document.close();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
}
