package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.ameraz.android.cipdfcapture.app.filebrowser.FileChooser;
import com.itextpdf.text.BadElementException;

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
    Boolean actionresult = false;

    public APIQueries(Context mContext){
        setmContext(mContext);
    }

    public Boolean getActionresult() {
        return actionresult;
    }

    public void setActionresult(Boolean pingresult) {
        this.actionresult = pingresult;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    String targetCIQuery(){
        LoginLogoff lilobj = new LoginLogoff(mContext);
        String targetCIQuery = "http://" + lilobj.getHostname() + "." +
                lilobj.getDomain() + ":" + lilobj.getPortnumber() + "/ci";
        return targetCIQuery;
    }
    //createtopic
    void createtopicQuery(ArrayList<Object> args) throws IOException, XmlPullParserException, InterruptedException, ExecutionException {
        XmlParser xobj = new XmlParser();
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
        xobj.parseXMLfunc(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        if(getActionresult()){//if return codes are good, it was successful
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(),"File was successfully Uploaded.");
            tmtask.execute();
        }
        else{
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(),"File upload failed.");
            tmtask.execute();
        }
        Capture_Fragment.argslist.clear();//clear argslist after query
    }

    //listnode - add &sid to the string for it to work properly
    String[] listnodeQuery(ArrayList<Object> args) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException{
        XmlParser xobj = new XmlParser();
        ArrayList<Object> actionargs = args;
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
        xobj.parseXMLfunc(apitaskobj.getResponse());
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
        listnodeArray[0] =  xobj.findTagText("xid",apitaskobj.getResponse());
        listnodeArray[1] = xobj.findTagText("name",apitaskobj.getResponse());
        Capture_Fragment.argslist.clear();//clear argslist after query
        return listnodeArray;
    }
    //listversion
    /*String[] listversionQuery(ArrayList<Object> args) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException {
        ArrayList<Object> actionargs = args;
        actionargs.add("act,listversion");
        HttpEntity entity = mebBuilder(actionargs);
        XmlParser xobj = new XmlParser();
        APITask apitaskobj = new APITask(targetCIQuery(),entity,getmContext());
        try {
            apitaskobj.execute().get(MainActivity.getLilo_timeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Login failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }
        Log.d("Variable","apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        xobj.parseXMLfunc(apitaskobj.getResponse());
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
        Capture_Fragment.argslist.clear();//clear argslist after query
        return getActionresult();
    }*/
    //logon
    Boolean logonQuery(ArrayList<Object> args) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException {
        XmlParser xobj = new XmlParser();
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
        xobj.parseXMLfunc(apitaskobj.getResponse());
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
        Capture_Fragment.argslist.clear();//clear argslist after query
        return getActionresult();
    }
    //logoff
    Boolean logoffQuery(ArrayList<Object> args) throws ExecutionException,
    InterruptedException, IOException, XmlPullParserException{
        XmlParser xobj = new XmlParser();
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
        xobj.parseXMLfunc(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        if(getActionresult()){//if login successful, set sid
            LoginLogoff.setSid(xobj.getXmlstring());
        }
        LoginLogoff.logoffMessage(getActionresult(), getmContext());//show status of logon action
        if (getActionresult()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Server logoff successful.");
        }
        else{
            Log.d("Message", "CI Server logoff failed.");
        }
        Capture_Fragment.argslist.clear();//clear argslist after query
        return getActionresult();
    }
    //ping
    public Boolean pingQuery(ArrayList<Object> args) throws ExecutionException, InterruptedException, IOException, XmlPullParserException {//pings the CI server, returns true if ping successful
        if(LoginLogoff.getSid() == ("") || LoginLogoff.getSid() == null){//check if there is an sid (i.e. a session established)
            Log.d("Message", "CI Server ping failed.");
            return false;//if no session established, return false
        }
        XmlParser xobj = new XmlParser();
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
        xobj.parseXMLfunc(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        Capture_Fragment.argslist.clear();//clear argslist after query
        if (getActionresult()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Server ping successful.");
            return true;
        }
        else{
            Log.d("Message", "CI Server ping failed.");
            return false;
        }

    }
    //retrieve
    public com.itextpdf.text.Image retrieveQuery(ArrayList<Object> args) throws ExecutionException, InterruptedException, IOException, XmlPullParserException {//pings the CI server, returns true if ping successful
        ArrayList<Object> actionargs = args;
        actionargs.add("act,retrieve");
        HttpEntity entity = mebBuilder(actionargs);
        APITask apitaskobj = new APITask(targetCIQuery(),entity,getmContext());
        try {
            apitaskobj.execute().get(MainActivity.getAction_timeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Download failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }
        //the content is returned. Handle the response and set it to an object of the correct filetype
        com.itextpdf.text.Image image = null;
        try {
            image = com.itextpdf.text.Image.getInstance(apitaskobj.getResponse());
        } catch (BadElementException e) {
            ToastMessageTask tmtask = new ToastMessageTask(mContext,"Bad Filetype returned. Download Failed");
            tmtask.execute();
            e.printStackTrace();
        }
        Capture_Fragment.argslist.clear();//clear argslist after query
        return image;
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
