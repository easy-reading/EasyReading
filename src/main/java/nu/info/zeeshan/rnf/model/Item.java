package nu.info.zeeshan.rnf.model;

/**
 * Created by Zeeshan Khan on 10/29/2015.
 */
public class Item {
    private String title, image_url, desc;
    private String id;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", image_url='" + image_url + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
