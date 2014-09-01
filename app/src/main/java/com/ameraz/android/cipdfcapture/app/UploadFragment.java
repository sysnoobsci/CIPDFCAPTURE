package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ameraz.android.cipdfcapture.app.ExtendedClasses.GestureImageView;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by john.williams on 8/27/2014.
 * UploadFragment gets the image path from InternalGalleryFragment and displays it on the screen.
 * It then allows you to upload the document to the Content Server.
 */

public class UploadFragment extends Fragment {

    private Context maContext;
    private Uri imageUri;
    private GestureImageView imageView;
    private ImageButton imageButton;
    private EditText description;

    final static ArrayList<Object> argslist = new ArrayList<Object>();
    SharedPreferences preferences;
    final static private int NVPAIRS = 1;//number of nvpairs in createtopic api call
    final static private String tplid1 = "create.phonecapture";//time in milliseconds for createtopic attempt to timeout

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_single_layout, container, false);
        initializeViews(rootView);
        maContext = getActivity();

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
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
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
        Picasso.with(maContext)
                .load(imageUri)
                .fit()
                .centerCrop()
                .into(imageView);
    }



    public void uploadButton() throws IOException, XmlPullParserException, InterruptedException,
            ExecutionException, TimeoutException {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        LoginLogoff liloobj = new LoginLogoff(getActivity());
        final APIQueries apiobj = new APIQueries(getActivity());
        final ProgressDialog ringProgressDialog = ProgressDialog.show(maContext, "Performing Action ...",
                "Uploading file ...", true);
        argslist.add(LoginLogoff.getSid());
        if (apiobj.pingQuery(argslist)) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Login successful and ready to upload file.");
            //create a topic instance object
            if(imageUri != null || !description.getText().toString().isEmpty()) {
                String[] nvpairsarr = new String[NVPAIRS];
                nvpairsarr[0] = "name,"+ description.getText().toString();

                argslist.add("tplid," + tplid1);
                argslist.add(nvpairsarr[0]);
                argslist.add("detail,y");
                argslist.add("sid,"+ LoginLogoff.getSid());
                argslist.add(imageUri);
                new Thread() {
                    public void run() {
                        try {
                            apiobj.createtopicQuery(argslist);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        ringProgressDialog.dismiss();
                    }
                }.start();
            }
            else{
                ringProgressDialog.dismiss();
                ToastMessageTask tmtask = new ToastMessageTask(getActivity(),"Error. Fill out all the fields.");
                tmtask.execute();
            }

        }
        else {//if ping fails, selected ci profile will be used to log back in
            Log.d("Message", "Ping to CI server indicated no login session.");
            boolean login = false;
            try{
                liloobj.tryLogin();
                login = true;
            }catch(Exception e){
                e.printStackTrace();
                Log.d("Ping: ", "failed ping check");
                login = false;
            }
            if(login) {
                Log.d("Message", "CI Login successful and ready to upload file.");
                if(imageUri != null || !description.getText().toString().isEmpty()) {
                    final String[] nvpairsarr = new String[NVPAIRS];
                    nvpairsarr[0] = "name,"+ description.getText().toString();
                    argslist.add("tplid," + tplid1);
                    argslist.add(nvpairsarr[0]);
                    argslist.add("detail,y");
                    argslist.add("sid," + LoginLogoff.getSid());
                    argslist.add(imageUri);
                    new Thread() {
                        public void run() {
                            try {
                                apiobj.createtopicQuery(argslist);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                            ringProgressDialog.dismiss();
                        }
                    }.start();
                }
                else{
                    ringProgressDialog.dismiss();
                    ToastMessageTask tmtask = new ToastMessageTask(getActivity(),"Error. Fill out all the fields.");
                    tmtask.execute();
                }
            }
            else{//if login attempt fails from trying the CI server profile, prompt user to check profile
                ringProgressDialog.dismiss();
                ToastMessageTask tmtask = new ToastMessageTask(getActivity(),"Connection to CI Server failed. Check" +
                        "CI Connection Profile under Settings.");
                tmtask.execute();
            }
        }
    }
}
