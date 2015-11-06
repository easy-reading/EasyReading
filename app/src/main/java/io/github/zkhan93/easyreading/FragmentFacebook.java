package io.github.zkhan93.easyreading;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

import io.github.zkhan93.easyreading.model.Item;

/**
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentFacebook extends FragmentMain{

    public static String TAG = "FragmentFacebook";

    public void startFetchingFeed() {
        if (AccessToken.getCurrentAccessToken() != null) {
            Bundle parameters = new Bundle();
            parameters
                    .putString("fields",
                            "name,story,description,link,message,created_time,object_id,likes,picture");
            GraphRequest request = new GraphRequest(
                    AccessToken.getCurrentAccessToken(), "/me/feed", parameters,
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
                    Item fb_feed;
                    for (int i = 0; i < len; i++) {
                        try {
                            json_feed = data.getJSONObject(i);
                            fb_feed = new Item();

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
                            if (json_feed.has("link"))
                                ;//fb_feed.setLink(json_feed.getString("link"));
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

                            fb_feeds.add(fb_feed);
                        } catch (JSONException ex) {
                            json_feed = null;
                            Log.d(TAG, ex.getLocalizedMessage());
                        }

                    }
                    fillAdapter(fb_feeds);
                    stopRefresh();
                }
            });
            request.executeAsync();
        } else {
            Toast.makeText(getContext(), "login to facebook", Toast.LENGTH_SHORT).show();
            stopRefresh();
        }
    }

    public FragmentFacebook() {
    }

}
