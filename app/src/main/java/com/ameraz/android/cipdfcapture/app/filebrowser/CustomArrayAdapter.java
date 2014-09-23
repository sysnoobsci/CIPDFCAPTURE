package com.ameraz.android.cipdfcapture.app.filebrowser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ameraz.android.cipdfcapture.app.R;

import java.util.ArrayList;
import java.util.List;


public class CustomArrayAdapter extends BaseAdapter {

	private Context c;
	private ArrayList<Item> data = null;
	
	public CustomArrayAdapter(Context context,
			ArrayList<Item> data) {
		c = context;
		this.data = data;
	}

    @Override
    public int getCount() {
        return data.size();
    }

    public Item getItem(int i)
	 {
		 return data.get(i);
	 }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
       public View getView(int position, View convertView, ViewGroup parent) {
               View v = convertView;
               if (v == null) {
                   LayoutInflater vi = (LayoutInflater)c.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   v = vi.inflate(R.layout.file_view, null);
               }

               TextView t1 = (TextView) v.findViewById(R.id.TextView01);
               TextView t2 = (TextView) v.findViewById(R.id.TextView02);
               TextView t3 = (TextView) v.findViewById(R.id.TextViewDate);
	           ImageView fileTypeImage = (ImageView) v.findViewById(R.id.fd_Icon1);

               Item items = data.get(position);
               t1.setText(items.getName());
               t2.setText(items.getData());
               t3.setText(items.getDate());
               fileTypeImage.setImageDrawable(c.getResources().getDrawable(items.getImage()));
        Log.d("Array adapter getName()=", items.getName());
               return v;
       }

}
