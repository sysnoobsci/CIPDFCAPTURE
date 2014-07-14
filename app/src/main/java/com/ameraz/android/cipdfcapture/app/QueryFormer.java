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
                if (arg.contains(",")) {
                    String[] argList = arg.split(",");
                    if(argList[1] != null && !argList[1].equals("null")) {// if the value in the argument is empty(not null), leave it like this: argname.
                        if(argList.length > 2){//if arg contains multiple key-value pairs
                            int j = 0;
                            int k = 1;
                            for(String piece : argList){
                                Log.d("Variable", "Value of arg - formQuery(): " + arg);
                                Log.d("Variable", "argList[" + j + "] is " + argList[j]);
                                Log.d("Variable", "argList[" + k + "] is " + argList[k]);
                                appender += "&" + argList[j] + "=" + argList[k];
                                Log.d("Variable", "appender value: " + appender);
                                j+=2;
                                k+=2;
                            }
                        }
                        else if(argList.length < 3) {//if arg contains a single key-value pair
                            // Split it.
                            Log.d("Variable", "Value of arg - formQuery(): " + arg);
                            Log.d("Variable", "argList[0] is " + argList[0]);
                            Log.d("Variable", "argList[1] is " + argList[1]);
                            appender += "&" + argList[0] + "=" + argList[1];
                            Log.d("Variable", "appender value: " + appender);
                        }
                    }
                    else{
                        Log.d("Message", arg + " omitted since value of variable was null");
                    }
                }
                else {
                    Log.e("Error","arg does not contain ','");
                }
        }
        Log.d("Variable", "total appender value: " + appender);
        return appender;
    }

}
