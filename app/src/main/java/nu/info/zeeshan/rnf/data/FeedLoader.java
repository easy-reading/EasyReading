package nu.info.zeeshan.rnf.data;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.util.Util;

/**
 * Created by Zeeshan Khan on 5/28/2016.
 */
public class FeedLoader extends AsyncTaskLoader<List<Item>> {
    private List<Item> data;
    private int type;
    public static final String TAG = FeedLoader.class.getSimpleName();

    public interface TYPE {
        int FACEBOOK = 1;
        int NEWS = 2;

    }

    public FeedLoader(Context context, int type) {
        super(context);
        this.type = type;
    }

    @Override
    protected void onStartLoading() {
        if (data != null) {
            deliverResult(data);
        } else {
            forceLoad();
        }
    }

    @Override
    public void stopLoading() {
        cancelLoad();
    }

    @Override
    public List<Item> loadInBackground() {
        Util.log(TAG, "loading data in background");
        FeedsDbHelper dbHelper = new FeedsDbHelper(getContext());
        switch (type) {
            case TYPE.FACEBOOK:
                return dbHelper.getFacebookFeeds(false);
            case TYPE.NEWS:
                return dbHelper.getNewsFeeds(false);
            default:
                return null;
        }
    }

    @Override
    public void deliverResult(List<Item> data) {
        this.data = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
}
