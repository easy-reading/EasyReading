package nu.info.zeeshan.rnf.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Zeeshan Khan on 11/3/2015.
 */
public interface Constants {
    public interface News {
        String URL = "https://ajax.googleapis.com/ajax/services/search/news?v=1.0&rsz=8";
    }

    boolean DEBUG = false;
    Set<String> DEFAULT_NEWS_KEYWORDS = new HashSet<String>(Arrays.asList("latest"));
    String DEFAULT_FEED_LIMIT = "50";
    String DEFAULT_UPDATE_INTERVAL_IN_HOURS = "2";
    Set<String> FACEBOOK_PERMISSIONS = new HashSet<String>(Arrays.asList("user_posts", "user_actions.news"));

    String EMPTY_FEED = "nothing";
    String pref_filename = "nu.info.zeeshan.preference_file";
    String pref_facebookrss = "facebook_rss";
    String pref_newsrss = "news_rss";
    String pref_limit = "feed_limit";
    String pref_update_interval = "update_interval";

    public interface FacebookFeed {
        String PARAMS = "name,story,description,link,message,created_time,object_id,likes,picture";
        String NODE = "/me/feed";
    }
    interface ItemViewType {
        int NORMAL=1;
        int EXPANDED=3;
        int AD=2;
    }
    interface ItemType{
        int NEWS=1;
        int FACEBOOK=3;
    }
}
