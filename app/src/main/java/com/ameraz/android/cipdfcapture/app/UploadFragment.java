package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    SharedPreferences preferences;
    final static private String tplid1 = "create.phonecapture";//time in milliseconds for createtopic attempt to timeout

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_single_layout, container, false);
        initializeViews(rootView);
        apiobj = new APIQueries(getActivity());
        ringProgressDialog = new ProgressDialog(getActivity());
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
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        loginlogoff liloobj = new loginlogoff(getActivity());
        ringProgressDialog.show();
        QueryArguments.addArg(loginlogoff.getSid());
        if (apiobj.pingQuery(QueryArguments.argslist)) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Login successful and ready to upload file.");
            createTopic();//create a topic instance object
        } else {//if ping fails, selected ci profile will be used to log back in
            Log.d("Message", "Ping to CI server indicated no login session.");
            if (liloobj.tryLogin()) {
                Log.d("Message", "CI Login successful and ready to upload file.");
                createTopic();//create a topic instance object
            } else {//if login attempt fails from trying the CI server profile, prompt user to check profile
                ringProgressDialog.dismiss();
                ToastMessageTask.noConnectionMessage(getActivity());
            }
        }
    }

    void createTopic() {
        if (!String.valueOf(description.getText()).isEmpty()) {
            if (imageUri == null) {//checks if image taken yet
                ToastMessageTask.picNotTaken(getActivity());
            } else {
                QueryArguments.addArg("tplid," + tplid1);
                QueryArguments.addArg("name," + description.getText().toString());
                QueryArguments.addArg("detail,y");
                QueryArguments.addArg("sid," + loginlogoff.getSid());
                QueryArguments.addArg(imageUri);
                new Thread() {
                    public void run() {
                        try {
                            apiobj.createtopicQuery(QueryArguments.argslist);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ringProgressDialog.dismiss();
                    }
                }.start();
            }
        } else {
            ringProgressDialog.dismiss();
            ToastMessageTask.fillFieldMessage(getActivity());
        }
    }
}
