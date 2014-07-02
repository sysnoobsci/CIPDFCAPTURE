package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by The Bat Cave on 7/1/2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    final static int DB_VERSION = 1;
    final static String DB_NAME = "config_table";
    final static String SCRIPT_NAME = "create.sql";
    Context context;


    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d("Message", "DatabaseHandler.oncreate() executed");
        executeSQLScript(database, SCRIPT_NAME);
    }


    private void executeSQLScript(SQLiteDatabase database, String dbname) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;

        try {
            inputStream = assetManager.open(dbname);
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            String[] createScript = outputStream.toString().split(";");
            for (int i = 0; i < createScript.length; i++) {
                String sqlStatement = createScript[i].trim();
                // TODO You may want to parse out comments here
                if (sqlStatement.length() > 0) {
                    database.execSQL(sqlStatement + ";");
                    Log.d("Message", "SQL script executed.");
                }
            }
        } catch (IOException e) {
            // TODO Handle Script Failed to Load
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            switch (oldVersion) {
                case 1:
                    executeSQLScript(db, "update_v2.sql");
                case 2:
                    executeSQLScript(db, "update_v3.sql");
            }
        }
    }

    public String select_ci_server(String cis) {
        SQLiteDatabase db = this.getReadableDatabase();
        String ciserverQuery = "SELECT * FROM config_table WHERE Ciprofile = " + cis + ";";
        Cursor cursor = db.rawQuery(ciserverQuery, null);
        int i = 0;
        String result = "";
        while (cursor.moveToNext()) {
            result.concat(cursor.getString(i) + ",");
            i++;
        }
        cursor.close();
        db.close();
        Log.d("Variable", "Value of select_ci_server() result: " + result);
        return result;
    }

    public String add_ci_server(String tablename, ArrayList<String> slist) {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = "";
        if (!slist.isEmpty()) {//check if list is empty first

            String ctcols = " (Ciprofile, Hostname, Domain, Portnumber, Username, Password)";

            String ciprofile = "'" + slist.get(0) + "'";
            String hostname = "'" + slist.get(1) + "'";
            String domain = "'" + slist.get(2) + "'";
            String port = "'" + slist.get(3) + "'";
            String username = "'" + slist.get(4) + "'";
            String password = "'" + slist.get(5) + "'";

            String colvals = "(" + ciprofile + "," + hostname + "," + domain + "," + port + "," +
                    username + "," + password + ")";

            String insertciserverQuery = "INSERT INTO " + tablename + ctcols + "VALUES " + colvals;
            Cursor cursor = db.rawQuery(insertciserverQuery, null);
            int i = 0;

            while (cursor.moveToNext()) {
                result.concat(cursor.getString(i) + ",");
                i++;
            }
            cursor.close();
            db.close();
            Log.d("Variable", "Value of result: " + result);
        }
        return result;

    }
}
