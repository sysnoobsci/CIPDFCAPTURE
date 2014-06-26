package com.ameraz.android.cipdfcapture.app.filebrowser;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ameraz.android.cipdfcapture.app.R;

public class Browse_Fragment extends Fragment {
 
	private static final int REQUEST_PATH = 1;
 
	String curFileName;
    static View rootView;
	EditText edittext;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater .inflate(R.layout.fragment_fileexplorer, container, false);
        edittext = (EditText)rootView.findViewById(R.id.editText);
        return rootView;
    }

    public void getfile(View view){ 
    	Intent intent1 = new Intent(getActivity(), FileChooser.class);
        startActivityForResult(intent1,REQUEST_PATH);
    }
    // Listen for results.
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
    	if (requestCode == REQUEST_PATH){
    		if (resultCode == Activity.RESULT_OK) {
    			curFileName = data.getStringExtra("GetFileName"); 
            	edittext.setText(curFileName);
    		}
    	 }
    }
}
