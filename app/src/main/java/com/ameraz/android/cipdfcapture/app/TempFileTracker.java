package com.ameraz.android.cipdfcapture.app;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 9/23/2014.
 */
public class TempFileTracker {
    static ArrayList<String> tempFileInfo;
    StringBuilder fullFileInfo;

    public static void addTempFileToList(String filePath, int version) {//add temp files and versions
        tempFileInfo = new ArrayList<String>();
/*        fullFileInfo = new StringBuilder();
        fullFileInfo.append(filePath).append(",").append(version);
        tempFileInfo.add(String.valueOf(fullFileInfo));
        fullFileInfo.setLength(0);//empty the StringBuilder buffer*/
    }

    public ArrayList<String> getTempFileInfo() {
        return tempFileInfo;
    }
}
