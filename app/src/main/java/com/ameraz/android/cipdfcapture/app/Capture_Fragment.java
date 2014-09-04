package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by John Williams on 6/2/2014.
 */
public class Capture_Fragment extends Fragment {
    private GestureImageView imageView;
    private ImageView background;
    private ImageButton takePic;
    private ImageButton sharePDF;
    private EditText descriptionText1;
    private Uri imageUri;
    private String incImage;
    private File newImage;
    Context maContext;

    SharedPreferences preferences;
    final static private int NVPAIRS = 1;//number of nvpairs in createtopic api call
    final static private String tplid1 = "create.phonecapture";//time in milliseconds for createtopic attempt to timeout

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capture_fragment, container, false);
        maContext = getActivity();
        initializeViews(rootView);
        setCaptureBackground();
        takePicButtonListener();
        sharePDFListener();
        return rootView;
    }

    private void setCaptureBackground() {
        Picasso.with(maContext)
                .load(R.drawable.clouds_parlx_bg1)
                .fit()
                .centerInside()
                .into(background);
    }

    private void sharePDFListener() {
        sharePDF.setOnClickListener(new View.OnClickListener() {
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

    private void takePicButtonListener() {
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilePath fp = new FilePath();
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    incImage = fp.getFilePath()+"sys_original_image" + System.currentTimeMillis() + ".jpg";
                    newImage = new File(incImage);
                    try {
                        if (!newImage.exists()) {
                            newImage.getParentFile().mkdirs();
                            newImage.createNewFile();
                        }

                    } catch (IOException e) {
                        Log.e("File: ", "Could not create file.", e);
                    }
                    Log.i("File: ", incImage);
                }
                    imageUri = Uri.fromFile(newImage);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 0);
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
        Picasso.with(maContext)
                .load(imageUri)
                .fit()
                .centerInside()
                .into(imageView);
    }

    private void initializeViews(View rootView) {
        background = (ImageView) rootView.findViewById(R.id.capture_background);
        imageView = (GestureImageView) rootView.findViewById(R.id.imageView);
        takePic = (ImageButton) rootView.findViewById(R.id.capture_new_pic);
        sharePDF = (ImageButton) rootView.findViewById(R.id.capture_share);
        descriptionText1 = (EditText) rootView.findViewById(R.id.description_text);
    }

    public void uploadButton() throws IOException, XmlPullParserException, InterruptedException,
            ExecutionException, TimeoutException {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        LoginLogoff liloobj = new LoginLogoff(getActivity());
        final APIQueries apiobj = new APIQueries(getActivity());
        final ProgressDialog ringProgressDialog = ProgressDialog.show(maContext, "Performing Action ...",
                "Uploading file ...", true);
        MainActivity.argslist.add(LoginLogoff.getSid());
        if (apiobj.pingQuery(MainActivity.argslist)) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Login successful and ready to upload file.");
            //create a topic instance object
            if(imageUri != null || !descriptionText1.getText().toString().isEmpty()) {
                String[] nvpairsarr = new String[NVPAIRS];
                nvpairsarr[0] = "name,"+ descriptionText1.getText().toString();

                MainActivity.argslist.add("tplid," + tplid1);
                MainActivity.argslist.add(nvpairsarr[0]);
                MainActivity.argslist.add("detail,y");
                MainActivity.argslist.add("sid,"+ LoginLogoff.getSid());
                MainActivity.argslist.add(imageUri);
                new Thread() {
                    public void run() {
                        try {
                            apiobj.createtopicQuery(MainActivity.argslist);
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
                if(imageUri != null || !descriptionText1.getText().toString().isEmpty()) {
                    final String[] nvpairsarr = new String[NVPAIRS];
                    nvpairsarr[0] = "name,"+ descriptionText1.getText().toString();
                    MainActivity.argslist.add("tplid," + tplid1);
                    MainActivity.argslist.add(nvpairsarr[0]);
                    MainActivity.argslist.add("detail,y");
                    MainActivity.argslist.add("sid," + LoginLogoff.getSid());
                    MainActivity.argslist.add(imageUri);
                    new Thread() {
                        public void run() {
                            try {
                                apiobj.createtopicQuery(MainActivity.argslist);
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

/*    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(maContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }*/
}
