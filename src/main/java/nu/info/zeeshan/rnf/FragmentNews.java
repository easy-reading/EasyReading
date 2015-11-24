package nu.info.zeeshan.rnf;

import android.text.Html;
import android.util.Log;

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

import nu.info.zeeshan.rnf.dao.DbHelper;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.model.NewsItem;
import nu.info.zeeshan.rnf.util.Constants;

/**
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentNews extends FragmentMain {
    public static String TAG = "FragmentNews";
    private RequestQueue reqQueue;


    public void startFetchingFeed() {

        StringRequest strReq = new StringRequest(Constants.URL.NEWS + "&q=india", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<Item> feeds = new ArrayList<>();
                try {
                    JSONObject jobj = new JSONObject(response);
                    jobj = jobj.optJSONObject("responseData");
                    JSONArray jarr = jobj.getJSONArray("results");
                    DateFormat dateFormat=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                    String dateString;
                    for (int i = 0; i < jarr.length(); i++) {
                        jobj = jarr.getJSONObject(i);
                        NewsItem item = new NewsItem();
                        item.setTitle(Html.fromHtml(jobj.optString("title")).toString());
                        item.setDesc(Html.fromHtml(jobj.optString("content")).toString());
                        if (jobj.has("image"))
                            item.setImage_url(jobj.optJSONObject("image").optString("tbUrl"));
                        Log.d(TAG,"whole: "+jobj.toString());
                        dateString=jobj.optString("publishedDate");
                        if(dateString!=null){
                            try {
                                Date date = dateFormat.parse(dateString);
                                item.setTime(date.getTime());
                            }catch(ParseException ex){
                                Log.d(TAG,"invalid date format");
                            }
                        }
                        Log.d(TAG, "date: " + jobj.optString("publishedDate"));
                        item.setLink(jobj.optString("unescapedUrl"));
                        item.setPublisher(jobj.optString("publisher"));
                        feeds.add(item);
                    }
                } catch (JSONException ex) {
                    Log.d(TAG, "exp in response parsing" + ex.getLocalizedMessage());
                }
                Log.d(TAG, feeds.size() + " -> " + feeds);
                fillAdapter(feeds);
                swipeRefreshLayout.setRefreshing(false);
                stopRefresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error + "");
                swipeRefreshLayout.setRefreshing(false);
                stopRefresh();
            }
        });
        getReqQueue().add(strReq);
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
        DbHelper dbh=new DbHelper(getActivity());
        List<NewsItem> newsItems=new ArrayList<>();
        for(Item i:items){
            newsItems.add((NewsItem)i);
        }
        dbh.fillNewsFeed(newsItems);
        super.fillAdapter(dbh.getNewsFeeds(true));
    }
}
