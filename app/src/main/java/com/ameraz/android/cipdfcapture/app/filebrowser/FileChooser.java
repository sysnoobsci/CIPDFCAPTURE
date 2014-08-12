package com.ameraz.android.cipdfcapture.app.filebrowser;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.ameraz.android.cipdfcapture.app.R;

import java.io.File;
import java.net.URI;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileChooser extends ListActivity {

	private File currentDir;
    private FileArrayAdapter adapter;
    private static String fullFilePath;
    private static String fileName;
    private static Uri itemURI;

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
        FileChooser.fileName = fileName;
    }

    public static Uri getItemURI() {
        return itemURI;
    }

    public static void setItemURI(Uri itemURI) {
        FileChooser.itemURI = itemURI;
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        currentDir = new File(Environment.getExternalStorageDirectory().getPath());
        Log.d("Variable","Value of currentDir after being set by " +
                "Environment.getExternalStorageDirectory().getPath():" + currentDir.toString());
        fill(currentDir); 
    }
    private void fill(File f)
    {
    	File[]dirs = f.listFiles(); 
		 this.setTitle("Current Dir: "+f.getName());
		 List<Item>dir = new ArrayList<Item>();
		 List<Item>fls = new ArrayList<Item>();
		 try{
			 for(File ff: dirs)
			 { 
				Date lastModDate = new Date(ff.lastModified()); 
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if(ff.isDirectory()){

					File[] fbuf = ff.listFiles(); 
					int buf;
					if(fbuf != null){ 
						buf = fbuf.length;
					} 
					else buf = 0;
                    String num_item = String.valueOf(buf);
					if(buf == 0) num_item = num_item + " item";
					else num_item = num_item + " items";
					
					//String formated = lastModDate.toString();
					dir.add(new Item(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon")); 
				}
				else
				{
					fls.add(new Item(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_icon"));
				}
			 }
		 }catch(Exception e)
		 {    
			 Log.e("Error",e.toString());
		 }
		 Collections.sort(dir);
		 Collections.sort(fls);
		 dir.addAll(fls);
		 if(!f.getName().equalsIgnoreCase("sdcard"))
			 dir.add(0,new Item("..","Parent Directory","",f.getParent(),"directory_up"));
		 adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,dir);
		 this.setListAdapter(adapter); 
    }
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Item o = adapter.getItem(position);
		if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
				currentDir = new File(o.getPath());
                Log.d("Message", " Directory path is " + o.getPath());
				fill(currentDir);
		}
		else
		{
			onFileClick(o);
		}
	}
    private void onFileClick(Item o)
    {
    	//Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent();
        intent.putExtra("GetPath",currentDir.toString());
        intent.putExtra("GetFileName",o.getName());
        setFileName(o.getName());//setting the file name
        setFullFilePath(currentDir.toString() + "/" + o.getName());//setting the full file path
        Log.d("Message", "File path is " + currentDir.toString() + "/" + o.getName());
        File newImage = new File(getFullFilePath());
        setItemURI(Uri.fromFile(newImage));
        setResult(RESULT_OK, intent);
        finish();
    }
}
