package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMsgTask;
import com.ameraz.android.cipdfcapture.app.AsyncTasks.UploadFileTask;
import com.ameraz.android.cipdfcapture.app.SupportingClasses.APIQueries;
import com.ameraz.android.cipdfcapture.app.ExtendedClasses.GestureImageView;
import com.ameraz.android.cipdfcapture.app.SupportingClasses.FileUtility;
import com.ameraz.android.cipdfcapture.app.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;


/**
 * Created by John Williams on 6/2/2014.
 */
public class Capture_Fragment extends Fragment {
    private GestureImageView imageView;
    private ImageView background;
    private ImageButton takePic;
    private ImageButton uploadButton;
    private EditText description;
    private Uri imageUri;
    private String incImage;
    private String outImage;
    private File newImage;
    APIQueries apiobj;
    static Context context;


    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Capture_Fragment.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_capture, container, false);
        initializeViews(rootView);
        setContext(getActivity());
        apiobj = new APIQueries(getContext());
        setCaptureBackground();
        takePicButtonListener();
        uploadButtonListener();
        return rootView;
    }

    private void setCaptureBackground() {
        Picasso.with(getContext())
                .load(R.drawable.clouds_parlx_bg1)
                .fit()
                .centerInside()
                .into(background);
    }

    private void uploadButtonListener() {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadFileTask(getContext(), description, imageUri)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    private void takePicButtonListener() {
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtility fp = new FileUtility();
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    incImage = fp.getImageFilePath() + "Images/" + "sys_image" + System.currentTimeMillis() + ".jpg";
                    outImage = fp.getImageFilePath() + "PDF/" + "sys_pdf" + System.currentTimeMillis() + ".pdf";
                    newImage = new File(incImage);
                    try {
                        if (!newImage.exists()) {
                            newImage.getParentFile().mkdirs();
                            newImage.createNewFile();
                            imageUri = Uri.fromFile(newImage);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, 0);
                        }
                    } catch (IOException e) {
                        ToastMsgTask.fileNotWritten(getContext());
                        Log.e("File: ", "Could not create file.", e);
                    }
                    Log.i("File: ", incImage);
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            Log.d("onActivityResult ", imageUri.toString());
            setCapturedImage();
        }
    }

    private void setCapturedImage() {
        Picasso.with(getContext())
                .load(imageUri)
                .fit()
                .centerInside()
                .into(imageView);
    }

    private void initializeViews(View rootView) {
        background = (ImageView) rootView.findViewById(R.id.capture_background);
        imageView = (GestureImageView) rootView.findViewById(R.id.imageView);
        takePic = (ImageButton) rootView.findViewById(R.id.capture_new_pic);
        uploadButton = (ImageButton) rootView.findViewById(R.id.capture_share);
        description = (EditText) rootView.findViewById(R.id.description_text);
    }

}
