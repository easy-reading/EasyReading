package nu.info.zeeshan.rnf.model;

/**
 *
 * Created by Zeeshan Khan on 10/29/2015.
 */
public class NewsItem extends Item{

    private String publisher;

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "publisher='" + publisher + '\'' + super.toString()+
                '}';
    }
}
