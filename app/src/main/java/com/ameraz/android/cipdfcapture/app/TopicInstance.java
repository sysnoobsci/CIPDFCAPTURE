package com.ameraz.android.cipdfcapture.app;

import java.io.IOException;

/**
 * Created by adrian.meraz on 7/28/2014.
 */
public class TopicInstance {
    String rname;
    String filename;
    String filebufferstring;

    public TopicInstance(String rname, String filename) throws IOException {
        this.rname = rname;
        this.filename = filename;
        setFilebuffer(LoadFile.readFile(getFilename()));//read in file when topic instance is created
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

    public String getFilebuffer() {
        return filebufferstring;
    }

    public void setFilebuffer(byte[] filebuffer) {//convert the byte array to a binary string representation of the bytes
        StringBuilder s1 = new StringBuilder();
        for(byte b1: filebuffer){
            s1.append(String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0'));
        }
        this.filebufferstring = s1.toString();
    }
}
