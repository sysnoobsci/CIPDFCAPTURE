package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by john.williams on 8/26/2014.
 */
public class GalleryAdapter extends BaseAdapter {
    private Context context;
    private File FILE_DIR;
    String[] names;
    FilePath fp;
    int width;

    public GalleryAdapter(Context context) {
        setContext(context);
    }

    public void setUriArray() {
        fp = new FilePath();
        FILE_DIR = new File(fp.getImageFilePath());
        Log.d("File name: ", FILE_DIR.toString());
        names = FILE_DIR.list(
                new FilenameFilter() {
                    public boolean accept(File FILE_DIR, String name) {
                        if (name.endsWith(".jpg")) {
                            return name.endsWith(".jpg");
                        }
                        if (name.endsWith(".jpeg")) {
                            return name.endsWith(".jpeg");
                        }
                        if (name.endsWith(".png")) {
                            return name.endsWith(".png");
                        }
                        if (name.endsWith(".gif")) {
                            return name.endsWith(".gif");
                        }
                        if(name.endsWith(".TIF")){
                            return name.endsWith(".TIF");
                        }
                        return false;
                    }
                });
    }

    public void setWidth(int maWidth) {
        this.width = maWidth;
    }

    public String getNames(Integer position) {
        return names[position];
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("position: ", Integer.toString(position));
        Log.d("Loading images...", Uri.parse("file://" + fp.getImageFilePath() + names[position]).toString());
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater in = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = in.inflate(R.layout.thumbnail, null);
            imageView = (ImageView) convertView.findViewById(R.id.thumbnail_image);
        } else {
            imageView = (ImageView) convertView;
        }
        Log.d("Loading images...", Uri.parse("file://" + fp.getImageFilePath() + names[position]).toString());
        Picasso.with(getContext())
                .load(Uri.parse("file://" + fp.getImageFilePath() + names[position]))
                .resize(width, width)
                .placeholder(R.drawable.sw_placeholder)
                .centerCrop()
                .into(imageView);

        return imageView;
    }
}
