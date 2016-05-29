package nu.info.zeeshan.rnf;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.model.NYTResult;
import nu.info.zeeshan.rnf.util.FetchTaskUICallbacks;
import nu.info.zeeshan.rnf.util.Util;
import retrofit2.Response;

/**
 * Created by Zeeshan Khan on 5/28/2016.
 */
public class FetchNewsTask extends AsyncTask<Void, Void, Boolean> {
    public static final String TAG = FetchNewsTask.class.getSimpleName();
    private Context context;
    private FetchTaskUICallbacks fetchTaskUICallbacks;

    FetchNewsTask(Context context, FetchTaskUICallbacks fetchTaskUICallbacks) {
        this.context = context;
        this.fetchTaskUICallbacks = fetchTaskUICallbacks;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        NYTApiClient client = ClientGenerator.getNYTClient();
        boolean result=false;
        try {
            Response<NYTResult> response = client.home().execute();
            if (response != null && response.isSuccessful()) {
                Util.log(TAG, "fetching news successful" + response.body().getResults());
                List<Item> newsItems = new ArrayList<Item>();
                newsItems.addAll(response.body().getResults());
                Util.fillDb(context, newsItems);
                result=true;
            } else {
                Util.log(TAG, "fetching news failed gracefully");
                result=false;
            }
        } catch (IOException ex) {
            Util.log(TAG, "error occured while fetching news" + ex.getLocalizedMessage());
            result=false;
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        fetchTaskUICallbacks.taskComplete(result);
    }
}
