package nu.info.zeeshan.rnf.model;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import nu.info.zeeshan.rnf.R;


/**
 * Created by Zeeshan Khan on 10/29/2015.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {
    private TextView itemTitle, itemDesc;
    private ImageView itemImage;

    public ItemViewHolder(final View parent, TextView itemTitle, TextView itemDesc, ImageView itemImage) {
        super(parent);
        this.itemDesc = itemDesc;
        this.itemImage = itemImage;
        this.itemTitle = itemTitle;
    }

    public static ItemViewHolder newInstance(View parent) {
        TextView itemTitle, itemDesc;
        ImageView itemImage;
        itemDesc = (TextView) parent.findViewById(R.id.item_desc);
        itemTitle = (TextView) parent.findViewById(R.id.item_title);
        itemImage = (ImageView) parent.findViewById(R.id.item_image);
        return new ItemViewHolder(parent, itemTitle, itemDesc, itemImage);
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
        if (item.getImage_url() != null)
            ImageLoader.getInstance().displayImage(item.getImage_url(), itemImage, new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.com_facebook_profile_picture_blank_square)
                    .showImageOnFail(R.drawable.com_facebook_profile_picture_blank_square)
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .showImageOnLoading(R.drawable.com_facebook_profile_picture_blank_square)
                    .cacheInMemory(true)
                    .build());
    }

    public ImageView getItemImage() {
        return itemImage;
    }
}