package com.ameraz.android.cipdfcapture.app;
/**
 * Created by adrian.meraz on 6/27/2014.
 */
import java.util.Arrays;
import java.util.List;

//form of incoming arguments - argname.argvalue - the "." delimits the string
public class QueryFormer {
    public String formQuery(String... args){
        String appender = "";
        for(String arg : args){
            if(arg != null) {// if the argument is null, leave it out(arg.isEmpty() is added with an empty string as the value)
                List<String> argList = Arrays.asList(arg.split("."));
                appender += "&" + argList.get(0) + "=" + argList.get(1);
                argList.clear();//clear out the list after using it
            }
        }
        return appender;
    }
}
