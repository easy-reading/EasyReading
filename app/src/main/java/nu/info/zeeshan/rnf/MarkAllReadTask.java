package nu.info.zeeshan.rnf;

import android.content.Context;
import android.os.AsyncTask;

import nu.info.zeeshan.rnf.data.DbConstants;
import nu.info.zeeshan.rnf.data.FeedsDbHelper;
import nu.info.zeeshan.rnf.util.Util;

/**
 * TODO: implement working of this thing in Loaders update method
 * Created by Zeeshan Khan on 5/28/2016.
 */
public class MarkAllReadTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    public static final String TAG = MarkAllReadTask.class.getSimpleName();

    public MarkAllReadTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            new FeedsDbHelper(context).markAllAs(DbConstants.State.READ, DbConstants.Type.FB);
        } catch (FeedsDbHelper.InvalidStateException inEx) {
            Util.log(TAG, "Invalid state of feed item encountered");
        } catch (FeedsDbHelper.InvalidTypeException inTy) {
            Util.log(TAG, "Invalid type of feed item encountered");
        }
        return null;
    }
}
