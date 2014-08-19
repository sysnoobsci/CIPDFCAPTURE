package com.ameraz.android.cipdfcapture.app;
/**
 * Created by adrian.meraz on 6/27/2014.
 */
import android.util.Log;

import java.util.Arrays;
import java.util.List;

//form of incoming arguments - argname,argvalue - the "," delimits the string
public class QueryFormer {
    public String formQuery(String... args){
        StringBuilder appender = new StringBuilder();
        for(String arg : args){
            Log.d("Variable", "Value of arg - formQuery(): " + arg);
                if (arg.contains(",")) {
                    String[] argList = arg.split(",");
                    //see what the contents of argList are
                    int i = 0;
                    for(String piece : argList){
                        Log.d("argList", "value of argList[" + i + "]: " + piece);
                        i++;
                    }
                    if(argList.length > 2){//if arg contains multiple key-value pairs
                        int k = 0;
                        for(int j = 0;j < argList.length-1; j++){
                            k = j + 1;
                            Log.d("Variable","Value of j: " + j + " Value of k: " + k);
                            Log.d("Variable", "argList[" + j + "] is " + argList[j]);
                            Log.d("Variable", "argList[" + k + "] is " + argList[k]);
                            if(argList[k] != null && !argList[k].equals("null")){// if the value in the argument is empty(not null), leave it like this: argname.
                                appender.append("&" + argList[j] + "=" + argList[k]);
                            }
                            else{
                                //do nothing, don't append null valued keys
                                Log.d("Message", arg + " omitted since value of variable was null");
                            }
                            Log.d("Variable", "appender value: " + appender);
                            j+=2;
                        }
                    }
                    else if(argList.length < 3) {//if arg contains a single key-value pair
                        // Split it.
                        Log.d("Message","argList.length < 3");
                        Log.d("Variable", "argList[0] is " + argList[0]);
                        Log.d("Variable", "argList[1] is " + argList[1]);
                        if(argList[1] != null && !argList[1].equals("null")){// if the value in the argument is empty(not null), leave it like this: argname.
                            appender.append("&" + argList[0] + "=" + argList[1]);
                        }
                        else{
                            //do nothing, don't append null valued keys
                            Log.d("Message", arg + " omitted since value of variable was null");
                        }
                        Log.d("Variable", "appender value: " + appender);
                    }
                }
                else {
                    Log.e("Error","arg does not contain ','");
                }
        }
        Log.d("Variable", "total appender value: " + appender);
        return appender.toString();
    }

}
