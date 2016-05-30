package nu.info.zeeshan.rnf.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * The initial result of API call to NY Times
 * Created by Zeeshan Khan on 5/14/2016.
 */
public class NYTResult {
    String status;
    String copyright;
    String section;
    @SerializedName("last_updated")
    Date lastUpdated;
    @SerializedName("num_results")
    int numResults;
    List<NewsItem> results;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public List<NewsItem> getResults() {
        return results;
    }

    public void setResults(List<NewsItem> results) {
        this.results = results;
    }
}
