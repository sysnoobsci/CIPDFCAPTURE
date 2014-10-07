package com.ameraz.android.cipdfcapture.app.filebrowser;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by john.williams on 10/3/2014.
 */
public class FileBrowserExpandableList extends BaseExpandableListAdapter {



    private Context context;
    // header titles
    private List<String> file_item;
    // child data in HashMap of header title, child title
    private HashMap<String, List<String>> standard_options;

    public FileBrowserExpandableList(Context context, List<String> file_item, HashMap<String, List<String>> standard_options){
        this.context = context;
        this.file_item = file_item;
        this.standard_options = standard_options;
    }

    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
