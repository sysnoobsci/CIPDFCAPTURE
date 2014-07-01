package com.ameraz.android.cipdfcapture.app;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 6/27/2014.
 */
public class APIQueries {
    Context mContext;
    QueryFormer qf = new QueryFormer();
    Boolean pingresult = false;

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
    //logon
    String logonQuery(String user,String password,String newpwd){
        String logonQuery = "?action=logon" + qf.formQuery("user." + user,"password." + password,
                "newpwd." + newpwd);
        return targetCIQuery() + logonQuery;
    }
    //logoff
    String logoffQuery(){
        String logoffQuery = "?action=logoff";
        return targetCIQuery() + logoffQuery;
    }
    //ping
    String pingQuery(){
        String pingQuery = "?action=ping";
        return targetCIQuery() + pingQuery;
    }
    //ping check
    protected void isPingSuccessful(ArrayList<String> larray) {
        if(larray.get(0).equals("0") && larray.get(1).equals("0") && larray.get(2).equals("0")){
            setPingresult(true);
        }
        else{
            setPingresult(false);
        }
    }

}
