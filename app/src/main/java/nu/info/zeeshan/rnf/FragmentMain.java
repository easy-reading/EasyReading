package nu.info.zeeshan.rnf;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import nu.info.zeeshan.rnf.adapters.ItemAdapter;
import nu.info.zeeshan.rnf.model.ActionClickListener;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.model.MySwipeRefreshLayout;
import nu.info.zeeshan.rnf.util.Util;

/**
 * Created by Zeeshan Khan on 10/28/2015.
 */
public abstract class FragmentMain extends Fragment implements SwipeRefreshLayout
        .OnRefreshListener {
    public static String TAG = "FragmentMain";
    protected MySwipeRefreshLayout swipeRefreshLayout;
    private ScrollView emptyView;
    private ItemAdapter itemAdapter;
    private ActionClickListener actionClickListener = new ActionClickListener() {
        @Override
        public void onLikeClick(long itemId) {

        }

        @Override
        public void onFullStoryClick(String url) {
            Intent intent = new Intent(getActivity().getApplicationContext(), FullStoryActivity
                    .class);
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onShareClick(Item item) {

        }
    };

    public FragmentMain() {
    }

    protected void fillAdapter(List<Item> items) {
        if (itemAdapter != null) {
            itemAdapter.addAll(items);
        }
    }


//    public void emptyListCheck() {
//        if (itemAdapter == null || itemAdapter.getItemCount() == 0) {
//            emptyView.setVisibility(View.VISIBLE);
//        } else {
//            emptyView.setVisibility(View.GONE);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView itemList = (RecyclerView) rootView.findViewById(R.id.item_list);
        itemList.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = (MySwipeRefreshLayout) rootView.findViewById(R.id
                .swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);
//        emptyView = (ScrollView) rootView.findViewById(R.id.empty_view);

        itemAdapter = new ItemAdapter(new ArrayList<Item>(), getContext(),actionClickListener);
        itemList.setAdapter(itemAdapter);

//        itemAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                emptyListCheck();
//            }
//        });

//        emptyListCheck();

        return rootView;
    }

    /**
     * called on refresh action performed by SwipeRefreshLayout
     */
    @Override
        public void onRefresh() {
        String msg = null;
        ConnectivityManager cm = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {

            startFetchingFeed();
        } else {
            msg = getString(R.string.toast_no_internet);
            stopRefresh();
        }

        if (msg != null)
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

    }

    public void stopRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        } else
            Util.log(TAG, "refreshlayout is null");
    }

    protected void showMsg(String msg) {
        Snackbar.make(swipeRefreshLayout, msg, Snackbar.LENGTH_SHORT).show();
    }

    public abstract void startFetchingFeed();
}
