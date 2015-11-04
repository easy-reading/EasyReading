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
                startFetchingFeed();

            }
        });

        Bundle bundle = getArguments();
        if (bundle.getInt(ARG_SECTION_NUMBER) == 1) {
            itemAdapter = new ItemAdapter(new ArrayList<Item>(),getContext());
        } else {
            itemAdapter = new ItemAdapter(new ArrayList<Item>(),getContext());
        }
        itemList.setAdapter(itemAdapter);
        return rootView;
    }


}
