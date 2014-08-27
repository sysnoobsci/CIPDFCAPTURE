package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by adrian.meraz on 5/16/2014.
 */

public class LoginLogoff {

    Context mContext;

    public LoginLogoff(Context mContext) {
        LoginLogoff.this.mContext = mContext;
    }

    private static String hostname;
    private static String domain;
    private static int portnumber;
    private static String username;
    private static String password;
    private static String sid;//session id
    SharedPreferences preferences;
    final static int SIZE_OF_TARGET_SID = 40;//size of session ID

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
        LoginLogoff.domain = domain;
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

    public static void setSid(String result) {
        Log.d("Variable","result value: " + result);
        String target = "session sid=\"";
        int a = result.indexOf(target);
        int b = a + target.length();
        int c =  a + SIZE_OF_TARGET_SID + target.length();//54 is the size of the sid plus the "target" string size
        LoginLogoff.sid = result.substring(b,c);
    }

    static void logonMessage(Boolean success, Context context){
        if (success) {
            ToastMessageTask tmtask = new ToastMessageTask(context,"Successfully logged in.");
            tmtask.execute();
        }
        else {
            ToastMessageTask tmtask = new ToastMessageTask(context,"Problem logging off.");
            tmtask.execute();
        }
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

    public void setCiLoginInfo(){
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

    public Boolean tryLogin() throws InterruptedException, ExecutionException, XmlPullParserException, IOException {
        setCiLoginInfo();
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        APIQueries apiobj = new APIQueries(mContext);
        Boolean login_result = false;
        //try a ping first, if successful, don't try logging in again
        Capture_Fragment.argslist.add("sid," + LoginLogoff.getSid());
        if(apiobj.pingQuery(Capture_Fragment.argslist)){
            Log.d("Message","Logon session already established. Ping Successful.");
            return true;//if ping is successful, return true
        }
        Log.d("Variable","preferences.getString() value: " + preferences.getString("list_preference_ci_servers", "n/a"));
        if(!preferences.getString("list_preference_ci_servers", "n/a").equals("n/a")) {//check if profile has been chosen
            Capture_Fragment.argslist.add("user," + getUsername());
            Capture_Fragment.argslist.add("password," + getPassword());
            login_result = apiobj.logonQuery(Capture_Fragment.argslist);//send login query to CI via asynctask
        }
        return login_result;
    }
}
