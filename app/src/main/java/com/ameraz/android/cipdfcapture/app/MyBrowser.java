package com.ameraz.android.cipdfcapture.app;

/**
 * Created by adrian.meraz on 9/22/2014.
 */

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MyBrowser extends WebViewClient {
    boolean loadingFinished = true;
    boolean redirect = false;
    ProgressDialog ringProgressDialog;

    public MyBrowser(ProgressDialog pd) {
        ringProgressDialog = pd;
    }

    private void setLoadingViewDialog() {
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Loading View ...");
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
        if (!loadingFinished) {
            redirect = true;
        }
        loadingFinished = false;
        view.loadUrl(urlNewString);
        return true;
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        loadingFinished = false;
        Log.d("onPageStarted()", "WebView is starting to load");
        setLoadingViewDialog();//prepare dialog to show downloading view message
        ringProgressDialog.show();
        //SHOW LOADING IF IT ISNT ALREADY VISIBLE
    }

    public void onPageFinished(WebView view, String url) {
        if (!redirect) {
            loadingFinished = true;
        }
        if (loadingFinished && !redirect) {
            //HIDE LOADING IT HAS FINISHED
        } else {
            redirect = false;
        }
        Log.d("onPageFinished()", "WebView is done loading");
        ringProgressDialog.dismiss();//dismiss loading screen after webView is done loading
        // do your stuff here
    }


}

