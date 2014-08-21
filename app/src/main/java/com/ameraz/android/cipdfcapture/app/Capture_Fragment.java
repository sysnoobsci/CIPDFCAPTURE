package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

import com.ameraz.android.cipdfcapture.app.filebrowser.FileChooser;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by John Williams on 6/2/2014.
 */
public class Capture_Fragment extends Fragment {
    static byte[] bArray;
    static com.itextpdf.text.Image image;
    private ImageView imageView;
    private ImageButton takePic;
    private ImageButton searchGallery;
    private ImageButton navFileSystem;
    private ImageButton savePDF;
    private ImageButton sharePDF;
    private EditText descriptionText1;
    private Uri imageUri;
    private String incImage;
    private File newImage;
    private Bitmap myImage;
    private Bitmap bm;
    private int width;
    private int height;

    SharedPreferences preferences;



    final static private int LOGIN_TIMEOUT = 500;//time in milliseconds for login attempt to timeout
    final static private int CT_TIMEOUT = 500;//time in milliseconds for createtopic attempt to timeout
    final static private int NVPAIRS = 2;//number of nvpairs in createtopic api call
    final static private String tplid1 = "create.redmine1625";//time in milliseconds for createtopic attempt to timeout

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capture_fragment, container, false);
        descriptionText1 = (EditText) rootView.findViewById(R.id.description_text);
        initializeViews(rootView);
        takePicButtonListener();
        searchGalleryListener();
        navFileSystemListener();
        savePDFListener();
        sharePDFListener();
        imageViewListener();

        return rootView;
    }

    private void imageViewListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri!=null){
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(imageUri, "image/png");
                    startActivity(intent);
                }else{
                    ToastMessageTask tmtask = new ToastMessageTask(getActivity(), "Nothing to view.");
                    tmtask.execute();
                }
            }
        });
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

    private void savePDFListener() {
        savePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView == null || imageUri == null) {
                    ToastMessageTask tmtask = new ToastMessageTask(getActivity(), "Nothing to save.");
                    tmtask.execute();
                } else {
                    new saveImageAsPDF().execute();
                }
            }
        });
    }

    private void navFileSystemListener() {
        navFileSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FileChooser.class);
                startActivityForResult(intent, 2);
            }
        });
    }

    private void searchGalleryListener() {
        searchGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });
    }

    private void takePicButtonListener() {
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    incImage = "/storage/sdcard0/Systemware/sys_original_image" + System.currentTimeMillis() + ".jpg";
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

                    imageUri = Uri.fromFile(newImage);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 0);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //getImageDimensions();
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Log.d("onActivityResult ", "case 0");
                    scaleAndDisplayBitmap();
                    break;
                case 1:
                    Log.d("onActivityResult ", "case 1");
                    imageUri = data.getData();
                    //getImageOrientation();
                    scaleAndDisplayBitmap();
                    break;
                case 2:
                    Log.d("onActivityResult ", "case 2");
                    String loc = "file://" + data.getStringExtra("GetPath") + "/" + data.getStringExtra("GetFileName");
                    imageUri = Uri.parse(loc);
                    scaleAndDisplayBitmap();
                    break;
                default:
                    break;
            }
        }
    }

    private void rotateImage() {
        File imageFile = new File(imageUri.toString());
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imageFile.getAbsolutePath());
        } catch (IOException e) {
            Log.d("nope ", "nope 1");
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotate = 0;
        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate-=90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate-=90;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate-=90;
        }
        Log.d("rotate = ", Integer.toString(rotate));
        Log.d("orientation = ", Integer.toString(orientation));
