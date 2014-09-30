package com.ameraz.android.cipdfcapture.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by john.williams on 9/26/2014.
 */
public class Version{
    String date;
    String type;
    String versionNum;

    public Version(String date, String type, String versionNum){
        this.date = date;
        this.type = type;
        this.versionNum = versionNum;
    }

    public String getDate(){
        return date;
    }

    public String getType(){
        return type;
    }

    public String getVersionNum(){
        return versionNum;
    }

}
