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


/**
 * Created by Zeeshan Khan on 10/29/2015.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {
    private TextView itemTitle, itemDesc, itemTimestamp, itemExtra;
    private ImageView itemImage;
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd, MMM");

    public ItemViewHolder(final View parent, TextView itemTitle, TextView itemDesc, ImageView itemImage, TextView itemTimestamp, TextView itemExtra) {
        super(parent);
        this.itemDesc = itemDesc;
        this.itemImage = itemImage;
        this.itemTitle = itemTitle;
        this.itemTimestamp = itemTimestamp;
        this.itemExtra = itemExtra;
    }

    public static ItemViewHolder newInstance(View parent) {
        TextView itemTitle, itemDesc, itemTimestamp, itemExtra;
        ImageView itemImage;
        itemDesc = (TextView) parent.findViewById(R.id.item_desc);
        itemTitle = (TextView) parent.findViewById(R.id.item_title);
        itemImage = (ImageView) parent.findViewById(R.id.item_image);
        itemTimestamp = (TextView) parent.findViewById(R.id.item_timestamp);
        itemExtra = (TextView) parent.findViewById(R.id.item_extra);
        return new ItemViewHolder(parent, itemTitle, itemDesc, itemImage, itemTimestamp, itemExtra);
    }

    public void setItem(Item item) {
        if (item.getTitle() != null)
            itemTitle.setText(item.getTitle());
        else
            itemTitle.setText("--N.A--");
        if (item.getDesc() != null)
            itemDesc.setText(item.getDesc());
        else
            itemDesc.setText("--N.A--");
        if (item.getTime() > 0) {
            itemTimestamp.setText(DATE_FORMAT.format(new Date(item.getTime())));
        } else {
            itemTimestamp.setText("Unspecified time");
        }
        if (item.getImage_url() != null)
            ImageLoader.getInstance().displayImage(item.getImage_url(), itemImage, new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.com_facebook_profile_picture_blank_square)
                    .showImageOnFail(R.drawable.com_facebook_profile_picture_blank_square)
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .showImageOnLoading(R.drawable.com_facebook_profile_picture_blank_square)
                    .cacheInMemory(true)
                    .build());
        //for extra data
        if (item instanceof FacebookItem) {
            //facebook likes
            FacebookItem fbItem = (FacebookItem) item;
            if (fbItem.getLikes() > 0)
                itemExtra.setText(fbItem.getLikes() + " likes");
            else
                itemExtra.setText("No likes");
        } else if (item instanceof NewsItem) {
            NewsItem newsItem = (NewsItem) item;
            if (newsItem.getPublisher() != null)
                itemExtra.setText(((NewsItem) item).getPublisher());
            else
                itemExtra.setText("Unspecified publisher");
        }
    }

    public ImageView getItemImage() {
        return itemImage;
    }
}
