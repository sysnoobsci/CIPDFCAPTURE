package com.ameraz.android.cipdfcapture.app;

import java.io.IOException;

/**
 * Created by adrian.meraz on 7/28/2014.
 */
public class TopicInstance {
    String rname;
    String filename;

    public TopicInstance(String rname, String filename) throws IOException {
        this.rname = rname;
        this.filename = filename;
        LoadFile.readFile(getFilename());//read in file when topic instance is created
        //continue code
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


}
