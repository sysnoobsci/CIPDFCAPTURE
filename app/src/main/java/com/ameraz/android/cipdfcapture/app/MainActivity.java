package com.ameraz.android.cipdfcapture.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ameraz.android.cipdfcapture.app.filebrowser.Browse_Fragment;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    Dialog loginDialog = null;
    Context maContext = MainActivity.this;
    ArrayList<String> logonXmlTextTags;
    Bundle bundle2 = new Bundle();
    String datetime;
    final static private int LOGIN_TIMEOUT = 500;//time in milliseconds for login attempt to timeout
    final static private int LOGOFF_TIMEOUT = 500;//time in milliseconds for logoff attempt to timeout
    final static private int REQUEST_TIMEOUT = 500;

    private Boolean first_open = true;//keeps track of if the app is opening for the first time to show the home screen

    ProgressDialog progress;

    public ArrayList<String> getLogonXmlTextTags() {
        return logonXmlTextTags;
    }

    public void setLogonXmlTextTags(ArrayList<String> logonXmlTextTags) {
        this.logonXmlTextTags = logonXmlTextTags;
    }

    public Boolean getFirst_open() {
        return first_open;
    }

    public void setFirst_open(Boolean first_open) {
        this.first_open = first_open;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        loginDialog = new Dialog(this);
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        // Create loginDialog Dialog
        setContentView(R.layout.activity_main);
        loginDialog.setContentView(R.layout.login_dialog);
        //navigation drawer stuff
        mNavigationDrawerFragment=(NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle=getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout)findViewById(R.id.drawer_layout)
        );
        Log.d("PrefDate",preferences.getString("pref_date", "n/a"));
        //******FIX THIS******
        Home_Fragment hmobj = new Home_Fragment();
        hmobj.setText(preferences.getString("pref_date", "n/a"));
        // Set GUI of loginDialog screen
        final EditText hostname = (EditText) loginDialog.findViewById(R.id.hostname);
        final EditText domain = (EditText) loginDialog.findViewById(R.id.domain);
        final EditText port = (EditText) loginDialog.findViewById(R.id.port);
        final EditText username = (EditText) loginDialog.findViewById(R.id.username);
        final EditText password = (EditText) loginDialog.findViewById(R.id.password);
        final Button cancel = (Button) loginDialog.findViewById(R.id.cancel_button);
        final Button loginButton = (Button) loginDialog.findViewById(R.id.login_button);


        //Closes app if they try to back out of dialog
        loginDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //setting up date and time on Home_Fragment before closing app
                preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                //add current date to preferences for next app opening
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTimeStamp = dateFormat.format(new Date()); // Find todays date
                Log.d("Date",currentTimeStamp);//log the time stamp
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("pref_date", currentTimeStamp);//added date to preferences for next app open
                edit.commit();
                finish();//close app
            }
        });
        //Listener for loginDialog button
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                Log.d("Message", "Login button clicked");
                final loginlogoff liloobj = new loginlogoff(maContext);//passed in context of this activity
                liloobj.setHostname(hostname.getText().toString());
                liloobj.setDomain(domain.getText().toString());
                liloobj.setPortnumber(Integer.parseInt(port.getText().toString()));
                liloobj.setUsername(username.getText().toString());
                liloobj.setPassword(password.getText().toString());

                progress = ProgressDialog.show(maContext, "Logging in...", "Please Wait", true);

                new Thread(new Runnable() {
                    public void run() {
                        final ReqTask reqobj = new ReqTask(liloobj.httpstringcreate(),//send login query to CI via asynctask
                                this.getClass().getName(), maContext);
                        try {
                            reqobj.execute().get(LOGIN_TIMEOUT, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            ToastMessageTask tmtask = new ToastMessageTask(maContext, "Logon attempt timed out.");
                            tmtask.execute();
                            e.printStackTrace();
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();

                                XmlParser xobj3 = new XmlParser();
                                try {
                                    xobj3.parseXMLfunc(reqobj.getResult());
                                } catch (XmlPullParserException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Log.d("Variable","reqobj.getResult() value is: " + reqobj.getResult());
                                setLogonXmlTextTags(xobj3.getTextTag());
                                //check if login worked
                                loginlogoff lobj = new loginlogoff(maContext);
                                lobj.isLoginSuccessful(reqobj);//check if login was successful
                                lobj.logonMessage(reqobj);//show status of login
                                if(lobj.getLogin_successful()){//if login is true,dismiss login screen
                                    loginDialog.dismiss();
                                }

                            }
                        });//end of UiThread
                    }
                }).start();
            }
        });
        //Listener for Cancel Button
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                finish();
            }
        });

    }//end of oncreate

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Log.d("Variable", "Value of argument position: " + position);
        Fragment fragment = new Home_Fragment();


        if(getFirst_open()){//if first time opening app, show home screen fragment
            position = -1;
            setFirst_open(false);
        }
        FragmentManager fragmentManager = getFragmentManager();
        switch(position) {
            case 0:
                fragment = new CapturePDF_Fragment();
                break;
            case 1:
                fragment = new Browse_Fragment();
                //fragment = new UploadPDF_Fragment();
                // Make dialog box visible when uploadPDF_Fragment is opened.
                loginDialog.show();
                break;
            case 2:
                fragment = new DownloadPDF_Fragment();
                break;
            default:
                fragment = new Home_Fragment();

                break;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                        //.replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
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
            return true;
        }
        if(id == R.id.action_logoff) {
            final loginlogoff liloobj2 = new loginlogoff(maContext);
            new Thread(new Runnable() {
                public void run() {
                    ReqTask reqobj4 = new ReqTask(liloobj2.logoffQuery(), this.getClass().getName(), maContext);
                    XmlParser xobj4 = new XmlParser();
                    try {
                        reqobj4.execute().get(LOGOFF_TIMEOUT,TimeUnit.MILLISECONDS);
                        xobj4.parseXMLfunc(reqobj4.getResult());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    liloobj2.isLogoffSuccessful(xobj4.getTextTag());
                    liloobj2.logoffMessage();
                }
            }).start();
        }

        return super.onOptionsItemSelected(item);
    }

}//end of MainActivity