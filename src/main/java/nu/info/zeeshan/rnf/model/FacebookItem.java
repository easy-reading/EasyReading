package nu.info.zeeshan.rnf.model;

/**
 * Created by Zeeshan Khan on 10/29/2015.
 */
public class FacebookItem extends Item{

    private int likes;

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "FacebookItem{" +
                "likes='" + likes + '\'' + super.toString()+
                '}';
    }
}
