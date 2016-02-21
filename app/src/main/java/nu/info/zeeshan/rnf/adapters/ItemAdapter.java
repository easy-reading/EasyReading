package nu.info.zeeshan.rnf.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nu.info.zeeshan.rnf.R;
import nu.info.zeeshan.rnf.model.ActionClickListener;
import nu.info.zeeshan.rnf.model.AddItemViewHolder;
import nu.info.zeeshan.rnf.model.ExpandedItemViewHolder;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.model.ItemClickListener;
import nu.info.zeeshan.rnf.model.ItemViewHolder;
import nu.info.zeeshan.rnf.model.NewsItem;
import nu.info.zeeshan.rnf.util.Constants;

/**
 * Created by Zeeshan Khan on 10/29/2015.
 */
public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Item> itemList;
    private Context context;
    private ItemClickListener clickListener;
    private ActionClickListener actionClickListener;
    private int lastExpandedItem;
    public static int ADD_AFTER = 7;

    public ItemAdapter(final List<Item> itemList, Context context,ActionClickListener actionClickListener) {
        this.actionClickListener=actionClickListener;
        this.itemList = itemList;
        this.context = context;
        this.clickListener = new ItemClickListener() {
            public void onClick(int position) {
                if (position == lastExpandedItem) {
                    itemList.get(getActualItemPosition(position)).setExpanded(!itemList.get(getActualItemPosition(position)).isExpanded());
                    notifyItemChanged(lastExpandedItem);
                } else {
                    itemList.get(getActualItemPosition(lastExpandedItem)).setExpanded(false);
                    notifyItemChanged(lastExpandedItem);

                    itemList.get(getActualItemPosition(position)).setExpanded(true);
                    notifyItemChanged(position);

                    lastExpandedItem = position;
                }
            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constants.ItemViewType.NORMAL) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return ItemViewHolder.newInstance(view, clickListener);
        } else if (viewType == Constants.ItemViewType.EXPANDED) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expanded, parent, false);
            return ExpandedItemViewHolder.newInstance(view, clickListener,actionClickListener);
        } else {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_item, parent, false);
            return new AddItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == Constants.ItemViewType.NORMAL || getItemViewType(position) == Constants.ItemViewType.EXPANDED) {
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
            Item item = itemList.get(getActualItemPosition(position));
            if (item instanceof NewsItem)
                holder.getItemImage().setImageDrawable(context.getResources().getDrawable(R.drawable.default_news_background));
            else
                holder.getItemImage().setImageDrawable(context.getResources().getDrawable(R.drawable.default_facebook_background));
            holder.setItem(item);
        } else {
            AddItemViewHolder holder = (AddItemViewHolder) viewHolder;
            holder.initAd();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if ((position + 1) % ADD_AFTER == 0)
            return Constants.ItemViewType.AD;
        else {
            if (itemList.get(getActualItemPosition(position)).isExpanded())
                return Constants.ItemViewType.EXPANDED;
            else
                return Constants.ItemViewType.NORMAL;
        }
    }

    private int getActualItemPosition(int position) {
        return position - position / ADD_AFTER;
    }

    public static String TAG = "ItemAdapter";

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size() + (int) Math.ceil(itemList.size() / (ADD_AFTER - 1));
    }

    public void addAll(List<Item> items) {
        if (itemList == null)
            itemList = new ArrayList<>();
        itemList.clear();
        itemList.addAll(items);
        notifyDataSetChanged();
    }
}