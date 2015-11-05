package io.github.zkhan93.easyreading;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

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

/**
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentFacebook extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout emptySwipeRefreshLayout;
    public static String TAG = "FragmentFacebook";
    private ScrollView emptyView;
    private ItemAdapter itemAdapter;
    public static boolean updating;

    private void fillAdapter(List<Item> items) {
        if (itemAdapter != null)
            itemAdapter.addAll(items);
    }

    public void startFetchingFb() {
        if (AccessToken.getCurrentAccessToken() != null) {
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
                    stopRefresh();
                }
            });
            request.executeAsync();
        } else {
            Toast.makeText(getContext(), "login to facebook", Toast.LENGTH_SHORT).show();
            stopRefresh();
        }
    }


    public FragmentFacebook() {
    }

    public void emptyListCheck(){
        if(itemAdapter==null || itemAdapter.getItemCount()==0){
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView itemList = (RecyclerView) rootView.findViewById(R.id.item_list);
        itemList.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        emptyView = (ScrollView) rootView.findViewById(R.id.empty_view);
        itemAdapter = new ItemAdapter(new ArrayList<Item>(), getContext());
        itemList.setAdapter(itemAdapter);
        itemAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                emptyListCheck();
            }
        });
        emptyListCheck();
        return rootView;
    }
    /**
     * called on refresh action performed by SwipeRefreshLayout
     */
    @Override
    public void onRefresh() {

        String msg=null;
        if (!updating) {
            updating = true;
            ConnectivityManager cm = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.isConnected()) {

                startFetchingFb();
            } else {
                msg = getString(R.string.toast_no_internet);
                stopRefresh();
            }
        } else {
            msg = getString(R.string.toast_msg_wait);
            stopRefresh();
        }
        if (msg != null)
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

    }

    public void stopRefresh() {

        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        else
            Log.d(TAG, "refreshlayout is null");

        updating = false;
    }

}
