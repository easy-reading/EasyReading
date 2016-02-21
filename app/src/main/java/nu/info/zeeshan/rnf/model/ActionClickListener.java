package nu.info.zeeshan.rnf.model;

/**
 * Created by Zeeshan Khan on 2/21/2016.
 */
public interface ActionClickListener {
    public void onLikeClick(long itemId);

    public void onFullStoryClick(String url);

    public void onShareClick(Item item);
}
