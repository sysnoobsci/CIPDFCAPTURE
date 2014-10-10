package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.ameraz.android.cipdfcapture.app.AsyncTasks.SetGridTask;
import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMsgTask;
import com.ameraz.android.cipdfcapture.app.SupportingClasses.FileUtility;
import com.ameraz.android.cipdfcapture.app.Adapters.GalleryAdapter;
import com.ameraz.android.cipdfcapture.app.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by john.williams on 8/26/2014.
 */
public class Internal_Gallery_Fragment extends Fragment {

    private Context context;
    static GridView gridView;
    static GalleryAdapter ga;
    private ImageButton createNewImage;
    private int width;
    private SharedPreferences pref;
    private Uri fileUri;
    private String fileName;
    private File newImage;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery, container, false);
        setContext(getActivity());
        initializeViews(rootView);
        Log.i("onCreateView()", "Value of width: " + width);
        setColumnWidth();
        SetGridTask sobj = new SetGridTask(getContext(), ga, gridView);
        sobj.execute();
        gridViewListener();
        gridLongClickListener();
        newImageListener();
        return rootView;
    }

    private void gridLongClickListener() {
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Image?");
                builder.setMessage("This image will completely remove the image from your device!")
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                File file = new File(FileUtility.getImageFilePath() + ga.getItem(position));
                                file.delete();
                                ga.removeItem(position);
                                ga.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });
    }

    private void newImageListener() {
        createNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createFile()) {
                    startCamera();
                }
            }
        });
    }

    private void initializeViews(View rootView) {
        ga = new GalleryAdapter(getContext());
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        createNewImage = (ImageButton)rootView.findViewById(R.id.open_camera_button);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        width = rootView.getWidth();
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public void setColumnWidth() {

        int numColumns;
        if (isTablet(getContext())) {
            numColumns = pref.getInt("gallery_preference", 0);
            if (numColumns == 0) {
                numColumns = 6;
            }
        } else {
            numColumns = pref.getInt("gallery_preference", 0);
            if (numColumns == 0) {
                numColumns = 3;
            }
        }
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        width = iDisplayWidth
                / numColumns;
        gridView.setColumnWidth(width);
        ga.setWidth(width);
    }

    void gridViewListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new Image_Upload_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("fileName", ga.getItem(position));
                fragment.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private boolean createFile() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            fileName = "sys_image" + System.currentTimeMillis() + ".jpg";
            String incImage = FileUtility.getImageFilePath() + fileName;
            newImage = new File(incImage);
            try {
                if (!newImage.exists()) {
                    newImage.getParentFile().mkdirs();
                    newImage.createNewFile();
                    fileUri = Uri.fromFile(newImage);
                }
            } catch (IOException e) {
                ToastMsgTask.fileNotWritten(getContext());
                Log.e("File: ", "Could not create file.", e);
                return false;
            }
        } else {
            ToastMsgTask.fileNotWritten(getContext());
            Log.e("File: ", "Storage not mounted.");
            return false;
        }
        return true;
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult ", "checkmark pressed");
            Log.d("onActivityResult ", fileUri.toString());
            Fragment fragment = new Image_Upload_Fragment();
            Bundle bundle = new Bundle();
            bundle.putString("fileName", fileName);
            fragment.setArguments(bundle);
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if(resultCode == Activity.RESULT_CANCELED){
            Log.d("onActivityResult ", "cancel 'X' pressed");
            newImage.delete();
        }
    }


}
