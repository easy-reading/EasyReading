package nu.info.zeeshan.rnf.model;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import nu.info.zeeshan.rnf.R;


/**
 * Created by Zeeshan Khan on 10/29/2015.
 */
public class ExpandedItemViewHolder extends ItemViewHolder {
    private TextView itemDesc;
    private ImageButton shareBtn, likeBtn, viewFullBtn;
    public static String TAG = ExpandedItemViewHolder.class.getSimpleName();
    private ActionClickListener actionClickListener;

    public ExpandedItemViewHolder(final View parent, final ItemClickListener clickListener,
                                  ActionClickListener actionClickListener) {
        super(parent, clickListener);
        this.actionClickListener = actionClickListener;
        itemDesc = (TextView) parent.findViewById(R.id.item_desc);
        shareBtn = (ImageButton) parent.findViewById(R.id.btnShare);
        likeBtn = (ImageButton) parent.findViewById(R.id.btnLike);
        viewFullBtn = (ImageButton) parent.findViewById(R.id.btnViewFull);

    }

    public static ExpandedItemViewHolder newInstance(View parent, ItemClickListener
            clickListener, ActionClickListener actionClickListener) {
        return new ExpandedItemViewHolder(parent, clickListener, actionClickListener);
    }

    public void setItem(Item item) {
        if (item instanceof FacebookItem)
            setItem((FacebookItem) item);
        else if (item instanceof NewsItem)
            setItem((NewsItem) item);
    }

    public void setItem(final NewsItem item) {
        super.setItem(item);
        itemDesc.setText(item.getAbstractt());
        viewFullBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionClickListener.onFullStoryClick(item.getUrl());
            }
        });
    }

    public void setItem(final FacebookItem item) {
        super.setItem(item);
        itemDesc.setText(item.getDesc());
        viewFullBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionClickListener.onFullStoryClick(item.getLink());
            }
        });
    }
}
