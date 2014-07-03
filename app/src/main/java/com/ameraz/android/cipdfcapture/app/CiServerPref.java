package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by adrian.meraz on 7/3/2014.
 */

public class CiServerPref extends ListPreference {

    public CiServerPref(Context context) {
    //public CiServerPref(Context context, AttributeSet attrs) {
        super(context);
        //super(context, attrs);
        DatabaseHandler dbh = new DatabaseHandler(context);

        /*BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
        CharSequence[] entries = new CharSequence[pairedDevices.size()];
        CharSequence[] entryValues = new CharSequence[pairedDevices.size()];
        int i = 0;
        for (BluetoothDevice dev : pairedDevices) {
            entries[i] = dev.getName();
            entryValues[i] = dev.getAddress();
            i++;
    }*/
        String result = "";
        String[] entries = dbh.list_ci_servers();
        for(String s : entries) {
            result.concat(s + " ");
        }
        Log.d("Message","Contents of entries" + result);
        setEntries(entries);

        //setEntryValues(entryValues);
    }
}

