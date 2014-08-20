package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by adrian.meraz on 5/16/2014.
 */


public class loginlogoff {

    Context mContext;

    public loginlogoff(Context mContext) {
        loginlogoff.this.mContext = mContext;
    }

    private static String hostname;
    private static String domain;
    private static int portnumber;
    private static String username;
    private static String password;
    private static String sid;//session id
    private static Boolean login_successful = false;
    private static Boolean logoff_successful = false;
    final static int LOGIN_TIMEOUT = 500;//in milliseconds
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

    public static void setSid(String result) {
        Log.d("Variable","result value: " + result);
        String target = "session sid=\"";
        int a = result.indexOf(target);
        int b = a + target.length();
        int c =  a + SIZE_OF_TARGET_SID + target.length();//54 is the size of the sid plus the "target" string size
        loginlogoff.sid = result.substring(b,c);
    }

    public static Boolean getLogin_successful() {
        return login_successful;
    }

    public static void setLogin_successful(Boolean login_successful) {
        loginlogoff.login_successful = login_successful;
    }

    public static Boolean getLogoff_successful() {
        return logoff_successful;
    }

    public static void setLogoff_successful(Boolean logoff_successful) {
        loginlogoff.logoff_successful = logoff_successful;
    }

    protected void isLoginSuccessful(ArrayList<String> larray) {
        try{
            if(larray.get(0).equals("0") && larray.get(1).equals("0") && larray.get(2).equals("0")){
                setLogin_successful(true);
            }
            else{
                setLogin_successful(false);
            }
        }
        catch(IndexOutOfBoundsException iobe){
            iobe.printStackTrace();
            setLogin_successful(false);
        }
    }
    void logonMessage(){
        if (getLogin_successful()) {
            ToastMessageTask tmtask = new ToastMessageTask(mContext,"Successfully logged in.");
            tmtask.execute();
        }
        else {
            ToastMessageTask tmtask = new ToastMessageTask(mContext,"Problem logging off.");
            tmtask.execute();
        }

    }

    void isLogoffSuccessful(ArrayList<String> larray){
        if(larray.get(0).equals("0") && larray.get(1).equals("0") && larray.get(2).equals("0")){
            setLogoff_successful(true);
        }
        else{
            setLogoff_successful(false);
        }
    }

    void logoffMessage(){
        if(getLogoff_successful()){
            ToastMessageTask tmtask = new ToastMessageTask(mContext, "Successfully logged off.");
            tmtask.execute();
        }
        else{
            ToastMessageTask tmtask = new ToastMessageTask(mContext, "Problem logging off.");
            tmtask.execute();
        }
    }

    public void setCiLoginInfo(){
        SharedPreferences preferences;
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
            ToastMessageTask tmtask = new ToastMessageTask(mContext,"Select a CI Profile under Settings.");
            tmtask.execute();
        }
    }

    public Boolean tryLogin() throws InterruptedException, ExecutionException, XmlPullParserException, IOException {
        setCiLoginInfo();
        //try a ping first, if successful, don't try logging in again
        APIQueries apiobj2 = new APIQueries(mContext);
        if(apiobj2.pingQuery()){
            Log.d("Message","Logon session already established. Ping Successful.");
            return true;//if ping is successful, return true
        }
                XmlParser xobj3 = new XmlParser();
                APIQueries apiobj = new APIQueries(mContext);
                ReqTask reqobj = new ReqTask(apiobj.logonQuery(getUsername(),
                        getPassword(), null), mContext);//send login query to CI via asynctask
                try {
                    reqobj.execute().get(LOGIN_TIMEOUT, TimeUnit.MILLISECONDS);
                }
                catch (TimeoutException e) {
                    ToastMessageTask tmtask = new ToastMessageTask(mContext, "Logon attempt timed out.");
                    tmtask.execute();
                    e.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    xobj3.parseXMLfunc(reqobj.getResult());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Upload_Fragment.setLogonXmlTextTags(xobj3.getTextTag());
                //check if login worked
                isLoginSuccessful(Upload_Fragment.getLogonXmlTextTags());//check if login was successful
                logonMessage();//show status of login
                if (getLogin_successful()){
                    setSid(reqobj.getResult());//get the session id if the login was successful
                    return true;
                }
        return false;
    }
}
