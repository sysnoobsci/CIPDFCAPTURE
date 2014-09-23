package com.ameraz.android.cipdfcapture.app.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMessageTask;
import com.ameraz.android.cipdfcapture.app.MyBrowser;
import com.ameraz.android.cipdfcapture.app.R;

import java.io.File;

/**
 * Created by adrian.meraz on 9/18/2014.
 */
public class Image_Preview_Fragment extends Fragment {

    static View rootView;
    WebView webView;
    ImageButton saveButton;
    Context context;
    String uri;
    ProgressDialog ringProgressDialog;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_image_preview, container, false);
        setContext(getActivity());
        instantiateViews();
        loadImage();
        saveButtonListener();
        return rootView;
    }

    public void instantiateViews() {
        webView = (WebView) rootView.findViewById(R.id.webView);
        saveButton = (ImageButton) rootView.findViewById(R.id.download_and_save);
    }

    public void loadImage() {
        Bundle bundle = this.getArguments();
        uri = bundle.getString("retrieve_url");
        //webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//turns off hardware accelerated canvas
        setWebViewSettings(webView);
        webView.loadUrl(uri);
    }

    public void setWebViewSettings(WebView webView) {
        webView.setWebViewClient(new MyBrowser(ringProgressDialog));
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setInitialScale(1);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//load the image from cache
    }

    private void saveButtonListener() {//searches for the report and displays the versions
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ToastMessageTask tmtask = new ToastMessageTask(getContext(), "Save button pressed");
                    tmtask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
