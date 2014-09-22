package com.ameraz.android.cipdfcapture.app;

import android.os.Environment;

/**
 * Created by john.williams on 8/26/2014.
 */
public class FilePath {
    static String root = Environment.getExternalStorageDirectory().toString();
    public static final String SYSWARE_FILEPATH = root + "/Systemware/";
    public static final String IMAGE_FILEPATH = root + "/Systemware/Images/";
    public static final String PDF_FILEPATH = root + "/Systemware/PDF/";
    public static final String TEMP_FILEPATH = root + "/Systemware/Temp/";

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

    public static String getTempFilePath() {
        return TEMP_FILEPATH;
    }
}
