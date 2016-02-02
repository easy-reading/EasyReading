package nu.info.zeeshan.rnf;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import nu.info.zeeshan.rnf.dao.DbConstants;
import nu.info.zeeshan.rnf.dao.DbHelper;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.model.NewsItem;
import nu.info.zeeshan.rnf.util.Constants;
import nu.info.zeeshan.rnf.util.Util;

/**
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentNews extends FragmentMain {
    public static String TAG = "FragmentNews";
    private RequestQueue reqQueue;
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.log(TAG, error + "");
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity().getApplicationContext(), "Error occured. Try again!!", Toast.LENGTH_SHORT).show();
            stopRefresh();
        }
    };
    private Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            List<Item> feeds = new ArrayList<>();
            try {
                if (response != null) {
                    JSONObject jobj = new JSONObject(response);
                    jobj = jobj.optJSONObject("responseData");
                    if (jobj != null) {
                        JSONArray jarr = jobj.optJSONArray("results");
                        if (jarr != null && jarr.length() > 0) {
                            DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                            String dateString;
                            for (int i = 0; i < jarr.length(); i++) {
                                jobj = jarr.getJSONObject(i);
                                NewsItem item = new NewsItem();
                                item.setTitle(Html.fromHtml(jobj.optString("title")).toString());
                                item.setDesc(Html.fromHtml(jobj.optString("content")).toString());
                                if (jobj.has("image"))
                                    item.setImage_url(jobj.optJSONObject("image").optString("url"));
                                Util.log(TAG, "whole: " + jobj.toString());
                                dateString = jobj.optString("publishedDate");
                                if (dateString != null) {
                                    try {
                                        Date date = dateFormat.parse(dateString);
                                        item.setTime(date.getTime());
                                    } catch (ParseException ex) {
                                        Util.log(TAG, "invalid date format");
                                    }
                                }
                                Util.log(TAG, "date: " + jobj.optString("publishedDate"));
                                item.setLink(jobj.optString("unescapedUrl"));
                                item.setPublisher(jobj.optString("publisher"));
                                feeds.add(item);
                            }
                        } else {
                            Util.log(TAG, "array is null or have nothing in it");
                        }
                    } else {
                        Util.log(TAG, "no responseData");
                    }
                } else {
                    Util.log(TAG, "empty response from server");
                }
            } catch (JSONException ex) {
                Util.log(TAG, "exp in response parsing" + ex.getLocalizedMessage());
            }
            Util.log(TAG, feeds.size() + " -> " + feeds);
            fillAdapter(feeds);
            swipeRefreshLayout.setRefreshing(false);
            stopRefresh();
        }
    };

    public void startFetchingFeed() {
        SharedPreferences spf = getActivity().getSharedPreferences(getString(R.string.pref_filename), Context.MODE_PRIVATE);
        Set<String> interests = spf.getStringSet(getString(R.string.pref_news_keywords), Constants.DEFAULT_NEWS_KEYWORDS);
        for (String s : interests) {
            StringRequest strReq = new StringRequest(Constants.News.URL + "&q=india," + s, responseListener, errorListener);
            getReqQueue().add(strReq);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            new DbHelper(getContext()).markAllAs(DbConstants.State.READ, DbConstants.Type.NEWS);
        } catch (Exception ex) {
            Util.log(TAG, ex.getMessage() + "");
        }
        fillAdapter(null);
    }

    private RequestQueue getReqQueue() {
        if (reqQueue == null)
            reqQueue = Volley.newRequestQueue(getContext());
        return reqQueue;
    }

    public FragmentNews() {
    }

    @Override
    protected void fillAdapter(List<Item> items) {
        DbHelper dbh = new DbHelper(getActivity());
        if (items != null && items.size() > 0) {
            List<NewsItem> newsItems = new ArrayList<>();
            for (Item i : items) {
                newsItems.add((NewsItem) i);
            }
            dbh.fillNewsFeed(newsItems);
        }
        super.fillAdapter(dbh.getNewsFeeds(true));
    }
}
