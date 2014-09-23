package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ameraz.android.cipdfcapture.app.R;
import com.ameraz.android.cipdfcapture.app.filebrowser.CustomArrayAdapter;
import com.ameraz.android.cipdfcapture.app.filebrowser.Item;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by john.williams on 9/17/2014.
 */
public class File_Explorer_Fragment extends Fragment {


    private File currentDir;
    private CustomArrayAdapter adapter;
    private Context context;
    private ListView browser;
    private ArrayList<Item> content;

    public Context getContext() {
        return context;
    }

    public void setContext(Activity activity) {
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.file_explorer_list, container, false);
        setContext(getActivity());
        browser = (ListView)rootView.findViewById(R.id.file_explorer);
        currentDir = new File(Environment.getExternalStorageDirectory().getPath());
        setArrayList(currentDir);
        setListAdapter();
        setListListener();
        return rootView;
    }

    private void setArrayList(File f) {
        File[] dirs = f.listFiles();
        content = new ArrayList<Item>();
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
                    Item item = new Item(ff.getName(), num_item, date_modify, ff.getAbsolutePath(), R.drawable.directory_icon, true);
                    content.add(item);
                }
            }
            for (File ff : dirs) {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                if (!ff.isDirectory()) {

                    File[] fbuf = ff.listFiles();
                    int buf;
                    if (fbuf != null) {
                        buf = fbuf.length;
                    } else buf = 0;
                    String num_item = String.valueOf(buf);
                    if (buf == 0) num_item = num_item + " item";
                    else num_item = num_item + " items";
                    Item item = new Item(ff.getName(), num_item, date_modify, ff.getAbsolutePath(), R.drawable.directory_icon, true);
                    content.add(item);
                }
            }

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }

        if (!f.getName().equalsIgnoreCase("sdcard")) {
            Item item = new Item("..", "Parent Directory", "", f.getParent(), R.drawable.directory_up, true);
            content.add(item);
        }
    }

    private void setCodeAdapter() {
        Log.d("Setting adapter","");
        adapter = new CustomArrayAdapter(getContext(), content);
        Log.d("Adapter set","");

    }

    private void setListListener() {
        browser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
/*                Item o = adapter.getItem(position);
                if (o.getNeedsNewPath()) {
                    currentDir = new File(o.getPath());
                    Log.d("Message", " Directory path is " + o.getPath());
                    setListAdapter(currentDir);
                } else {
                    onFileClick(o);
                }
            }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setListAdapter() {
        setCodeAdapter();
        browser.setAdapter(adapter);
        Log.d("Adapter set for the browser", "");
    }

    private void onFileClick(Item o) {
        //Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        if (o.getName().endsWith(".jpg") || o.getName().endsWith(".jpeg") || o.getName().endsWith(".png")) {
            setImageFragment(o);
        }
        else if (o.getName().endsWith(".pdf")) {
            setPDFFragment(o);
        }
        else if(o.getName().endsWith(".txt") || o.getName().endsWith(".xml")){
            setTXTXMLFragment(o);
        }
    }

    private void setTXTXMLFragment(Item o) {
        Fragment fragment = new Text_XML_Upload_Fragment();
        sendFile(fragment, o);
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
        bundle.putString("stringUri", "file://" + o.getPath());
        fragment.setArguments(bundle);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
