package nu.info.zeeshan.rnf.model;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import nu.info.zeeshan.rnf.R;
import nu.info.zeeshan.rnf.util.Util;


/**
 *
 * Created by Zeeshan Khan on 10/29/2015.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {
    private TextView itemTitle, itemDesc, itemInfo;
    private ImageView itemImage;
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd, MMM");
    public static String TAG = "ItemViewHolder";

    public ItemViewHolder(final View parent, final ItemClickListener clickListener) {
        super(parent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(getAdapterPosition());
                Util.log(TAG, "clicked item");
            }
        });
        itemDesc = (TextView) parent.findViewById(R.id.item_desc);
        itemTitle = (TextView) parent.findViewById(R.id.item_title);
        itemImage = (ImageView) parent.findViewById(R.id.item_image);
        itemInfo = (TextView) parent.findViewById(R.id.item_info);

    }

    public static ItemViewHolder newInstance(View parent, ItemClickListener clickListener) {
        return new ItemViewHolder(parent, clickListener);
    }

    public void setItem(Item item) {
        if (item.getTitle() != null)
            itemTitle.setText(item.getTitle());
        else
            itemTitle.setText("-N.A-");
        if (item.getDesc() != null)
            itemDesc.setText(item.getDesc());
        else
            itemDesc.setText("-N.A-");
        if (item.getTime() > 0) {
            itemInfo.setText(DATE_FORMAT.format(new Date(item.getTime())));
        } else {
            itemInfo.setText("Unspecified time");
        }
        if (item.getImage_url() != null)
            ImageLoader.getInstance().displayImage(item.getImage_url(), itemImage, new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(item instanceof FacebookItem ? R.drawable.default_facebook_background : R.drawable.default_news_background)
                    .showImageOnFail(item instanceof FacebookItem ? R.drawable.default_facebook_background : R.drawable.default_news_background)
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .showImageOnLoading(item instanceof FacebookItem ? R.drawable.default_facebook_background : R.drawable.default_news_background)
                    .cacheInMemory(true)
                    .build());
        //for extra data
        if (item instanceof FacebookItem) {
            //facebook likes
            FacebookItem fbItem = (FacebookItem) item;
            if (fbItem.getLikes() > 0)
                addInfoText(fbItem.getLikes() + " likes");
            else
                addInfoText("No likes");
        } else if (item instanceof NewsItem) {
            NewsItem newsItem = (NewsItem) item;
            if (newsItem.getPublisher() != null)
                addInfoText(((NewsItem) item).getPublisher());
            else
                addInfoText("Unspecified publisher");
        }
        if (item.isExpanded()) {
            itemDesc.setVisibility(View.VISIBLE);
        } else {
            itemDesc.setVisibility(View.GONE);
        }
    }

    private void addInfoText(String text) {
        if (text != null && text.trim().length() > 0) {
            if (itemInfo.getText().toString().trim().length() > 0) {
                itemInfo.setText(itemInfo.getText() + " | " + text);
            } else {
                itemInfo.setText(text);
            }
        }
    }

    private void clearInfoText() {
        if (itemInfo != null) {
            itemInfo.setText("");
        }
    }

    public ImageView getItemImage() {
        return itemImage;
    }
}
