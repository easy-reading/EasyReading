package io.github.zkhan93.easyreading;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ScrollView;
import android.widget.Toast;

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

import io.github.zkhan93.easyreading.adapters.ItemAdapter;
import io.github.zkhan93.easyreading.model.Item;
import io.github.zkhan93.easyreading.util.Constants;

/**
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentNews extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView emptyView;
    public static String TAG = "FragmentNews";
    private RequestQueue reqQueue;
    private ItemAdapter itemAdapter;
    public static boolean updating;

    private void fillAdapter(List<Item> items) {
        if (itemAdapter != null)
            itemAdapter.addAll(items);
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

                startFetchingFeed();
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
