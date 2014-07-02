package com.ameraz.android.cipdfcapture.app;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * Created by The Bat Cave on 7/1/2014.
 */
public class Database extends SQLiteOpenHelper {

    final static int DB_VERSION = 1;
    final static String DB_NAME = "mydb.s3db";
    Context context;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        executeSQLScript(database, "create.sql");
    }

    private void executeSQLScript(SQLiteDatabase database, String dbname) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;

        try{
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
                }
            }
        } catch (IOException e){
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
    public String select_ci_server(SQLiteDatabase db,String cis){
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
        return result;
    }

}
