package com.ameraz.android.cipdfcapture.app.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ameraz.android.cipdfcapture.app.FilePath;
import com.ameraz.android.cipdfcapture.app.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by john.williams on 8/26/2014.
 */
public class GalleryAdapter extends BaseAdapter {
    private Context context;
    private File FILE_DIR;
    private ArrayList<String> names = new ArrayList<String>();
    int width;

    public GalleryAdapter(Context context) {
        setContext(context);
    }

    public void setUriArray() {
        FILE_DIR = new File(FilePath.getImageFilePath());
        Log.d("File name: ", FILE_DIR.toString());
        Log.d("setUriArray()","Does FILE_DIR exist: " + FILE_DIR.exists());
        Log.d("setUriArray()","Value of names: " + names);
        FILE_DIR.list(
                new FilenameFilter() {
                    public boolean accept(File FILE_DIR, String name) {
                        if (name.endsWith(".jpg")) {
                            names.add(name);
                            //return name.endsWith(".jpg");
                        }
                        if (name.endsWith(".jpeg")) {
                            names.add(name);
                            //return name.endsWith(".jpeg");
                        }
                        if (name.endsWith(".png")) {
                            names.add(name);
                            //return name.endsWith(".png");
                        }
                        if (name.endsWith(".gif")) {
                            names.add(name);
                            //return name.endsWith(".gif");
                        }
                        return false;
                    }
                });
    }

    public void setWidth(int maWidth) {
        this.width = maWidth;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public void removeItem(int position){
        names.remove(position);
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public String getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("position: ", Integer.toString(position));
        Log.d("Loading images...", Uri.parse("file://" + FilePath.getImageFilePath() + names.get(position)).toString());
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater in = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = in.inflate(R.layout.thumbnail, null);
            imageView = (ImageView) convertView.findViewById(R.id.thumbnail_image);
        } else {
            imageView = (ImageView) convertView;
        }
        Log.d("Loading images...", Uri.parse("file://" + FilePath.getImageFilePath() + names.get(position)).toString());
        Picasso.with(getContext())
                .load(Uri.parse("file://" + FilePath.getImageFilePath() + names.get(position)))
                .resize(width, width)
                .centerCrop()
                .into(imageView);

        return imageView;
    }
}