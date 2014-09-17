package com.ameraz.android.cipdfcapture.app;

/**
 * Created by john.williams on 8/26/2014.
 */
public class FilePath {
    public static final String IMAGE_FILEPATH = "/storage/sdcard0/Systemware/Images/";
    public static final String PDF_FILEPATH = "/storage/sdcard0/Systemware/PDF/";
    public static final String TEMP_FILEPATH = "/storage/sdcard0/Systemware/Temp/";

    public String getImageFilePath() {
        return IMAGE_FILEPATH;
    }
    public String getPDFFilePath() {
        return PDF_FILEPATH;
    }
    public String getTempFilePath() {
        return TEMP_FILEPATH;
    }
}
