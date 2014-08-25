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
    Context maContext;

    SharedPreferences preferences;

    final static private int NVPAIRS = 1;//number of nvpairs in createtopic api call
    final static private String tplid1 = "create.phonecapture";//time in milliseconds for createtopic attempt to timeout

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capture_fragment, container, false);
        descriptionText1 = (EditText) rootView.findViewById(R.id.description_text);
        initializeViews(rootView);
        takePicButtonListener();
        sharePDFListener();
        imageViewListener();
        bm=null;
        maContext = getActivity();

        return rootView;
    }

    private void imageViewListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri!=null){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
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
                default:
                    break;
            }
        }
    }

    private void rotateImage(int rotate) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        getImageDimensions();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(myImage, width, height, true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
        imageView.setImageBitmap(rotatedBitmap);

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

    private void scaleAndDisplayBitmap() {
        try {
            myImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        rotateImage(getImageRotation());
        destroyBitmap();
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
        Log.d("width = " + Integer.toString(width), "height = " + Integer.toString(height) + "ratio = " + Integer.toString(ratio));
    }

    //stuff was changed
    private void initializeViews(View rootView) {
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        takePic = (ImageButton) rootView.findViewById(R.id.capture_new_pic);
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
        final APIQueries apiobj = new APIQueries(getActivity());
        final ProgressDialog ringProgressDialog = ProgressDialog.show(maContext, "Performing Action ...",
                "Uploading file ...", true);
        if (apiobj.pingQuery()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Login successful and ready to upload file.");
            //create a topic instance object
            if(imageUri != null || !descriptionText1.getText().toString().isEmpty()) {
                final String[] nvpairsarr = new String[NVPAIRS];
                nvpairsarr[0] = "name,"+ descriptionText1.getText().toString();
                new Thread() {
                    public void run() {
                        try {
                            apiobj.createtopicQuery(tplid1, nvpairsarr, "y", LoginLogoff.getSid(), imageUri);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        ringProgressDialog.dismiss();
                    }
                }.start();
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
                if(imageUri != null || !descriptionText1.getText().toString().isEmpty()) {
                    final String[] nvpairsarr = new String[NVPAIRS];
                    nvpairsarr[0] = "name,"+ descriptionText1.getText().toString();
                    new Thread() {
                        public void run() {
                            try {
                                apiobj.createtopicQuery(tplid1, nvpairsarr, "y", LoginLogoff.getSid(), imageUri);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                            ringProgressDialog.dismiss();
                        }
                    }.start();
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

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(maContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
