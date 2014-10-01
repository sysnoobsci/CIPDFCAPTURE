package com.ameraz.android.cipdfcapture.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ameraz.android.cipdfcapture.app.fragments.Capture_Fragment;
import com.ameraz.android.cipdfcapture.app.fragments.Home_Fragment;
import com.ameraz.android.cipdfcapture.app.fragments.Navigation_Drawer_Fragment;

import java.util.ArrayList;

public class MainActivity extends Activity
        implements Navigation_Drawer_Fragment.NavigationDrawerCallbacks {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private Navigation_Drawer_Fragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    static Preference button;
    private CharSequence mTitle;
    Context context = MainActivity.this;
    DatabaseHandler db;

    private Boolean first_open = true;//keeps track of if the app is opening for the first time to show the home screen


    public Boolean getFirst_open() {
        return first_open;
    }

    public void setFirst_open(Boolean first_open) {
        this.first_open = first_open;
    }

    public static void buttonClickListener(final Context context) {
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                //code for what you want it to do
                SharedPreferences connProfile = PreferenceManager.getDefaultSharedPreferences(context);
                String profkey = connProfile.getString("profilename_preference", null);
                String hkey = connProfile.getString("hostname_preference", null);
                String dkey = connProfile.getString("domain_preference", null);
                String portkey = connProfile.getString("port_preference", null);
                String userkey = connProfile.getString("username_preference", null);
                String pwkey = connProfile.getString("password_preference", null);

                ArrayList<String> arlist = new ArrayList<String>();
                arlist.add(profkey);
                arlist.add(hkey);
                arlist.add(dkey);
                arlist.add(portkey);
                arlist.add(userkey);
                arlist.add(pwkey);
                DatabaseHandler db = new DatabaseHandler(context);
                db.add_ci_server(arlist);
                return true;
            }
        });//end of onclick listener
    }

    public static class PrefsFragment extends PreferenceFragment {//saves prefs when save connection is pressed

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            button = findPreference("save");
            buttonClickListener(getActivity());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            getActionBar().setDisplayShowTitleEnabled(false);
        }
        FileUtility.directoryCheck();//checks if application dirs exist
        TempFileTracker.clearTempFiles();//clears all temp files
        db = new DatabaseHandler(getApplicationContext());//create a db if one doesn't exist
        //navigation drawer stuff
        mNavigationDrawerFragment = (Navigation_Drawer_Fragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout)
        );
    }//end of oncreate



    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Log.d("Variable", "Value of argument position: " + position);
        Fragment fragment;
        Log.d("Navigation Position: ", String.valueOf(position));
        if (getFirst_open()) {//if first time opening app, show home screen fragment
            position = -1;
            setFirst_open(false);
        }
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
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
    public void onDestroy() {
        TempFileTracker.clearTempFiles();//clears all temp files on app close
        super.onDestroy();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
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
                    .replace(R.id.container, new PrefsFragment()).addToBackStack(null).commit();
            return true;
        }
        if (id == R.id.action_logoff) {
            new Thread(new Runnable() {
                public void run() {
                    APIQueries apiobj = new APIQueries(context);
                    QueryArguments.addArg(LogonSession.getSid());
                    try {
                        apiobj.logoffQuery(QueryArguments.getArgslist());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        return super.onOptionsItemSelected(item);
    }

}//end of MainActivity