package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by adrian.meraz on 5/16/2014.
 */

public class loginlogoff {

    Context mContext;

    public loginlogoff(Context mContext) {
        setmContext(mContext);
    }

    private static String hostname;
    private static String domain;
    private static int portnumber;
    private static String username;
    private static String password;
    private static String sid;//session id
    private static String jsid;//jsession id
    SharedPreferences preferences;
    final static int SIZE_OF_TARGET_SID = 40;//size of session ID
    final static int SIZE_OF_TARGET_JSID = 32;//size of session ID
    APIQueries apiobj = new APIQueries(mContext);

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public static String getDomain() {
        return domain;
    }

    public static void setDomain(String domain) {
        loginlogoff.domain = domain;
    }

    public int getPortnumber() {
        return portnumber;
    }

    public void setPortnumber(int portnumber) {
        this.portnumber = portnumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static String getSid() {
        return sid;
    }

    public static String getJSid() {
        return jsid;
    }

    public static void setSid(String result) {
        Log.d("Variable","result value: " + result);
        String target = "session sid=\"";
        int a = result.indexOf(target);
        int b = a + target.length();
        int c =  a + SIZE_OF_TARGET_SID + target.length();//54 is the size of the sid plus the "target" string size
        loginlogoff.sid = result.substring(b,c);
        Log.d("Sid", sid);
    }

    public static void setJSid(String result) {
        Log.d("Variable","result value: " + result);
        String target = "jsessionid=\"";
        int a = result.indexOf(target);
        int b = a + target.length();
        int c =  a + SIZE_OF_TARGET_JSID + target.length();//47 is the size of the sid plus the "target" string size
        loginlogoff.jsid = result.substring(b,c);
        Log.d("JSid", jsid);
    }

    static void logoffMessage(Boolean success, Context context){
        if(success){
            ToastMessageTask tmtask = new ToastMessageTask(context, "Successfully logged off.");
            tmtask.execute();
        }
        else{
            ToastMessageTask tmtask = new ToastMessageTask(context, "Problem logging off.");
            tmtask.execute();
        }
    }

    public void setCiLoginInfo() {//takes the info from the fields and sends it in the loginQuery
        DatabaseHandler dbh = new DatabaseHandler(mContext);
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String ciserver = preferences.getString("list_preference_ci_servers", "n/a");
        String ciserverResult = dbh.select_ci_server(ciserver);
        String[] parms = ciserverResult.split(",");
        try {
            setHostname(parms[2]);
            setDomain(parms[3]);
            setPortnumber(Integer.parseInt(parms[4]));
            setUsername(parms[5]);
            setPassword(parms[6]);
        }
        catch(Exception e){
            Log.e("Error", e.toString());
        }
    }

    protected Boolean tryLogin(Context context) throws Exception {
        Boolean loginResult;
        setCiLoginInfo();
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        //try a ping first, if successful, don't try logging in again
        QueryArguments.addArg("sid," + loginlogoff.getSid());
        if (apiobj.pingQuery(QueryArguments.getArgslist())) {
            Log.d("tryLogin()", "Logon session already established. Ping Successful.");
            return true;//if ping is successful, return true
        }
        if(!preferences.getString("list_preference_ci_servers", "n/a").equals("n/a")) {//check if profile has been chosen
            QueryArguments.addArg("user," + getUsername());
            QueryArguments.addArg("password," + getPassword());
            loginResult = apiobj.logonQuery(QueryArguments.getArgslist());//send login query to CI via asynctask
            return loginResult;
        } else {
            ToastMessageTask.noProfileSelected(context);
            return false;
        }
    }
}
