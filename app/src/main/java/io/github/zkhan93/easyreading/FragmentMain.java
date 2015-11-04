package io.github.zkhan93.easyreading;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.zkhan93.easyreading.adapters.ItemAdapter;
import io.github.zkhan93.easyreading.model.Item;
import io.github.zkhan93.easyreading.util.Constants;

/**
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentMain extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private SwipeRefreshLayout swipeRefreshLayout;
    public static String TAG = "FragmentMain";
    private RequestQueue reqQueue;
    private ItemAdapter itemAdapter;
    private int myType;

    private static interface FRAGMENT_TYPE {
        int NEWS = 1;
        int FB = 2;
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentMain newInstance(int sectionNumber) {
        FragmentMain fragment = new FragmentMain();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void fillAdapter(List<Item> items) {
        if (itemAdapter != null)
            itemAdapter.addAll(items);
    }

    public void startFetchingFb() {
        Bundle parameters = new Bundle();
        parameters
                .putString("fields",
                        "name,story,description,link,message,created_time,object_id,likes,picture");
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(), "/me/feed", parameters,
                HttpMethod.GET, new GraphRequest.Callback() {

            @Override
            public void onCompleted(GraphResponse response) {
                JSONArray data;

                try {
                    data = response.getJSONObject()
                            .getJSONArray("data");
                } catch (JSONException ex) {
                    data = new JSONArray();
                }
                // fill the data in db
                int len = data.length();
                JSONObject json_feed;
                ArrayList<Item> fb_feeds = new ArrayList<Item>();
                Item fb_feed;
                for (int i = 0; i < len; i++) {
                    try {
                        json_feed = data.getJSONObject(i);
                        fb_feed = new Item();

                        fb_feed.setId(json_feed.getString("id"));
                        if (json_feed.has("story"))
                            fb_feed.setTitle(json_feed
                                    .getString("story"));
                        else if (json_feed.has("name"))
                            fb_feed.setTitle(json_feed
                                    .getString("name"));

                        if (json_feed.has("description"))
                            fb_feed.setDesc(json_feed
                                    .getString("description"));

                        if (json_feed.has("message"))
                            fb_feed.setDesc(json_feed
                                    .getString("message"));

                        if (json_feed.has("picture"))
                            fb_feed.setImage_url(json_feed
                                    .getString("picture"));
                        if (json_feed.has("link"))
                            ;//fb_feed.setLink(json_feed.getString("link"));
                        try {
                            if (json_feed.has("created_time")) {
                                SimpleDateFormat format = new SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ssZ");

                                fb_feed.setTime(format.parse(json_feed
                                        .getString("created_time")).getTime());
                            }
                        } catch (ParseException e) {
                            fb_feed.setTime(new Date().getTime());
                            e.printStackTrace();
                        }

                        fb_feeds.add(fb_feed);
                    } catch (JSONException ex) {
                        json_feed = null;
                        Log.d(TAG, ex.getLocalizedMessage());
                    }
                }
                fillAdapter(fb_feeds);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        request.executeAsync();
    }

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
                        feeds.add(item);
                    }

                } catch (JSONException ex) {
                    Log.d(TAG, "exp in response parsing" + ex.getLocalizedMessage());
                }
                Log.d(TAG, feeds.size() + " -> " + feeds);
                fillAdapter(feeds);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error + "");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        getReqQueue().add(strReq);
    }

    private RequestQueue getReqQueue() {
        if (reqQueue == null)
            reqQueue = Volley.newRequestQueue(getContext());
        return reqQueue;
    }

    public FragmentMain() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView itemList = (RecyclerView) rootView.findViewById(R.id.item_list);
        itemList.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "refresh", Toast.LENGTH_SHORT).show();
                if (myType == FRAGMENT_TYPE.NEWS)
                    startFetchingFeed();
                else
                    startFetchingFb();

            }
        });

        Bundle bundle = getArguments();
        myType = bundle.getInt(ARG_SECTION_NUMBER);
        itemAdapter = new ItemAdapter(new ArrayList<Item>(), getContext());
        itemList.setAdapter(itemAdapter);
        return rootView;
    }
}
