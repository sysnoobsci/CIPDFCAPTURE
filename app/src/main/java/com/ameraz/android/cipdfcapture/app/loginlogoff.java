package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;


/**
 * Created by adrian.meraz on 5/16/2014.
 */


public class loginlogoff {

    Context mContext;

    public loginlogoff(Context mContext) {
        loginlogoff.this.mContext = mContext;
    }

    private static String hostname = "gardner";
    private static String domain = "usa.systemware.com";
    private static int portnumber = 34544;
    private static String username = "admin";
    private static String password = "admin";
    private static Boolean connection_state = false;
    private static Boolean login_successful = false;
    private static Boolean logoff_successful = false;

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

    public static Boolean getConnection_state() {
        return connection_state;
    }

    public static void setConnection_state(Boolean connection_state) {
        loginlogoff.connection_state = connection_state;
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
        if(larray.get(0).equals("0") && larray.get(1).equals("0") && larray.get(2).equals("0")){
            setLogin_successful(true);
        }
        else{
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

    private void isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        setConnection_state(activeNetworkInfo != null && activeNetworkInfo.isConnected());
        Log.d("Message", "loginlogoff.java - getConnection_state(): " + getConnection_state());
        //return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
