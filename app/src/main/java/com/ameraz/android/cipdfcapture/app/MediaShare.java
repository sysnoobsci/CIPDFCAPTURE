package com.ameraz.android.cipdfcapture.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ameraz.android.cipdfcapture.app.ExtendedClasses.GestureImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by john.williams on 9/15/2014.
 */
public class MediaShare extends Activity {
    private ImageButton uploadButton;
    private GestureImageView imageView;
    private EditText uploadName;
    private Uri imageUri;
    private ProgressDialog ringProgressDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_upload_layout);
        Intent receivedIntent = getIntent();
        initializeViews();
        setUploadProgressDialog();
        setUploadButtonListener();
        context = this;

        imageUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        //imageUri = Uri.parse("file:/" + getImagePath(imageUri));
        //imageUri.getPath();
        String filePath = imageUri.getPath();
        //String filePath = imageUri.toString();
        String yourRealPath = "";
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            yourRealPath = cursor.getString(columnIndex);
        } else {
            //boooo, cursor doesn't have rows ...
        }
        cursor.close();
        if (imageUri != null) {
            Log.d("imageUri =", yourRealPath);
            //setUploadName();
            setImage();
        }
    }

    private void setUploadButtonListener() {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    private void upload() {
        ringProgressDialog.show();
        new Thread() {
            public void run() {
                try {
                    UploadProcess upobj = new UploadProcess(context, uploadName, imageUri, ringProgressDialog);
                    upobj.uploadProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setUploadProgressDialog() {
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Uploading file ...");
    }

    private void initializeViews() {
        uploadButton = (ImageButton)findViewById(R.id.image_upload_button);
        imageView = (GestureImageView)findViewById(R.id.upload_image_view);
        uploadName = (EditText)findViewById(R.id.upload_name_input);
        ringProgressDialog = new ProgressDialog(this);
    }

    private void setImage() {
        Picasso.with(this)
                .load(imageUri)
                .fit()
                .centerInside()
                .into(imageView);
    }

    private void setUploadName() {
        uploadName.setText(getUploadName());
    }

    private String getUploadName() {
        Cursor returnCursor =
                getContentResolver().query(imageUri, null, null, null, null);
        return returnCursor.getString(returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
    }

    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public String getRealPathFromURI(Uri imageUri)
    {
        String[] proj = { MediaStore.Audio.Media.DATA };
        String[] proj1 = {MediaStore.EXTRA_OUTPUT};
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        Cursor cursor = getContentResolver().query(imageUri, proj, null, null, null); //Since manageQuery is deprecated
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
