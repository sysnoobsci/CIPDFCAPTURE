package com.ameraz.android.cipdfcapture.app.SupportingClasses;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * Created by adrian.meraz on 7/3/2014.
 */

public class CiServerPref extends ListPreference {
    DatabaseHandler dbh;

    public CiServerPref(Context context, AttributeSet attrs) {
        super(context, attrs);
        dbh = new DatabaseHandler(context);
        setEntries(dbh.list_ci_servers());
        setEntryValues(dbh.list_ci_servers());
    }//end of CiServerPref Constructor

    public void setEntries(CharSequence[] sequence) {
        CharSequence[] entries  = sequence;
        super.setEntries(entries);
    }

    @Override
    public void setEntryValues(CharSequence[] sequence) {
        CharSequence[] values = sequence;
        super.setEntryValues(values);
    }

    @Override
    protected void onClick() {
        setEntries(dbh.list_ci_servers());
        setEntryValues(dbh.list_ci_servers());
        super.onClick();
    }
}

