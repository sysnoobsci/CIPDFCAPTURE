package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

/**
 * Created by john.williams on 8/27/2014.
 */
public class ImageCreator {
    private Uri imageUri;
    private Bitmap myImage;
    private Context maContext;
    private int width;
    private int height;

    public void ImageCreator(){
    }
    public void setContext(Context c){
        maContext = c;
    }

    public void setImageUri(Uri uri){
        imageUri = uri;
    }

    public Bitmap getMyImage(){
        return myImage;
    }
    public int getImageRotation(){
        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(
                    imageUri.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Rotate = ", String.valueOf(rotate));
        return rotate;
    }


}
