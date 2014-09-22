package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ameraz.android.cipdfcapture.app.AsyncTasks.APITask;
import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMessageTask;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by adrian.meraz on 6/27/2014.
 */
public class APIQueries {
    private static Context context;
    static Boolean actionresult = false;
    static int action_timeout = 5000;//default values in milliseconds of timeouts
    static int lilo_timeout = 5000;
    static int upload_timeout = 30000;

    public APIQueries(Context context) {
        setContext(context);
        setTimeouts();//set timeouts whenever APIQueries object is instantiated
    }

    public static Boolean getActionresult() {
        return actionresult;
    }

    public static void setActionresult(Boolean result) {
        actionresult = result;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    void setTimeouts() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        action_timeout = Integer.parseInt(preferences.getString("actiontimeout_preference", String.valueOf(action_timeout))) * 1000;
        lilo_timeout = Integer.parseInt(preferences.getString("lilotimeout_preference", String.valueOf(lilo_timeout))) * 1000;
        upload_timeout = Integer.parseInt(preferences.getString("uploadtimeout_preference", String.valueOf(upload_timeout))) * 1000;
        Log.d("setTimeouts()", "action_timeout in seconds: " + (double) action_timeout / 1000);
        Log.d("setTimeouts()", "lilo_timeout in seconds: " + (double) lilo_timeout / 1000);
        Log.d("setTimeouts()", "upload_timeout in seconds: " + (double) upload_timeout / 1000);
    }

    public void resetResult() {
        setActionresult(false);
    }

    String targetCIQuery() {
        LogonSession lilobj = new LogonSession(context);
        String targetCIQuery = "http://" + lilobj.getHostname() + "." +
                lilobj.getDomain() + ":" + lilobj.getPortnumber() + "/ci";
        return targetCIQuery;
    }

