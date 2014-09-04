package com.ameraz.android.cipdfcapture.app;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.UrlConnectionDownloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by john.williams on 9/2/2014.
 */
public class TestDownload extends Fragment {

    Context maContext;
    ImageView testView;
    Button testButton;
    Button loginButton;
    LoginLogoff liloobj;
    String jsid;
    String sid;
    Bitmap myImage;
    int count;
    Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.test_download, container, false);
        maContext = getActivity();
        testView = (ImageView)rootView.findViewById(R.id.test_image_view);
        testButton = (Button)rootView.findViewById(R.id.test_button);
        loginButton = (Button)rootView.findViewById(R.id.test_login);
        buttonListener();
        loginListener();
        count = 0;
        return rootView;
    }

    private void loginListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liloobj = new LoginLogoff(getActivity());
                try{
                    liloobj.tryLogin();
                }catch(Exception e){
                    e.printStackTrace();
                    Log.d("Ping: ", "failed ping check");
                }
                jsid= liloobj.getJSid();
                sid = liloobj.getSid();
                //testView.loadUrl("http://172.16.11.53:34544/ci?action=retrieve&tid=V~1~4A584150492020202014237F0175AF360001~/QA/PHONE.CAPTURE~4&sid=" + sid;// + "&jsid=" + jsid);
            }
        });
    }

    private void buttonListener() {
        //+ "&sessionid=" + sid
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(maContext)
                        .load("http://172.16.11.53:34544/ci?action=retrieve&tid=V~1~4A584150492020202014237F0175AF360001~/QA/PHONE.CAPTURE~4&sid=" + sid)
                        .rotate(90)
                        .fit()
                        .centerInside()
                        .into(testView);

            }
        });
    }

    public void readStream(InputStream in){
        //myImage =
        FilePath fp = new FilePath();
        try {
            in = new FileInputStream(fp.getFilePath() + "downloaded" + count + ".jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        count++;
        myImage = BitmapFactory.decodeFile(fp.getFilePath() + "downloaded" + count + ".jpg");
        imageUri.parse("file://" + fp.getFilePath() + "downloaded" + count + ".jpg");
    }

/*    private class doingStuff extends AsyncTask {
        //ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //dialog = ProgressDialog.show(getActivity(), "", "Doing Stuff", false);
        }

        @Override
        protected Object doInBackground(Object[] params) {


                //URL src = new URL("http://jwilliams:34544/ci?action=retrieve&tid=V~1~4A584150492020202014237F0175AF360001~/QA/PHONE.CAPTURE~4");
            testView.setBackgroundColor(0);
            testView.loadDataWithBaseURL("","<img src='http://jwilliams:34544/ci?action=retrieve&tid=V~1~4A584150492020202014237F0175AF360001~/QA/PHONE.CAPTURE~4'/>","jpeg", "UTF-8", "");
            return null;
        }


        @Override
        protected void onPostExecute(Object result) {
            //dialog.dismiss();
            Picasso.with(maContext)
                    .load(myImage)
                    .fit()
                    .centerInside()
                    .into(testView);
        }
    }*/


}
