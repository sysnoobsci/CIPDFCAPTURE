package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

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
    Context mContext;
    static Boolean actionresult = false;

    public APIQueries(Context mContext){
        setmContext(mContext);
    }

    public static Boolean getActionresult() {
        return actionresult;
    }

    public static void setActionresult(Boolean result) {
        actionresult = result;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void resetResult(){
        setActionresult(false);
    }

    String targetCIQuery(){
        LoginLogoff lilobj = new LoginLogoff(mContext);
        String targetCIQuery = "http://" + lilobj.getHostname() + "." +
                lilobj.getDomain() + ":" + lilobj.getPortnumber() + "/ci";
        return targetCIQuery;
    }
    //createtopic
    void createtopicQuery(ArrayList<Object> args) throws IOException, XmlPullParserException, InterruptedException, ExecutionException {

        ArrayList<Object> actionargs = args;
        actionargs.add("act,createtopic");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(),entity,mContext);
        try {
            apitaskobj.execute().get(MainActivity.getAction_timeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Create topic call failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }
        Log.d("Variable","apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        if(getActionresult()){//if return codes are good, it was successful
           Log.d("Message","File was successfully Uploaded.");
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(),"File was successfully Uploaded.");
            tmtask.execute();
        }
        else{
            Log.d("Message","File upload failed.");
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(),"File upload failed.");
            tmtask.execute();
        }
        resetResult();//reset action result after checking it
        MainActivity.argslist.clear();//clear argslist after query
    }

    //listnode - add &sid to the string for it to work properly
    String[] listnodeQuery (ArrayList<Object> args) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException{
        ArrayList<Object> actionargs = args;
        ArrayList<String> nodes = new ArrayList<String>();
        actionargs.add("act,listnode");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(),entity,getmContext());
        try {
            apitaskobj.execute().get(MainActivity.getAction_timeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Login failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }
        Log.d("Variable","apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        LoginLogoff.logonMessage(getActionresult(), getmContext());//show status of logon action
        if (getActionresult()) {//if the ping is successful(i.e. user logged in)
            LoginLogoff.setSid(apitaskobj.getResponse());
            Log.d("Message", "CI Server listnode successful.");
        }
        else{
            Log.d("Message", "CI Server listnode failed.");
        }
        String[] listnodeArray = new String[2];
        listnodeArray[0] =  xobj.findTagText("xid");
        listnodeArray[1] = xobj.findTagText("name");
        resetResult();//reset action result after checking it
        MainActivity.argslist.clear();//clear argslist after query
        return listnodeArray;
    }
    //listversion
    String listversionQuery(ArrayList<Object> args) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException {

        ArrayList<Object> actionargs = args;
        actionargs.add("act,listversion");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(),entity,getmContext());
        try {
            apitaskobj.execute().get(MainActivity.getAction_timeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Login failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }
        Log.d("Variable","apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        if (getActionresult()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Server listversion successful.");
        }
        else{
            Log.d("Message", "CI Server listversion failed.");
        }
        resetResult();//reset action result after checking it
        MainActivity.argslist.clear();//clear argslist after query
        return apitaskobj.getResponse();
    }
    //logon
    Boolean logonQuery(ArrayList<Object> args) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException {
        ArrayList<Object> actionargs = args;
        actionargs.add("act,logon");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(),entity,getmContext());
        try {
            apitaskobj.execute().get(MainActivity.getLilo_timeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Login failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }
        Log.d("Variable","apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        LoginLogoff.logonMessage(getActionresult(), getmContext());//show status of logon action
        if (getActionresult()) {//if the ping is successful(i.e. user logged in)
            LoginLogoff.setSid(apitaskobj.getResponse());
            Log.d("Variable", "loginlogoff.getSid() value: " + LoginLogoff.getSid());
            Log.d("Message", "CI Server logon successful.");
        }
        else{
            Log.d("Message", "CI Server logon failed.");
        }
        resetResult();//reset action result after checking it
        MainActivity.argslist.clear();//clear argslist after query
        return getActionresult();
    }
    //logoff
    Boolean logoffQuery(ArrayList<Object> args) throws ExecutionException,
    InterruptedException, IOException, XmlPullParserException{

        ArrayList<Object> actionargs = args;
        actionargs.add("act,logoff");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(),entity,getmContext());
        try {
            apitaskobj.execute().get(MainActivity.getLilo_timeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Login failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }
        Log.d("Variable","apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        if(getActionresult()){//if login successful, set sid
            LoginLogoff.setSid(xobj.getxmlVals());
        }
        LoginLogoff.logoffMessage(getActionresult(), getmContext());//show status of logon action
        if (getActionresult()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Server logoff successful.");
        }
        else{
            Log.d("Message", "CI Server logoff failed.");
        }
        resetResult();//reset action result after checking it
        MainActivity.argslist.clear();//clear argslist after query
        return getActionresult();
    }
    //ping
    public Boolean pingQuery(ArrayList<Object> args) throws ExecutionException, InterruptedException, IOException, XmlPullParserException {//pings the CI server, returns true if ping successful
        if(LoginLogoff.getSid() == ("") || LoginLogoff.getSid() == null){//check if there is an sid (i.e. a session established)
            Log.d("Message", "CI Server ping failed.");
            return false;//if no session established, return false
        }
        Boolean success;
        ArrayList<Object> actionargs = args;
        actionargs.add("act,ping");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(),entity,getmContext());
        try {
            apitaskobj.execute().get(MainActivity.getAction_timeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Ping failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }
        Log.d("Variable","apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XmlParser xobj = new XmlParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        MainActivity.argslist.clear();//clear argslist after query
        if (getActionresult()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Server ping successful.");
            success = true;
        }
        else{
            Log.d("Message", "CI Server ping failed.");
            success = false;
        }
        resetResult();//reset action result after checking it
        MainActivity.argslist.clear();//clear argslist after query
        return success;
    }
    //retrieve
    public String retrieveQuery(String tid){//pings the CI server, returns true if ping successful
        String retrieveQuery = targetCIQuery() + "?act=retrieve&tid="+ tid + "&sid=" + LoginLogoff.getSid();
        return retrieveQuery;
    }

    //build MultiPartEntity after checking type of the args
    HttpEntity mebBuilder(ArrayList<Object> args) throws UnsupportedEncodingException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for(Object larg : args){//check each argument for class type and act accordingly
            if(larg != null) {//make sure arg isn't null
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
                if (larg.getClass().getName().equals("android.net.Uri$HierarchicalUri")) {//if type of arg is Uri, do this
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

    ArrayList<String> getVersionInfo(String xmlResponse) throws IOException, XmlPullParserException {
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
        Log.d("Message", "pathsarr.length " + pathsarr.length);
        Log.d("Message", "xidarr.length " + xidarr.length);
        Log.d("Message", "dsidarr.length " + dsidsarr.length);
        Log.d("Message", "ctsarr.length " + ctsarr.length);
        Log.d("Message", "bytesarr.length " + bytesarr.length);
        Log.d("Message", "fmtarr.length " + fmtarr.length);
        Log.d("Message", "verarr.length " + verarr.length);

        for(int i = 0; i < dsidsarr.length ; i++){
            StringBuilder sbuild = new StringBuilder();
            sbuild.append(dsidsarr[i] + ",").append(ctsarr[i] + ",").append(bytesarr[i] + ",").append(fmtarr[i] + ",")
                    .append(verarr[i] + ",").append("V~" + xidarr[i] + "~" + dsidsarr[i] + "~" + pathsarr[i] +
                    "~" + verarr[i]);
            Log.d("Variable", "sbuild value: " + sbuild.toString());
            versionInfo.add(sbuild.toString());
    }
        return versionInfo;
    }

    public static ArrayList<String> showItems(ArrayList<String> lvers, int sel) {
        ArrayList<String> vers = new ArrayList<String>();
        for (String v : lvers) {
            String[] pieces = v.split(",");
            vers.add(pieces[sel]);//0=dsid,1=cts,2=bytes,3=fmt,4=ver,5=tid
        }
        return vers;
    }


    //action return code check
    protected void isActionSuccessful(ArrayList<String> larray) {
        if(larray.size()==0){//if the array is of size 0, nothing was returned from the ciserver
            Log.d("Message", "Nothing returned from CI server.");
            setActionresult(false);
        }
        else {
            try {
                if (larray.get(0).equals("0") && larray.get(1).equals("0") && larray.get(2).equals("0")) {
                    setActionresult(true);
                } else {
                    setActionresult(false);
                }
            }
            catch(Exception e){
                ToastMessageTask tmtask = new ToastMessageTask(getmContext(),"Error. Connection to CI Server failed. Check " +
                        "CI Connection Profile under Settings.");
                Log.e("Error",e.toString());
                tmtask.execute();
            }
        }
    }
}
