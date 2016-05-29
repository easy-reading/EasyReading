package nu.info.zeeshan.rnf.util;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nu.info.zeeshan.rnf.data.FeedsDbHelper;
import nu.info.zeeshan.rnf.model.FacebookItem;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.model.NewsItem;

/**
 * Created by Zeeshan Khan on 11/6/2015.
 */
public class Util {
    public static void fillDb(Context context, List<Item> items) {
        if (items != null && items.size() > 0) {
            FeedsDbHelper dbh = new FeedsDbHelper(context);
            Item tmpItem = items.get(0);
            if (tmpItem instanceof FacebookItem) {
                List<FacebookItem> fbItems = new ArrayList<FacebookItem>();
                for (Item i : items) {
                    fbItems.add((FacebookItem) i);
                }
                dbh.fillFacebookFeed(fbItems);
            } else if (tmpItem instanceof NewsItem) {
                List<NewsItem> newsItems = new ArrayList<>();
                for (Item i : items) {
                    newsItems.add((NewsItem) i);
                }
                dbh.fillNewsFeed(newsItems);
            }
        }
    }

    public static void log(String TAG, String msg) {
        if (Constants.DEBUG)
            Log.d(TAG, msg);
    }
}
