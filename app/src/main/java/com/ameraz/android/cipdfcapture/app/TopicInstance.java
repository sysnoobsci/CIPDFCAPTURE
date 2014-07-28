package com.ameraz.android.cipdfcapture.app;

/**
 * Created by adrian.meraz on 7/28/2014.
 */
public class TopicInstance {
    String filename;
    String csnode;
    String rname;
    String rpath;
    String datecreated;

    public TopicInstance(String filename, String csnode, String rname, String rpath, String datecreated) {
        this.filename = filename;
        this.csnode = csnode;
        this.rname = rname;
        this.rpath = rpath;
        this.datecreated = datecreated;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCsnode() {
        return csnode;
    }

    public void setCsnode(String csnode) {
        this.csnode = csnode;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getRpath() {
        return rpath;
    }

    public void setRpath(String rpath) {
        this.rpath = rpath;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }
}
