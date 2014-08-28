package com.ameraz.android.cipdfcapture.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity
    implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    SharedPreferences preferences;
    private CharSequence mTitle;
    Context maContext = MainActivity.this;
    DatabaseHandler db;
    final static ArrayList<Object> argslist = new ArrayList<Object>();

    private Boolean first_open = true;//keeps track of if the app is opening for the first time to show the home screen
    private static int action_timeout = 1000;//action timeout - default 1 sec
    private static int lilo_timeout = 1000;//login/logout timeout - default 1 sec
    private static int upload_timeout = 30000;//upload timeout - default 30 secs

    public Boolean getFirst_open() {
        return first_open;
    }

    public void setFirst_open(Boolean first_open) {
        this.first_open = first_open;
    }

    public static int getAction_timeout() {
        return action_timeout;
    }

    public static void setAction_timeout(String action_timeout) {
        if(action_timeout != null){
            MainActivity.action_timeout = Integer.parseInt(action_timeout) * 1000;
        }
        else{
            Log.d("Message","No action timeout in preferences. Default set to " + getAction_timeout() + " seconds");
        }
    }

    public static int getLilo_timeout() {
        return lilo_timeout;
    }

    public static void setLilo_timeout(String lilo_timeout) {
        if(lilo_timeout != null){
            MainActivity.lilo_timeout = Integer.parseInt(lilo_timeout) * 1000;
        }
        else{
            Log.d("Message","No login/logoff timeout in preferences. Default set to " + getLilo_timeout() + " seconds");
        }
    }

    public static int getUpload_timeout() {
        return upload_timeout;
    }

    public static void setUpload_timeout(String upload_timeout) {
        if(upload_timeout != null){
            MainActivity.upload_timeout = Integer.parseInt(upload_timeout) * 1000;
        }
        else{
            Log.d("Message","No upload timeout in preferences. Default set to " + getUpload_timeout() + " seconds");
        }
    }

    public void saveTimestamp(){//save current timestamp
        //add current date to preferences for next app opening
        Log.d("PrefDate", preferences.getString("pref_date", "n/a"));
        //setting up date and time on Home_Fragment before closing app
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeStamp = dateFormat.format(new Date()); // Find todays date
        Log.d("Date",currentTimeStamp);//log the time stamp
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("pref_date", currentTimeStamp);//added date to preferences for next app open
        edit.commit();
    }

    public void setTimeouts(){
        setAction_timeout(preferences.getString("actiontimeout_preference", null));
        setLilo_timeout(preferences.getString("lilotimeout_preference", null));
        setUpload_timeout(preferences.getString("uploadtimeout_preference", null));
    }

    public static class PrefsFragment extends PreferenceFragment {//saves prefs when save connection is pressed
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            Preference button = findPreference("save");

            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    //code for what you want it to do
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    String profkey = preferences.getString("profilename_preference", null);
                    String hkey = preferences.getString("hostname_preference", null);
                    String dkey = preferences.getString("domain_preference", null);
                    String portkey = preferences.getString("port_preference", null);
                    String userkey = preferences.getString("username_preference", null);
                    String pwkey = preferences.getString("password_preference", null);

                    ArrayList<String> arlist = new ArrayList<String>();
                    arlist.add(profkey);
                    arlist.add(hkey);
                    arlist.add(dkey);
                    arlist.add(portkey);
                    arlist.add(userkey);
                    arlist.add(pwkey);
                    DatabaseHandler db = new DatabaseHandler(getActivity());
                    db.add_ci_server(arlist);
                    return true;
                }
            });//end of onclick listener
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        super.onCreate(savedInstanceState);
        // Create loginDialog Dialog
        setTimeouts();
        setContentView(R.layout.activity_main);
        db = new DatabaseHandler(getApplicationContext());//create a db if one doesn't exist
        //navigation drawer stuff
        mNavigationDrawerFragment=(NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle=getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout)findViewById(R.id.drawer_layout)
        );
    }//end of oncreate

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Log.d("Variable", "Value of argument position: " + position);
        Fragment fragment;
        Log.d("Navigation Position: ", String.valueOf(position));
        if(getFirst_open()){//if first time opening app, show home screen fragment
            position = -1;
            setFirst_open(false);
        }
        FragmentManager fragmentManager = getFragmentManager();
        switch(position) {
            case 0:
                fragment = new Capture_Fragment();
                break;
            default:
                fragment = new Home_Fragment();
                break;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
    @Override
     public void onStop(){
        saveTimestamp();
        super.onStop();
    }
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new PrefsFragment()).commit();
            return true;
        }
        if(id == R.id.action_logoff) {
            new Thread(new Runnable() {
                public void run() {
                    APIQueries apiobj = new APIQueries(maContext);
                    try {
                        MainActivity.argslist.add(LoginLogoff.getSid());
                        apiobj.logoffQuery(MainActivity.argslist);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        return super.onOptionsItemSelected(item);
    }

}//end of MainActivity