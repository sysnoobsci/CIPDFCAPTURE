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

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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

    SharedPreferences preferences;
    final static private String tplid1 = "create.phonecapture";//time in milliseconds for createtopic attempt to timeout

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_single_layout, container, false);
        initializeViews(rootView);
        apiobj = new APIQueries(getActivity());
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
        imageView = (GestureImageView)rootView.findViewById(R.id.gallery_single_image_view);
        imageButton = (ImageButton)rootView.findViewById(R.id.gallery_single_image_upload_button);
        description = (EditText)rootView.findViewById(R.id.gallery_single_image_description_text);
    }

    private void setImage() {
        Picasso.with(getActivity())
                .load(imageUri)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    public void uploadButton() throws IOException, XmlPullParserException, InterruptedException,
            ExecutionException, TimeoutException {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        loginlogoff liloobj = new loginlogoff(getActivity());
        ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), "Performing Action ...",
                "Uploading file ...", true);
        MainActivity.argslist.add(loginlogoff.getSid());
        if (apiobj.pingQuery(MainActivity.argslist)) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Login successful and ready to upload file.");
            //create a topic instance object
            createTopic(ringProgressDialog);
        }
        else {//if ping fails, selected ci profile will be used to log back in
            Log.d("Message", "Ping to CI server indicated no login session.");
            if (liloobj.tryLogin()) {
                Log.d("Message", "CI Login successful and ready to upload file.");
                createTopic(ringProgressDialog);
            }
            else{//if login attempt fails from trying the CI server profile, prompt user to check profile
                ringProgressDialog.dismiss();
                ToastMessageTask.noConnectionMessage(getActivity());
            }
        }
    }

    void createTopic(final ProgressDialog ringProgressDialog) {
        if (imageUri != null || !description.getText().toString().isEmpty()) {
            MainActivity.argslist.add("tplid," + tplid1);
            MainActivity.argslist.add("name," + description.getText().toString());
            MainActivity.argslist.add("detail,y");
            MainActivity.argslist.add("sid," + loginlogoff.getSid());
            MainActivity.argslist.add(imageUri);
            new Thread() {
                public void run() {
                    try {
                        apiobj.createtopicQuery(MainActivity.argslist);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ringProgressDialog.dismiss();
                }
            }.start();
        } else {
            ringProgressDialog.dismiss();
            ToastMessageTask tmtask = new ToastMessageTask(getActivity(), "Error. Fill out all the fields.");
            tmtask.execute();
        }
    }
}