/*        bm = myImage;
        Bitmap workingBitmap = Bitmap.createBitmap(bm);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.rotate(rotate);
        getImageDimensions();*/
        //bm = mutableBitmap.createScaledBitmap(mutableBitmap, width, height, true);

    }

    private void scaleAndDisplayBitmap() {
        try {
            myImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //rotateImage();
        getImageDimensions();
        bm = myImage.createScaledBitmap(myImage, width, height, true);
        destroyBitmap();
        imageView.setImageBitmap(bm);
    }

    private void getImageDimensions() {
        width = myImage.getWidth();
        height = myImage.getHeight();
        int maxWidth = imageView.getWidth();
        int maxHeight = imageView.getHeight();
        int ratio = 0;

        if (width > height) {
            // for landscape
            ratio = width / maxWidth;
            width = maxWidth;
            height = height / ratio;
        } else if (height > width) {
            //  for portrait
            ratio = height / maxHeight;
            height = maxHeight;
            width = width / ratio;
        } else {
            // for square images
            height = maxHeight;
            width = maxWidth;
        }
        Log.d("width = " + Integer.toString(width), "height = " + Integer.toString(height)+ "ratio = " + Integer.toString(ratio));
    }

    //stuff was changed
    private void initializeViews(View rootView) {
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        takePic = (ImageButton) rootView.findViewById(R.id.capture_new_pic);
        searchGallery = (ImageButton) rootView.findViewById(R.id.capture_gallery);
        navFileSystem = (ImageButton) rootView.findViewById(R.id.capture_nav);
        savePDF = (ImageButton) rootView.findViewById(R.id.capture_save);
        sharePDF = (ImageButton) rootView.findViewById(R.id.capture_share);
    }

    private class saveImageAsPDF extends AsyncTask {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(), "", "Converting and saving as PDF", false);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            makeSystemwareDir();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bArray = stream.toByteArray();

            try {
                Document document = new Document();
                File f = new File(Environment.getExternalStorageDirectory() + "/Systemware");
                if(!f.isDirectory()){
                    f.mkdir();
                }
                String fileName = Environment.getExternalStorageDirectory() + "/Systemware/sys" + System.currentTimeMillis() + ".pdf";
                File file = new File(fileName);

                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.setPageSize(PageSize.A5);
                document.open();

                addImage(document);
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private void addImage(Document document) {

            try {
                image = com.itextpdf.text.Image.getInstance(bArray);
            } catch (BadElementException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int indentation = 0;
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - indentation) / image.getWidth()) * 100;
            image.scalePercent(scaler);
            try {
                document.add(image);
            } catch (DocumentException e) {
                Log.d("What's going on: ", e.toString());
                e.printStackTrace();
            }
        }

        private void makeSystemwareDir() {
            File directory = new File("/storage/sdcard0/Systemware/");
            try {
                directory.mkdirs();
            } catch (Exception e) {
                Log.d("what's mah problem: ", e.toString());
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            dialog.dismiss();
            imageView.setImageResource(R.drawable.sw_background);
            imageUri = null;
            ToastMessageTask tmtask = new ToastMessageTask(getActivity(), "Your image has been saved.");
            tmtask.execute();
        }
    }

    public void destroyBitmap(){
        myImage.recycle();
        myImage = null;
    }

    public void uploadButton() throws IOException, XmlPullParserException, InterruptedException,
            ExecutionException, TimeoutException {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        LoginLogoff liloobj = new LoginLogoff(getActivity());
        APIQueries apiobj = new APIQueries(getActivity());
        if (apiobj.pingQuery()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Login successful and ready to upload file.");
            //create a topic instance object
            if(imageUri != null) {
                String[] nvpairsarr = new String[NVPAIRS];
                nvpairsarr[0] = "name,"+ descriptionText1.getText().toString();
                apiobj.createtopicQuery(tplid1, nvpairsarr, "y", LoginLogoff.getSid());
            }
            else{
                ToastMessageTask tmtask = new ToastMessageTask(getActivity(),"Error. Fill out all the fields.");
                tmtask.execute();
            }

        }
        else {//if ping fails, selected ci profile will be used to log back in
            Log.d("Message", "Ping to CI server indicated no login session.");
            if(liloobj.tryLogin()) {
                Log.d("Message", "CI Login successful and ready to upload file.");
                if(imageUri != null) {
                    String[] nvpairsarr = new String[NVPAIRS];
                    nvpairsarr[0] = "name,"+ descriptionText1.getText().toString();
                    apiobj.createtopicQuery(tplid1, nvpairsarr, "y", LoginLogoff.getSid());
                }
                else{
                    ToastMessageTask tmtask = new ToastMessageTask(getActivity(),"Error. Fill out all the fields.");
                    tmtask.execute();
                }
            }
            else{//if login attempt fails from trying the CI server profile, prompt user to check profile
                ToastMessageTask tmtask = new ToastMessageTask(getActivity(),"Connection to CI Server failed. Check" +
                        "CI Connection Profile under Settings.");
                tmtask.execute();
            }
        }
    }
}
