package com.ameraz.android.cipdfcapture.app;
/**
 * Created by adrian.meraz on 6/27/2014.
 */
import android.util.Log;

import java.util.Arrays;
import java.util.List;

//form of incoming arguments - argname.argvalue - the "." delimits the string
public class QueryFormer {
    public String formQuery(String... args){
        String appender = "";
        for(String arg : args){
            if(arg != null) {// if the argument is empty(not null), leave it like this: argname.
                List<String> argList = Arrays.asList(arg.split("."));
                appender += "&" + argList.get(0) + "=" + argList.get(1);
                Log.d("Variable", "appender value: " + appender);
                argList.clear();//clear out the list after using it
            }
        }
        Log.d("Variable", "total appender value: " + appender);
        return appender;
    }
}
