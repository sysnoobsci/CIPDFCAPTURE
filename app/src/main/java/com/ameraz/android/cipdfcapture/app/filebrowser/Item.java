package com.ameraz.android.cipdfcapture.app.filebrowser;

import android.util.Log;

public class Item{
	private String name;
	private String date;
	private String path;
	private int image;
    private boolean newPathNeeded;
	
	public Item(String name, String date, String path, int image, boolean newPathNeeded)
	{
		this.name = name;
		this.date = date;
		this.path = path;
		this.image = image;
        this.newPathNeeded = newPathNeeded;

        Log.d("file name = ", name);
	}
	public String getName(){
		return name;
	}
	public String getDate(){
		return date;
	}
	public String getPath(){
		return path;
	}
	public int getImage() {
		return image;
	}
    public boolean getNeedsNewPath(){
        return newPathNeeded;
    }
}
