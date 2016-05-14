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
 * Created by Zeeshan Khan on 10/29/2015.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {
    private TextView itemTitle, itemInfo;
    private ImageView itemImage;
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd, MMM");
    public static String TAG = ItemViewHolder.class.getSimpleName();

    public ItemViewHolder(final View parent, final ItemClickListener clickListener) {
        super(parent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(getAdapterPosition());
                Util.log(TAG, "clicked item");
            }
        });

        itemTitle = (TextView) parent.findViewById(R.id.item_title);
        itemImage = (ImageView) parent.findViewById(R.id.item_image);
        itemInfo = (TextView) parent.findViewById(R.id.item_info);
    }

    public static ItemViewHolder newInstance(View parent, ItemClickListener clickListener) {
        return new ItemViewHolder(parent, clickListener);
    }

    public void setItem(Item item) {
        if (item instanceof FacebookItem)
            setItem((FacebookItem) item);
        else if (item instanceof NewsItem)
            setItem((NewsItem) item);
    }

    public void setItem(FacebookItem item) {
        if (item.getTitle() != null)
            itemTitle.setText(item.getTitle());
        else
            itemTitle.setText("-N.A-");
        if (item.getTime() > 0) {
            itemInfo.setText(DATE_FORMAT.format(new Date(item.getTime())));
        } else {
            itemInfo.setText("Unspecified time");
        }
        if (item.getImage_url() != null)
            ImageLoader.getInstance().displayImage(item.getImage_url(), itemImage, new
                    DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable
                            .default_facebook_background)
                    .showImageOnFail(R.drawable
                            .default_facebook_background)
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .showImageOnLoading(R.drawable
                            .default_facebook_background)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build());
        //for extra data

        //facebook likes

        if (item.getLikes() > 0)
            addInfoText(item.getLikes() + " likes");
        else
            addInfoText("No likes");

    }

    public void setItem(NewsItem item) {
        if (item.getTitle() != null)
            itemTitle.setText(item.getTitle());
        else
            itemTitle.setText("-N.A-");
        if (item.getPublishedDate().getTime() > 0) {
            itemInfo.setText(DATE_FORMAT.format(item.getPublishedDate()));
        } else {
            itemInfo.setText("Unspecified time");
        }
        MultimediaItem multiItem = item.getMultiMediaItem(MultimediaItem.TYPE.NORMAL);
        if (multiItem!=null) {
            ImageLoader.getInstance().displayImage(multiItem.getUrl(), itemImage, new
                    DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.default_news_background)
                    .showImageOnFail(R.drawable.default_news_background)
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .showImageOnLoading(R.drawable.default_news_background)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build());
        }
        //for extra data


        if (item.getSubsection() != null)
            addInfoText(item.getSubsection());
        else
            addInfoText("Unspecified publisher");

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
