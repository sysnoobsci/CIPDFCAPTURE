package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ameraz.android.cipdfcapture.app.filebrowser.FileChooser;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

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
    private Uri imageUri;
    private String incImage;
    private File newImage;
    private Bitmap myImage;
    private Bitmap bm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capture_fragment, container, false);
        initializeViews(rootView);
        takePicButtonListener();
        searchGalleryListener();
        navFileSystemListener();
        savePDFListener();
        sharePDFListener();

        return rootView;
    }

    private void sharePDFListener() {

    }

    private void savePDFListener() {
        savePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView == null || imageUri == null) {
                    ToastMessageTask tmtask = new ToastMessageTask(getActivity(), "Nothing to save.");
                    tmtask.execute();
                } else {
/*                    try {
                        myImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        bm = myImage.createScaledBitmap(myImage, 1080, 1920, true);
                        //destroyBitmap();
                        imageView.setImageBitmap(bm);
                    } catch (Exception e) {
                        Log.d("Error: ", e.toString());
                    }*/
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
                        if (newImage.exists() == false) {
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
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 0:

                    try {
                        myImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        bm = myImage.createScaledBitmap(myImage, 1080, 1920, true);
                        destroyBitmap();
                        imageView.setImageBitmap(bm);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:

                    imageUri = data.getData();
                    try {
                        myImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        bm = myImage.createScaledBitmap(myImage, 1080, 1920, true);
                        destroyBitmap();
                        imageView.setImageBitmap(bm);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:

                    String loc = "file://" + data.getStringExtra("GetPath") + "/" + data.getStringExtra("GetFileName");
                    imageUri = Uri.parse(loc);
                    try {
                        //myImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        bm = myImage.createScaledBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri), 1080, 1920, true);
                        destroyBitmap();
                        imageView.setImageBitmap(bm);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
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
}
