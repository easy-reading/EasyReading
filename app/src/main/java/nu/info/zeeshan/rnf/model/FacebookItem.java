package nu.info.zeeshan.rnf.model;

/**
 * Created by Zeeshan Khan on 10/29/2015.
 */
public class FacebookItem extends Item {
    private String title, image_url, desc;
    private String id;
    private long time;
    private String link;
    private int likes;

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

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
        return "FacebookItem{" +
                "title='" + title + '\'' +
                ", image_url='" + image_url + '\'' +
                ", desc='" + desc + '\'' +
                ", id='" + id + '\'' +
                ", time=" + time +
                ", link='" + link + '\'' +
                ", likes=" + likes +
                '}';
    }
}
