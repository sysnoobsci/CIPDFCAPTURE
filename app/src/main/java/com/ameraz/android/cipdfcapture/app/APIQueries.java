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
    void createtopicQuery(String tplid,String[] nvpairs,String detail,String sid, Uri imageUri) throws IOException, XmlPullParserException, InterruptedException, ExecutionException {
        File newImage;
        if(imageUri == null){
            newImage = new File(FileChooser.getFullFilePath());
            //Log.d("asdf", imageUri.toString());
        }else{
            newImage = new File(imageUri.getPath());
            Log.d("Variable", "imageUri.getPath().toString() value: " + imageUri.getPath().toString());
        }
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
    }

    //listnode - add &sid to the string for it to work properly
    String[] listnodeQuery(String sid) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException{
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        XmlParser xobj = new XmlParser();
        builder.addPart("action", new StringBody("listnode"));
        builder.addPart("sid", new StringBody(sid));
        HttpEntity entity = builder.build();
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
        return listnodeArray;
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
            apitaskobj.execute().get(MainActivity.getAction_timeout(), TimeUnit.MILLISECONDS);
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
    //retrieve
    public com.itextpdf.text.Image retrieveQuery(String mode,String tid,String dsid,int xid,int tplid,
                                 String fmt,String combtype,int maxseg,int offset,String axvs,
                                 String label,String inline,String tq,int sln,int lns) throws ExecutionException, InterruptedException, IOException, XmlPullParserException {//pings the CI server, returns true if ping successful
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart("action", new StringBody("retrieve"));
        builder.addPart("mode", new StringBody(mode));
        builder.addPart("tid", new StringBody(tid));
        builder.addPart("dsid", new StringBody(dsid));
        builder.addPart("xid", new StringBody(String.valueOf(xid)));
        builder.addPart("tplid", new StringBody(String.valueOf(tplid)));
        builder.addPart("fmt", new StringBody(fmt));
        builder.addPart("combtype", new StringBody(combtype));
        builder.addPart("maxseg", new StringBody(String.valueOf(maxseg)));
        builder.addPart("offset", new StringBody(String.valueOf(offset)));
        builder.addPart("axvs", new StringBody(axvs));
        builder.addPart("label", new StringBody(label));
        builder.addPart("inline", new StringBody(inline));
        builder.addPart("tq", new StringBody(tq));
        builder.addPart("sln", new StringBody(String.valueOf(sln)));
        builder.addPart("lns", new StringBody(String.valueOf(lns)));
        HttpEntity entity = builder.build();
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

        return image;
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
