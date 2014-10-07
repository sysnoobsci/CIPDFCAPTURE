package com.ameraz.android.cipdfcapture.app.SupportingClasses;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 10/7/2014.
 */
public class ListViewContent {
    public static ArrayList<Version> vVFcontent = null;

    public static ArrayList<Version> getvVFcontent() {
        return vVFcontent;
    }

    public static void setvVFcontent(ArrayList<Version> vVFcontent) {
        ListViewContent.vVFcontent = vVFcontent;
    }
}
