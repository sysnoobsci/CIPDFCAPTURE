package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

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
    final static int PING_TIMEOUT = 500;

    public APIQueries(Context mContext){
        this.mContext = mContext;
    }

    public Boolean getPingresult() {
        return pingresult;
    }

    public void setPingresult(Boolean pingresult) {
        this.pingresult = pingresult;
    }

    String targetCIQuery(){
        loginlogoff lilobj = new loginlogoff(mContext);
        String targetCIQuery = "http://" + lilobj.getHostname() + "." +
                lilobj.getDomain() + ":" + lilobj.getPortnumber() + "/ci";
        return targetCIQuery;
    }
    //listnode
    String listnodeQuery(){
        String listnodeQuery = "?action=listnode";
        return targetCIQuery() + listnodeQuery;
    }
    //logon
    String logonQuery(String user,String password,String newpwd){
        String logonQuery = "?action=logon" + qf.formQuery("user," + user,"password," + password,
                "newpwd," + newpwd);
        return targetCIQuery() + logonQuery;
    }
    //logoff
    String logoffQuery(){
        String logoffQuery = "?action=logoff";
        return targetCIQuery() + logoffQuery;
    }

    //ping
    String pingQuery(String sid){
        String pingQuery = "?action=ping" + qf.formQuery("sid," + sid);
        return targetCIQuery() + pingQuery;
    }
    public Boolean pingserver() throws ExecutionException, InterruptedException, IOException, XmlPullParserException {//pings the CI server, returns true if ping successful
        Boolean pingresult;// default is false
        XmlParser xobj = new XmlParser();
        APIQueries apiobj = new APIQueries(mContext);

        ReqTask reqobj4 = new ReqTask(apiobj.pingQuery(loginlogoff.getSid()), mContext);
        try{
            reqobj4.execute().get(PING_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        catch(TimeoutException te){
            Log.e("Error", te.toString());
            ToastMessageTask tmtask = new ToastMessageTask(mContext,"Connection to CI Server timed out. Check" +
                    "CI Connection Profile under Settings.");
            tmtask.execute();
        }
        xobj.parseXMLfunc(reqobj4.getResult());
        apiobj.isPingSuccessful(xobj.getTextTag());
        if (apiobj.getPingresult()) {//if the ping is successful(i.e. user logged in)
            Log.d("Message", "CI Server ping successful.");
            pingresult = true;
        }
        else{
            Log.d("Message", "CI Server ping failed.");
            pingresult = false;
        }
        return pingresult;
    }
    //ping check
    protected void isPingSuccessful(ArrayList<String> larray) {
        if(larray.size()==0){//if the array is of size 0, nothing was returned from the ciserver
            Log.d("Message", "Nothing returned from CI server.");
            setPingresult(false);
        }
        else {
            try {
                if (larray.get(0).equals("0") && larray.get(1).equals("0") && larray.get(2).equals("0")) {
                    setPingresult(true);
                } else {
                    setPingresult(false);
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
                         String inline,String tq,String sln){
        String retrieveQuery = "?action=retrieve" + qf.formQuery("mode,"+mode,"tid,"+tid,"DSID"+DSID,
                "xid,"+xid,"tplid,"+tplid,"fmt,"+fmt,"combtype,"+combtype,"maxseg,"+maxseg,"offset,"+offset,
                "axvs,"+axvs,"label,"+label,"inline,"+inline,"tq,"+tq,"sln,"+sln);
        return targetCIQuery() + retrieveQuery;
    }
    //createtopic
    String createtopicQuery(String tplid,String[] nvpairs,String detail){
        String appender = "";
        int j = 0;
        for(String nvp : nvpairs){
            appender.concat(nvp);
            if(j!=nvpairs.length-1){//if not at end of array, put commas between all the key-value pairs
                appender.concat(",");
            }
            j++;
        }
        String createtopicQuery = "?action=createtopic" + qf.formQuery("tplid,"+tplid,appender,"detail,"+ detail);
        return targetCIQuery() + createtopicQuery;
    }


}
