package nu.info.zeeshan.rnf.util;

/**
 * Created by Zeeshan Khan on 11/3/2015.
 */
public interface Constants {
    public interface URL {
        String NEWS = "https://ajax.googleapis.com/ajax/services/search/news?v=1.0&rsz=8&q=";
        String FB = "";

    }

    String DEFAULT_FEED_LIMIT = "50";
    String DEFAULT_UPDATE_INTERVAL_IN_HOURS = "2";
    String EMPTY_FEED = "nothing";

    String pref_filename = "nu.info.zeeshan.preference_file";
    String pref_facebookrss = "facebook_rss";
    String pref_newsrss = "news_rss";
    String pref_limit = "feed_limit";
    String pref_update_interval = "update_interval";
}
