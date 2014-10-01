package com.ameraz.android.cipdfcapture.app;

import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adrian.meraz on 9/23/2014.
 */
public class TempFileTracker {
    static Map<Integer, String> tempFileInfo = new HashMap<Integer, String>();
    static StringBuilder fullFileInfo;

    public static void addTempFileToList(String filePath, int version) {//add temp files and versions
        tempFileInfo.put(version, filePath);//version number is the key, filepath is the value
        Log.d("addTempFileToList()","file " + filePath + " added to the list");
    }

    public Map getTempFileInfo() {
        return tempFileInfo;
    }

    public static String getTempFilePath(int version){
        String tempFilePath = tempFileInfo.get(version);
        return tempFilePath;
    }

    public static void clearTempFiles(){
        File dir = new File(FileUtility.getTempFilePath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
                Log.d("clearTempFiles()","Temp file " + children[i] + " has been deleted.");
            }
        }
    }


    public static Boolean isTempFileCached(String fullFilePathName, int versionNumber){
        //check if file is even a temp file first
        String tempFilePath = TempFileTracker.getTempFilePath(versionNumber);
        Log.d("isTempFileCached()", "tempFilePath value: " + tempFilePath);
        if(tempFilePath != null){//if a filepath is returned, file is already cached
            return true;
        }
        else{
            TempFileTracker.addTempFileToList(fullFilePathName, VersionInfo.getVersion());//add temp file and version number to temp file list
            return false;
        }
    }
}
