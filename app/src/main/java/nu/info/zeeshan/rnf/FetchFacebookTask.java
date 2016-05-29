package nu.info.zeeshan.rnf;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import nu.info.zeeshan.rnf.model.FacebookItem;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.util.Constants;
import nu.info.zeeshan.rnf.util.FetchTaskUICallbacks;
import nu.info.zeeshan.rnf.util.Util;

/**
 * Created by Zeeshan Khan on 5/28/2016.
 */
public class FetchFacebookTask extends AsyncTask<Void, Void, Boolean> {
    public static final String TAG = FetchFacebookTask.class.getSimpleName();
    private Context context;
    private FetchTaskUICallbacks fetchTaskUICallbacks;

    public FetchFacebookTask(Context context, FetchTaskUICallbacks fetchTaskUICallbacks) {
        this.context = context;
        this.fetchTaskUICallbacks = fetchTaskUICallbacks;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result = false;
        if (AccessToken.getCurrentAccessToken() != null) {
            Bundle parameters = new Bundle();
            parameters
                    .putString("fields",
                            Constants.FacebookFeed.PARAMS);
            GraphRequest request = new GraphRequest(
                    AccessToken.getCurrentAccessToken(), Constants.FacebookFeed.NODE, parameters,
                    HttpMethod.GET, new GraphRequest.Callback() {

                @Override
                public void onCompleted(GraphResponse response) {
                    JSONArray data;
                    try {
                        data = response.getJSONObject()
                                .getJSONArray("data");
                    } catch (JSONException ex) {
                        data = new JSONArray();
                    }
                    // fill the data in db
                    int len = data.length();
                    JSONObject json_feed;
                    ArrayList<Item> fb_feeds = new ArrayList<Item>();
                    FacebookItem fb_feed;
                    for (int i = 0; i < len; i++) {
                        try {
                            json_feed = data.getJSONObject(i);
                            fb_feed = new FacebookItem();
                            fb_feed.setId(json_feed.getString("id"));

                            if (json_feed.has("story"))
                                fb_feed.setTitle(json_feed
                                        .getString("story"));

                            else if (json_feed.has("name"))
                                fb_feed.setTitle(json_feed
                                        .getString("name"));

                            if (json_feed.has("description"))
                                fb_feed.setDesc(json_feed
                                        .getString("description"));

                            if (json_feed.has("message"))
                                fb_feed.setDesc(json_feed
                                        .getString("message"));

                            if (json_feed.has("picture"))
                                fb_feed.setImage_url(json_feed
                                        .getString("picture"));
                            fb_feed.setLink(json_feed.optString("link"));
                            try {
                                if (json_feed.has("created_time")) {
                                    SimpleDateFormat format = new SimpleDateFormat(
                                            "yyyy-MM-dd'T'HH:mm:ssZ");

                                    fb_feed.setTime(format.parse(json_feed
                                            .getString("created_time")).getTime());
                                }
                            } catch (ParseException e) {
                                fb_feed.setTime(new Date().getTime());
                                e.printStackTrace();
                            }
                            JSONObject tmp = json_feed.optJSONObject("likes");
                            if (tmp != null) {
                                JSONArray likes = tmp.optJSONArray("data");
                                fb_feed.setLikes(likes.length());
                            }

                            fb_feeds.add(fb_feed);
                        } catch (JSONException ex) {
                            json_feed = null;
                            Util.log(TAG, ex.getLocalizedMessage());
                        }
                    }
                    Util.fillDb(context, fb_feeds);
                }
            });
            request.executeAndWait();
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        fetchTaskUICallbacks.taskComplete(result);
    }
}
