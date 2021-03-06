package com.ameraz.android.cipdfcapture.app.SupportingClasses;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by john.williams on 8/26/2014.
 */
public class FileUtility {
    static String root = Environment.getExternalStorageDirectory().toString();
    public static final String SYSWARE_FILEPATH = root + "/Systemware/";
    public static final String IMAGE_FILEPATH = root + "/Systemware/Images/";
    public static final String PDF_FILEPATH = root + "/Systemware/PDF/";
    public static final String TXT_FILEPATH = root + "/Systemware/TXT/";
    public static final String TEMP_FILEPATH = root + "/Systemware/Temp/";
    static ArrayList<File> dirArray = new ArrayList<File>();

    public static String getRootPath() {
        return root;
    }

    public static String getSyswareFilePath() {
        return SYSWARE_FILEPATH;
    }

    public static String getImageFilePath() {
        return IMAGE_FILEPATH;
    }

    public static String getPDFFilePath() {
        return PDF_FILEPATH;
    }

    public static String getTxtFilePath() {
        return TXT_FILEPATH;
    }

    public static String getTempFilePath() {
        return TEMP_FILEPATH;
    }

    public static Boolean doesFileExist(String fullFilePath){
        File file = new File(fullFilePath);
        return file.exists();
    }

    public static void directoryCheck(){
        dirArray.add(new File(getImageFilePath()));
        dirArray.add(new File(getPDFFilePath()));
        dirArray.add(new File(getTxtFilePath()));
        dirArray.add(new File(getTempFilePath()));
        for(File path : dirArray){
            if(!path.exists()){//if dir doesn't exist, create it
                path.mkdir();
                Log.d("directoryCheck()","Directory: " + path.getPath() + " created.");
            }
        }
        dirArray.clear();
    }

    public static String chooseDownloadFilePath(String versionFormat) {
        String fp;
        if (versionFormat.equals("PDF")) {
            fp = getPDFFilePath();
        } else if (versionFormat.equals("XML") || versionFormat.equals("TXT") || versionFormat.equals("ASC")) {
            fp = getTxtFilePath();
        } else {
            fp = getImageFilePath();
        }
        return fp;
    }

    public static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }
}
