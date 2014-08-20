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
    Boolean pingresult = false;
    final static int PING_TIMEOUT = 500;//time in milliseconds for ping attempt to timeout
    final static private int CT_TIMEOUT = 30000;//time in milliseconds for createtopic attempt to timeout

    public APIQueries(Context mContext){
        this.mContext = mContext;
    }

    public Boolean getActionresult() {
        return pingresult;
    }

    public void setActionresult(Boolean pingresult) {
        this.pingresult = pingresult;
    }

    String targetCIQuery(){
        loginlogoff lilobj = new loginlogoff(mContext);
        String targetCIQuery = "http://" + lilobj.getHostname() + "." +
                lilobj.getDomain() + ":" + lilobj.getPortnumber() + "/ci";
        return targetCIQuery;
    }
    //createtopic
    void createtopicQuery(String tplid,String[] nvpairs,String detail,String sid) throws IOException, XmlPullParserException {
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
        APITasks apitaskobj = new APITasks(targetCIQuery(),entity,mContext);
        try {
            apitaskobj.execute().get(CT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask tmtask = new ToastMessageTask(mContext, "Create topic call failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("Variable","apitaskobj.getResult() value: " + apitaskobj.getResult());
        xobj.parseXMLfunc(apitaskobj.getResult());
        isActionSuccessful(xobj.getTextTag());
        if(getActionresult()){//if return codes are good, it was successful
            ToastMessageTask tmtask = new ToastMessageTask(mContext,"File was successfully Uploaded.");
            tmtask.execute();
        }
        else{
            ToastMessageTask tmtask = new ToastMessageTask(mContext,"File upload failed.");
            tmtask.execute();
        }
    }

    //listnode - add &sid to the string for it to work properly
    String listnodeQuery(String sid){
        String listnodeQuery = "?action=listnode" + qf.formQuery("sid," + sid);
        return targetCIQuery() + listnodeQuery;
    }
    //logon
    String logonQuery(String user,String password,String newpwd){
        String logonQuery = "?action=logon" + qf.formQuery("user," + user,"password," + password,
                "newpwd," + newpwd);
        return targetCIQuery() + logonQuery;
    }
    //logoff
    String logoffQuery(String sid){
        String logoffQuery = "?action=logoff" + qf.formQuery("sid," + sid);
        return targetCIQuery() + logoffQuery;
    }

    //ping
    public Boolean pingQuery() throws ExecutionException, InterruptedException, IOException, XmlPullParserException {//pings the CI server, returns true if ping successful
        if(loginlogoff.getSid() == ("") || loginlogoff.getSid() == null){//check if there is an sid (i.e. a session established)
            Log.d("Message", "CI Server ping failed.");
            return false;//if no session established, return false
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        XmlParser xobj = new XmlParser();
        builder.addPart("action", new StringBody("ping"));
        builder.addPart("sid", new StringBody(loginlogoff.getSid()));
        HttpEntity entity = builder.build();
        APITasks apitaskobj = new APITasks(targetCIQuery(),entity,mContext);
        try {
            apitaskobj.execute().get(PING_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMessageTask tmtask = new ToastMessageTask(mContext, "Ping failed. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("Variable","apitaskobj.getResult() value: " + apitaskobj.getResult());
        xobj.parseXMLfunc(apitaskobj.getResult());
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
                ToastMessageTask tmtask = new ToastMessageTask(mContext,"Error. Connection to CI Server failed. Check " +
                                                "CI Connection Profile under Settings.");
                Log.e("Error",e.toString());
                tmtask.execute();
            }
        }
    }
    //retrieve
    String retrieveQuery(String mode, String tid, String DSID, String xid, String tplid,String fmt,
                         String combtype,String maxseg,String offset,String axvs,String label,
                         String inline,String tq,String sln,String sid){
        String retrieveQuery = "?action=retrieve" + qf.formQuery("mode,"+mode,"tid,"+tid,"DSID"+DSID,
                "xid,"+xid,"tplid,"+tplid,"fmt,"+fmt,"combtype,"+combtype,"maxseg,"+maxseg,"offset,"+offset,
                "axvs,"+axvs,"label,"+label,"inline,"+inline,"tq,"+tq,"sln,"+sln,"sid,"+sid);
        return targetCIQuery() + retrieveQuery;
    }



}
