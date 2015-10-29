package io.github.zkhan93.easyreading;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.easyreading.adapters.ItemAdapter;
import io.github.zkhan93.easyreading.model.Item;

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

    public FragmentMain() {
    }

    ListAdapter adapter;

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
                ;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        ItemAdapter itemAdapter;
        Bundle bundle = getArguments();
        if (bundle.getInt(ARG_SECTION_NUMBER) == 1) {
            itemAdapter = new ItemAdapter(new ArrayList<Item>());
        } else {
            itemAdapter = new ItemAdapter(getItemList());
        }
        itemList.setAdapter(itemAdapter);
        return rootView;
    }

    private List<Item> getItemList() {
        List<Item> itemList = new ArrayList<Item>();
        for (int i = 0; i < 10; i++) {
            Item item = new Item();
            item.setImage_url("");
            item.setTitle("Title" + i);
            item.setDesc("Desc" + i);
            itemList.add(item);
        }
        return itemList;
    }
}
