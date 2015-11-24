package nu.info.zeeshan.rnf.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nu.info.zeeshan.rnf.R;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.model.ItemViewHolder;

/**
 * Created by Zeeshan Khan on 10/29/2015.
 */
public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Item> itemList;
    private Context context;

    public ItemAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return ItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        holder.getItemImage().setImageDrawable(context.getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square));
        holder.setItem(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public void addAll(List<Item> items) {
        if (itemList == null)
            itemList = new ArrayList<>();
        itemList.clear();
        itemList.addAll(items);
        notifyDataSetChanged();
    }
}
