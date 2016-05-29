package nu.info.zeeshan.rnf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import nu.info.zeeshan.rnf.data.FeedLoader;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.util.FetchTaskUICallbacks;
import nu.info.zeeshan.rnf.util.Util;

/**
 * FragmentNews
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentNews extends FragmentMain implements LoaderManager
        .LoaderCallbacks<List<Item>>, FetchTaskUICallbacks {
    public static String TAG = FragmentNews.class.getSimpleName();
    public static final int NEWS_FEED_LOADER_ID = 1;

    public FragmentNews() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(NEWS_FEED_LOADER_ID, null, this);
    }

    public void startFetchingFeed() {
        Util.log(TAG, "fetching news");
        new FetchNewsTask(getActivity(), this).execute();
    }

    @Override
    public Loader<List<Item>> onCreateLoader(int id, Bundle args) {
        Util.log(TAG, "loader called");
        return new FeedLoader(getActivity(), FeedLoader.TYPE.NEWS);
    }

    @Override
    public void onLoadFinished(Loader<List<Item>> loader, List<Item> data) {
        Util.log(TAG, "data loaded by loader: " + data);
        itemAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Item>> loader) {
        itemAdapter.addAll(null);
    }

    @Override
    public void taskComplete(boolean wasSuccessful) {
        stopRefresh();
        if (wasSuccessful) {
            getLoaderManager().restartLoader(NEWS_FEED_LOADER_ID, null, this);
        } else {
            Util.log(TAG, "fetch weather task failed ui update");
        }
    }
}
