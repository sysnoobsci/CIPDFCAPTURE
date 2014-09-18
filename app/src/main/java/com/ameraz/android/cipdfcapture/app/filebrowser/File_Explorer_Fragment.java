package com.ameraz.android.cipdfcapture.app.filebrowser;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.fragments.Image_Upload_Fragment;
import com.ameraz.android.cipdfcapture.app.fragments.PDF_Upload_Fragment;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by john.williams on 9/17/2014.
 *
 */
public class File_Explorer_Fragment extends ListFragment {


    private File currentDir;
    private FileArrayAdapter adapter;
    private static String fullFilePath;
    private static String fileName;
    private static Uri itemURI;
    private Context context;

    public Context getContext() {
        return context;
    }
    public void setContext(Activity activity){
        context = activity;
    }

    public static String getFullFilePath() {
        return fullFilePath;
    }

    public void setFullFilePath(String fullFilePath) {
        this.fullFilePath = fullFilePath;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {
        File_Explorer_Fragment.fileName = fileName;
    }

    public static Uri getItemURI() {
        return itemURI;
    }

    public static void setItemURI(Uri itemURI) {
        File_Explorer_Fragment.itemURI = itemURI;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContext(getActivity());
        currentDir = new File(Environment.getExternalStorageDirectory().getPath());
        fill(currentDir);
    }

    private void fill(File f) {
        File[] dirs = f.listFiles();
        //this.setTitle("Current Dir: " + f.getName());
        List<Item> dir = new ArrayList<Item>();
        List<Item> fls = new ArrayList<Item>();
        try {
            for (File ff : dirs) {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                if (ff.isDirectory()) {

                    File[] fbuf = ff.listFiles();
                    int buf;
                    if (fbuf != null) {
                        buf = fbuf.length;
                    } else buf = 0;
                    String num_item = String.valueOf(buf);
                    if (buf == 0) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    //String formated = lastModDate.toString();
                    dir.add(new Item(ff.getName(), num_item, date_modify, ff.getAbsolutePath(), "directory_icon"));
                } else {
                    fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "file_icon"));
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if (!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0, new Item("..", "Parent Directory", "", f.getParent(), "directory_up"));
        adapter = new FileArrayAdapter(getContext(), R.layout.file_view, dir);
        this.setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Item o = adapter.getItem(position);
        if (o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up")) {
            currentDir = new File(o.getPath());
            Log.d("Message", " Directory path is " + o.getPath());
            fill(currentDir);
        } else {
            onFileClick(o);
        }
    }

    private void onFileClick(Item o) {
        //Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        if(o.getName().endsWith(".jpg") || o.getName().endsWith(".jpeg") || o.getName().endsWith(".png")){
            setImageFragment(o);
        }
        if(o.getName().endsWith(".pdf")){
            setPDFFragment(o);
        }

/*        Intent intent = new Intent();
        intent.putExtra("GetPath", currentDir.toString());
        intent.putExtra("GetFileName", o.getName());
        setFileName(o.getName());//setting the file name
        setFullFilePath(currentDir.toString() + "/" + o.getName());//setting the full file path
        Log.d("Message", "File path is " + currentDir.toString() + "/" + o.getName());
        File newImage = new File(getFullFilePath());
        setItemURI(Uri.fromFile(newImage));
        setResult(RESULT_OK, intent);
        finish();*/
    }

    private void setPDFFragment(Item o) {
        Fragment fragment = new PDF_Upload_Fragment();
        sendFile(fragment, o);
    }

    private void setImageFragment(Item o) {
        Fragment fragment = new Image_Upload_Fragment();
        sendFile(fragment, o);
    }

    private void sendFile(Fragment fragment, Item o) {
        Bundle bundle = new Bundle();
        bundle.putString("stringUri", "file://"+o.getPath());
        fragment.setArguments(bundle);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
