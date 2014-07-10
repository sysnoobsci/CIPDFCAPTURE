package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
    String pingQuery(){
        String pingQuery = "?action=ping";
        return targetCIQuery() + pingQuery;
    }
    //ping check
    protected void isPingSuccessful(ArrayList<String> larray) {
        if(larray.size()==0){//if the array is of size 0, nothing was returned from the ciserver
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
            catch(IndexOutOfBoundsException iob){
                ToastMessageTask tmtask = new ToastMessageTask(mContext,"Error. Connection to CI Server failed. Check " +
                                                "CI Connection Profile under Settings.");
                Log.e("Error",iob.toString());
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

}
