package com.ameraz.android.cipdfcapture.app;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 9/8/2014.
 */
public class QueryArguments {
    static private ArrayList<Object> argslist = new ArrayList<Object>();

    static ArrayList<Object> getArgslist() {
        return argslist;
    }

    static void addArg(Object arg) {
        argslist.add(arg);
    }

    static void clearList() {
        argslist.clear();
    }
}
