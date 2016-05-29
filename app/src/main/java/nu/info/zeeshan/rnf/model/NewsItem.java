package nu.info.zeeshan.rnf.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * NewItem
 * Created by Zeeshan Khan on 5/14/2016.
 */
public class NewsItem extends Item {
    String section;
    String subsection;
    String title;
    @SerializedName("abstract")
    String abstractt;
    String url;
    @SerializedName("published_date")
    Date publishedDate;
    @Expose
    List<MultimediaItem> multimedia;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubsection() {
        return subsection;
    }

    public void setSubsection(String subsection) {
        this.subsection = subsection;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractt() {
        return abstractt;
    }

    public void setAbstractt(String abstractt) {
        this.abstractt = abstractt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void setPublishedDate(long publishedDate) {
        this.publishedDate = new Date(publishedDate);
    }

    public List<MultimediaItem> getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(List<MultimediaItem> multimedia) {
        this.multimedia = multimedia;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "section='" + section + '\'' +
                ", subsection='" + subsection + '\'' +
                ", title='" + title + '\'' +
                ", abstractt='" + abstractt + '\'' +
                ", url='" + url + '\'' +
                ", publishedDate=" + publishedDate +
                ", multimedia=" + multimedia +
                '}';
    }

    public MultimediaItem getMultiMediaItem(String type) {
        if (multimedia == null || multimedia.isEmpty()) {
            return null;
        }
        MultimediaItem media = null;
        for (MultimediaItem mi : multimedia) {
            if (mi.getFormat().equals(type)) {
                media = mi;
                break;
            }
        }
        return media;
    }

    public MultimediaItem getMultiMediaItem() {
        return getMultiMediaItem(MultimediaItem.TYPE.LARGE);
    }

}
