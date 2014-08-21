import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by The Bat Cave on 8/20/2014.
 */
public class ProgressDialogTask extends AsyncTask<String, Void, String> {

    Context maContext;
    ProgressDialog pd;

    public ProgressDialogTask(Context maContext){
         setMaContext(maContext);
    }

    public Context getMaContext() {
        return maContext;
    }

    public void setMaContext(Context maContext) {
        this.maContext = maContext;
    }

    protected void onPreExecute() {
        pd = ProgressDialog.show(getMaContext(), "", "Loading. Please wait...", true);
    }

    protected String doInBackground(String... urls) {

        return "success";
    }

    protected void onPostExecute(String result) {
        pd.dismiss();
        Log.d("Message","ProgressDialog: " + result);
    }
}