    //createtopic
    public void createtopicQuery(ArrayList<Object> args) throws IOException, XmlPullParserException, InterruptedException, ExecutionException {

        ArrayList<Object> actionargs = args;
        actionargs.add("act,createtopic");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(), entity, context);
        try {
            apitaskobj.execute().get(action_timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask.noConnectionMessage(getContext());
        }
        Log.d("Variable", "apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        ToastMessageTask.fileUploadStatus(getContext(), getActionresult());
        resetResult();//reset action result after checking it
        QueryArguments.clearList();//clear argslist after query
    }

    //listnode - add &sid to the string for it to work properly
    public String[] listnodeQuery(ArrayList<Object> args) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException {
        ArrayList<Object> actionargs = args;
        actionargs.add("act,listnode");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(), entity, getContext());
        try {
            apitaskobj.execute().get(action_timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask.noConnectionMessage(getContext());
        }
        Log.d("Variable", "apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        if (getActionresult()) {
            Log.d("Message", "CI Server listnode successful.");
        } else {
            Log.d("Message", "CI Server listnode failed.");
        }
        String[] listnodeArray = new String[2];
        listnodeArray[0] = xobj.findTagText("xid");
        listnodeArray[1] = xobj.findTagText("name");
        resetResult();//reset action result after checking it
        QueryArguments.clearList();//clear argslist after query
        return listnodeArray;
    }

    //listversion
    public String listversionQuery(ArrayList<Object> args) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException {

        ArrayList<Object> actionargs = args;
        actionargs.add("act,listversion");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(), entity, getContext());
        try {
            apitaskobj.execute().get(action_timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask.noConnectionMessage(getContext());
        }
        Log.d("listversionQuery()", "apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        if (getActionresult()) {//if the ping is successful(i.e. user logged in)
            Log.d("listversionQuery()", "CI Server listversion successful.");
            resetResult();//reset action result after checking it
            QueryArguments.clearList();//clear argslist after query
            return apitaskobj.getResponse();//return the good results
        } else {
            ToastMessageTask.reportNotValidMessage(getContext());
            Log.d("listversionQuery()", "CI Server listversion failed.");
            resetResult();//reset action result after checking it
            QueryArguments.clearList();//clear argslist after query
            return null;
        }


    }


    //logon
    public Boolean logonQuery(ArrayList<Object> args) throws Exception {
        ArrayList<Object> actionargs = args;
        actionargs.add("act,logon");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(), entity, getContext());
        try {
            apitaskobj.execute().get(lilo_timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            ToastMessageTask.noConnectionMessage(getContext());
            e.printStackTrace();
        }
        Log.d("logonQuery()", "apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        Boolean logonStatus = getActionresult();
        if (logonStatus) {//if the ping is successful(i.e. user logged in)
            LogonSession.setSid(apitaskobj.getResponse());
            Log.d("logonQuery()", "CI Server logon successful.");
        } else {
            Log.d("logonQuery()", "CI Server logon failed.");
        }
        resetResult();//reset action result after checking it
        QueryArguments.clearList();//clear argslist after query
        return logonStatus;
    }

    //logoff
    public Boolean logoffQuery(ArrayList<Object> args) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException {

        ArrayList<Object> actionargs = args;
        actionargs.add("act,logoff");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(), entity, getContext());
        try {
            apitaskobj.execute().get(lilo_timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask.noConnectionMessage(getContext());
        }
        Log.d("logoffQuery()", "apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        Boolean logoffStatus = getActionresult();
        LogonSession.logoffMessage(getActionresult(), getContext());//show status of logon action
        if (logoffStatus) {//if the ping is successful(i.e. user logged in)
            Log.d("logoffQuery()", "CI Server logoff successful.");
        } else {
            Log.d("logoffQuery()", "CI Server logoff failed.");
        }
        resetResult();//reset action result after checking it
        QueryArguments.clearList();//clear argslist after query
        return logoffStatus;
    }

    //ping
    public Boolean pingQuery() throws ExecutionException, InterruptedException, IOException, XmlPullParserException {//pings the CI server, returns true if ping successful
        if (!LogonSession.doesSidExist()) {//check if there is an sid (i.e. a session established)
            Log.d("pingQuery()", "Empty sid found. Need to login");
            return false;//if no session established, return false
        }
        QueryArguments.addArg("act,ping");
        QueryArguments.addArg("sid," + LogonSession.getSid());
        HttpEntity entity = mebBuilder(QueryArguments.getArgslist());
        APITask apitaskobj = new APITask(targetCIQuery(), entity, getContext());
        try {
            apitaskobj.execute().get(action_timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("pingQuery()", "apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        if (getActionresult()) {//if the ping is successful(i.e. user logged in)
            Log.d("pingQuery()", "CI Server ping successful.");
            resetResult();//reset action result after checking it
            QueryArguments.clearList();//clear argslist after query
            return true;
        } else {
            Log.d("pingQuery()", "CI Server ping failed.");
            resetResult();//reset action result after checking it
            QueryArguments.clearList();//clear argslist after query
            return false;
        }
    }

    //retrieve
    public String retrieveQuery(String tid) {//pings the CI server, returns true if ping successful
        String retrieveQuery = targetCIQuery() + "?act=retrieve&tid=" + tid + "&sid=" + LogonSession.getSid();
        return retrieveQuery;
    }

    //build MultiPartEntity after checking type of the args
    HttpEntity mebBuilder(ArrayList<Object> args) throws UnsupportedEncodingException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (Object larg : args) {//check each argument for class type and act accordingly
            if (larg != null) {//make sure arg isn't null
                Log.d("mebBuilder()", "larg.getClass() value: " + larg.getClass());
                if (larg.getClass().equals(String.class)) {//if type of arg is String, do this
                    int i = 0;
                    int j = 1;
                    String[] parts = larg.toString().split(",");
                    while (j < parts.length) {//allows for multiple key-value pairs
                        builder.addPart(parts[i], new StringBody(parts[j]));
                        i += 2;
                        j += 2;
                    }
                }
                if (larg.getClass().equals(File.class)) {//if type of arg is File, do this
                    builder.addPart("file", new FileBody((File) larg));
                }
                //if (larg.getClass().getName().startsWith("android.net.Uri$HierarchicalUri") || larg.getClass().getName().equals("android.net.Uri$StringUri")) {//if type of arg is Uri, do this
                if (larg.getClass().getName().startsWith("android.net.Uri$")) {//if type of arg is Uri, do this
                    Uri imageUri = (Uri) larg;
                    File newImage = new File(imageUri.getPath());
                    Log.d("Variable", "imageUri.getPath().toString() value: " + imageUri.getPath());
                    builder.addPart("file", new FileBody(newImage));
                }
            }
        }
        HttpEntity entity = builder.build();
        return entity;
    }

    public ArrayList<String> getVersionInfo(String xmlResponse) throws IOException, XmlPullParserException {
        XmlParser xobj = new XmlParser(xmlResponse);
        ArrayList<String> versionInfo = new ArrayList<String>();
        String path = xobj.findTagText("path");//get the path name
        String xid = xobj.findTagText("xid");//get the xid
        String dsids = xobj.findTagText("dsid");//get the DSIDs
        String cts = xobj.findTagText("cts");//get the DSIDs
        String bytes = xobj.findTagText("bytes");//get the bytes
        String fmt = xobj.findTagText("fmt");//get the format
        String ver = xobj.findTagText("v");//get the version number
        String[] pathsarr = path.split(",");//arrays should all be the same size
        String[] xidarr = xid.split(",");
        String[] dsidsarr = dsids.split(",");
        String[] ctsarr = cts.split(",");
        String[] bytesarr = bytes.split(",");
        String[] fmtarr = fmt.split(",");
        String[] verarr = ver.split(",");

        for (int i = 0; i < dsidsarr.length; i++) {
            StringBuilder sbuild = new StringBuilder();
            sbuild.append(dsidsarr[i] + ",").append(ctsarr[i] + ",").append(bytesarr[i] + ",").append(fmtarr[i] + ",")
                    .append(verarr[i] + ",").append("V~" + xidarr[i] + "~" + dsidsarr[i] + "~" + pathsarr[i] +
                    "~" + verarr[i]);
            Log.d("getVersionInfo()", "version " + verarr[i] + " attributes: " + sbuild.toString());
            versionInfo.add(sbuild.toString());
        }
        return versionInfo;
    }

    public static ArrayList<String> getMetadata(ArrayList<String> lvers, String selection) {
        int choose = -1;//selects the version info to grab
        if (selection == "DSID") {//dsid of report version
            choose = 0;
        } else if (selection == "CTS") {//timestamp when report version was created
            choose = 1;
        } else if (selection == "BYTES") {//size in bytes of report version
            choose = 2;
        } else if (selection == "FMT") {//format of report version
            choose = 3;
        } else if (selection == "VER") {//version number of report version
            choose = 4;
        } else if (selection == "TID") {//topic instance id of report version
            choose = 5;
        }
        if (choose == -1) {
            Log.d("getMetadata()", "Error. Invalid Metadata Tag.");
        }
        ArrayList<String> vers = new ArrayList<String>();
        for (String v : lvers) {
            String[] pieces = v.split(",");
            vers.add(pieces[choose]);//0=dsid,1=cts,2=bytes,3=fmt,4=ver,5=tid
        }
        return vers;
    }


    //action return code check
    protected void isActionSuccessful(ArrayList<String> larray) {
        if (larray.size() == 0) {//if the array is of size 0, nothing was returned from the ciserver
            Log.d("isActionSuccessful()", "Nothing returned from CI server.");
            setActionresult(false);
        } else {
            if ((larray.get(0).equals("0") || larray.get(0).equals("7")) && larray.get(1).equals("0") && larray.get(2).equals("0")) {
                setActionresult(true);
            } else {
                setActionresult(false);
            }
        }
    }


}
