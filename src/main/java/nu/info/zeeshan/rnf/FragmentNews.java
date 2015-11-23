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

import java.util.ArrayList;
import java.util.List;

import nu.info.zeeshan.rnf.dao.DbHelper;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.util.Constants;

/**
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentNews extends FragmentMain {
    public static String TAG = "FragmentNews";
    private RequestQueue reqQueue;


    public void startFetchingFeed() {

        StringRequest strReq = new StringRequest(Constants.URL.NEWS + "india", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<Item> feeds = new ArrayList<>();
                try {
                    JSONObject jobj = new JSONObject(response);
                    jobj = jobj.optJSONObject("responseData");
                    JSONArray jarr = jobj.getJSONArray("results");

                    for (int i = 0; i < jarr.length(); i++) {
                        jobj = jarr.getJSONObject(i);
                        Item item = new Item();
                        item.setTitle(Html.fromHtml(jobj.optString("title")).toString());
                        item.setDesc(Html.fromHtml(jobj.optString("content")).toString());
                        if (jobj.has("image"))
                            item.setImage_url(jobj.optJSONObject("image").optString("tbUrl"));
                        Log.d(TAG,"whole: "+jobj.toString());
                        Log.d(TAG, "date: " + jobj.optString("publishedDate"));
                        item.setLink(jobj.optString("link"));
                        //item.setTime(Date.valueOf(jobj.optString("publishedDate")));
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
        super.fillAdapter(items);
        new DbHelper(getActivity()).fillNewsFeed(items);
    }
}
