package com.ameraz.android.cipdfcapture.app;

import android.net.Uri;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
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
    Uri imageUri;
    File newImage;

    public ImageToPDF(String input, String output) {
        this.input = input;
        this.output = output;
    }

    public void createFile(){
        newImage = new File(output);
        imageUri = Uri.fromFile(newImage);
    }

    public Uri getImageUri(){
        return imageUri;
    }

    public boolean convertImagetoPDF() {
        createFile();
        Document document = new Document();
            try {
                FileOutputStream fos = new FileOutputStream(newImage);
                PdfWriter writer = PdfWriter.getInstance(document, fos);
                writer.open();
                document.open();
                document.setPageSize(PageSize.A4);
                if(!imageAdded(document)){
                    return false;
                }
                document.close();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

    private boolean imageAdded(Document document) {
        Image image;
        try {
            image = Image.getInstance(input);
        } catch (BadElementException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        image.setRotationDegrees(270f);
        int indentation = 0;
        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                - document.rightMargin() - indentation) / image.getWidth()) * 100;

        image.scalePercent(scaler);

        try {
            document.add(image);
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
