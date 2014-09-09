package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ameraz.android.cipdfcapture.app.ExtendedClasses.GestureImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by john.williams on 8/27/2014.
 * UploadFragment gets the image path from InternalGalleryFragment and displays it on the screen.
 * It then allows you to upload the document to the Content Server.
 */

public class UploadFragment extends Fragment {

    private Uri imageUri;
    private GestureImageView imageView;
    private ImageButton imageButton;
    private EditText description;
    APIQueries apiobj = null;
    ProgressDialog ringProgressDialog;
    Context context;

    SharedPreferences preferences;
    final static private String tplid1 = "create.phonecapture";//time in milliseconds for createtopic attempt to timeout

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_single_layout, container, false);
        initializeViews(rootView);
        context = getActivity();
        apiobj = new APIQueries(context);
        ringProgressDialog = new ProgressDialog(context);
        setUploadProgressDialog();
        setImageButtonListener();
        //gets the data passed to it from InternalGalleryFragment and creates the uri.
        Bundle bundle = this.getArguments();
        String imageUriString = bundle.getString("inc_string");
        imageUri = Uri.parse(imageUriString);
        //Using the Picasso library, loads the image onto the screen.
        setImage();
        return rootView;
    }

    private void setImageButtonListener() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadButton();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeViews(View rootView) {
        imageView = (GestureImageView) rootView.findViewById(R.id.gallery_single_image_view);
        imageButton = (ImageButton) rootView.findViewById(R.id.gallery_single_image_upload_button);
        description = (EditText) rootView.findViewById(R.id.gallery_single_image_description_text);
    }

    private void setUploadProgressDialog() {
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Uploading file ...");
    }

    private void setImage() {
        Picasso.with(getActivity())
                .load(imageUri)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    public void uploadButton() throws Exception {
        if (uploadCheck(description, imageUri)) {
            loginlogoff liloobj = new loginlogoff(context);
            ringProgressDialog.show();
            QueryArguments.addArg(loginlogoff.getSid());
            Log.d("Message", "Ping to CI server indicated no login session.");
            if (liloobj.tryLogin(context)) {
                Log.d("Message", "CI Login successful and ready to upload file.");
                createTopic();//create a topic instance object
            }
            ringProgressDialog.dismiss();
        }
    }

    void createTopic() {
        QueryArguments.addArg("tplid," + tplid1);
        QueryArguments.addArg("name," + description.getText().toString());
        QueryArguments.addArg("detail,y");
        QueryArguments.addArg("sid," + loginlogoff.getSid());
        QueryArguments.addArg(imageUri);
        new Thread() {
            public void run() {
                try {
                    apiobj.createtopicQuery(QueryArguments.getArgslist());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ringProgressDialog.dismiss();
            }
        }.start();
    }

    Boolean uploadCheck(EditText description, Uri imageUri) {
        if (imageUri == null) {//checks if image taken yet
            ringProgressDialog.dismiss();
            ToastMessageTask.picNotTaken(context);
            return false;
        }
        if (String.valueOf(description.getText()).isEmpty()) {
            ringProgressDialog.dismiss();
            ToastMessageTask.fillFieldMessage(context);
            return false;
        }
        return true;//if pic was taken and there is a non-empty description, return true
    }
}
