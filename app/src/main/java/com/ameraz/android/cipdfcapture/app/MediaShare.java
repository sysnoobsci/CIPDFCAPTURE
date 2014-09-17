package com.ameraz.android.cipdfcapture.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.ameraz.android.cipdfcapture.app.ExtendedClasses.GestureImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by John Williams on 9/15/2014.
 * MediaShare adds the ability to bring in an image from any application that offers the share action.
 */
public class MediaShare extends Activity {
    private ImageButton uploadButton;
    private GestureImageView imageView;
    private EditText uploadName;
    private Uri imageUri;
    private ProgressDialog ringProgressDialog;
    Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private void setUploadName() {
        uploadName.setText(getUploadName());
    }

    private String getUploadName() {
        String fileName = imageUri.toString();
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
        Log.d("fileName =", fileName);
        return fileName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_upload_layout);
        Intent receivedIntent = getIntent();
        initializeViews();
        setUploadProgressDialog();
        setUploadButtonListener();
        setContext(getApplicationContext());
        imageUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        imageUri = Uri.parse("file://" + getRealPathFromURI(imageUri));
        Log.d("MediaShare.onCreate()", "imageUri value: " + imageUri.toString());
        if (imageUri != null) {
            setUploadName();
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
                    UploadProcess upobj = new UploadProcess(getContext(), uploadName, imageUri, ringProgressDialog);
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



    public String getRealPathFromURI(Uri imageUri)    {
        String realPath = "";
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            realPath = cursor.getString(columnIndex);
        } else {
            Log.d("Path =", "path not found...");
        }
        //realPath = realPath.replace("/storage/emulated/0", "/sdcard");
        Log.d("RealPath imageUri =", realPath);
        return realPath;
    }



}
