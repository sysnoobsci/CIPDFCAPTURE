package com.ameraz.android.cipdfcapture.app;

/**
 * Created by adrian.meraz on 9/19/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ameraz.android.cipdfcapture.app.AsyncTasks.ToastMessageTask;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;
import java.io.FileNotFoundException;

import static java.lang.String.format;

//import com.joanzapata.pdfview.sample.R;


public class PDFViewActivity extends Activity implements OnPageChangeListener {
    public static final String SAMPLE_FILE = "sample.pdf";
    public static final String ABOUT_FILE = "about.pdf";
    Context context;
    PDFView pdfView;
    String pdfName = SAMPLE_FILE;
    Integer pageNumber = 1;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        setContext(getApplicationContext());
        instantiateViews();
        try {
            afterViews();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }//end of oncreate

    void instantiateViews() {
        pdfView = (PDFView) findViewById(R.id.pdfView);

    }

    void afterViews() throws FileNotFoundException {
        display(pdfName, false);
    }

    public void about() throws FileNotFoundException {
        if (!displaying(ABOUT_FILE))
            display(ABOUT_FILE, true);
    }

    private void display(String assetFileName, boolean jumpToFirstPage) throws FileNotFoundException {
        if (jumpToFirstPage) pageNumber = 1;
        setTitle(pdfName = assetFileName);
        File testpdf = new File(FilePath.getSyswareFilePath() + "sys1404932462039.pdf");
        Log.d("display()", "opening " + FilePath.getSyswareFilePath() + "sys1404932462039.pdf");
        //FileInputStream fis = openFileInput(testpdf);
        pdfView.fromFile(testpdf)//test loading frim
                .defaultPage(pageNumber)
                .onPageChange(this)
                .load();
        /*pdfView.fromAsset(assetFileName)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .load();
        */
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(format("%s %s / %s", pdfName, page, pageCount));
    }

    @Override
    public void onBackPressed() {
        if (ABOUT_FILE.equals(pdfName)) {
            try {
                display(SAMPLE_FILE, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            super.onBackPressed();
        }
    }

    private boolean displaying(String fileName) {
        return fileName.equals(pdfName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            ToastMessageTask tmtask = new ToastMessageTask(getContext(), "action_setting pressed");
            tmtask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
