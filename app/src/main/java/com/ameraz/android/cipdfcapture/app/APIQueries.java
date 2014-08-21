package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.util.Log;

import com.ameraz.android.cipdfcapture.app.filebrowser.FileChooser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by adrian.meraz on 6/27/2014.
 */
public class APIQueries {
    Context mContext;
    QueryFormer qf = new QueryFormer();
    Boolean actionresult = false;
    final static int ACTION_TIMEOUT = 1000;//time in milliseconds for ping attempt to timeout
    final static private int CT_TIMEOUT = 30000;//time in milliseconds for createtopic attempt to timeout


    public APIQueries(Context mContext){
        this.mContext = mContext;
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
    void createtopicQuery(String tplid,String[] nvpairs,String detail,String sid) throws IOException, XmlPullParserException, InterruptedException, ExecutionException {
        File newImage = new File(FileChooser.getFullFilePath());
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        XmlParser xobj = new XmlParser();
        builder.addPart("action", new StringBody("createtopic"));
        builder.addPart("tplid", new StringBody(tplid));
        if(nvpairs.length > 0){//check if array has at least one element
            for(String nvp : nvpairs){
                if (nvp!="" || nvp!=null) {//check if element is empty or null
                    String[] parts = nvp.split(",");
                    builder.addPart(parts[0], new StringBody(parts[1]));
                }
            }
        }
        builder.addPart("detail", new StringBody(detail));
        builder.addPart("sid", new StringBody(sid));
        builder.addPart("file", new FileBody(newImage));
        HttpEntity entity = builder.build();
        APITask apitaskobj = new APITask(targetCIQuery(),entity,mContext);
        try {
            apitaskobj.execute().get(CT_TIMEOUT, TimeUnit.MILLISECONDS);
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
    }

    //listnode - add &sid to the string for it to work properly
    String listnodeQuery(String sid){
        String listnodeQuery = "?action=listnode" + qf.formQuery("sid," + sid);
        return targetCIQuery() + listnodeQuery;
    }
    //logon
    Boolean logonQuery(String user,String password,String newpwd) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        XmlParser xobj = new XmlParser();
        builder.addPart("action", new StringBody("logon"));
        builder.addPart("user", new StringBody(user));
        builder.addPart("password", new StringBody(password));
        if(newpwd != null && !newpwd.equals("null")) {
            builder.addPart("newpwd", new StringBody(newpwd));
        }
        HttpEntity entity = builder.build();
        APITask apitaskobj = new APITask(targetCIQuery(),entity,getmContext());
        try {
            apitaskobj.execute().get(ACTION_TIMEOUT, TimeUnit.MILLISECONDS);
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
        return getActionresult();
    }
    //logoff
    Boolean logoffQuery(String sid) throws ExecutionException,
    InterruptedException, IOException, XmlPullParserException{
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        XmlParser xobj = new XmlParser();
        builder.addPart("action", new StringBody("logff"));
        builder.addPart("sid", new StringBody(sid));
        HttpEntity entity = builder.build();
        APITask apitaskobj = new APITask(targetCIQuery(),entity,getmContext());
        try {
            apitaskobj.execute().get(ACTION_TIMEOUT, TimeUnit.MILLISECONDS);
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
        return getActionresult();
    }
    //ping
    public Boolean pingQuery() throws ExecutionException, InterruptedException, IOException, XmlPullParserException {//pings the CI server, returns true if ping successful
        if(LoginLogoff.getSid() == ("") || LoginLogoff.getSid() == null){//check if there is an sid (i.e. a session established)
            Log.d("Message", "CI Server ping failed.");
            return false;//if no session established, return false
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        XmlParser xobj = new XmlParser();
        builder.addPart("action", new StringBody("ping"));
        builder.addPart("sid", new StringBody(LoginLogoff.getSid()));
        HttpEntity entity = builder.build();
        APITask apitaskobj = new APITask(targetCIQuery(),entity,getmContext());
        try {
            apitaskobj.execute().get(ACTION_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask tmtask = new ToastMessageTask(getmContext(), "Ping failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }
        Log.d("Variable","apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        xobj.parseXMLfunc(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        if (getActionresult()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Server ping successful.");
            return true;
        }
        else{
            Log.d("Message", "CI Server ping failed.");
            return false;
        }
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
