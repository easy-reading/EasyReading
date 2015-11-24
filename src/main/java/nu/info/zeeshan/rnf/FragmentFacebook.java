package nu.info.zeeshan.rnf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nu.info.zeeshan.rnf.dao.DbHelper;
import nu.info.zeeshan.rnf.model.FacebookItem;
import nu.info.zeeshan.rnf.model.Item;

/**
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentFacebook extends FragmentMain {

    public static String TAG = "FragmentFacebook";
    ArrayList<String> permissions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        callbackManager = CallbackManager.Factory.create();
        permissions = new ArrayList<String>();
        permissions.add("user_posts");
        permissions.add("user_actions.news");

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, loginResult.toString());
                        Log.d(TAG, "login done");
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "cancled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "error->" + exception.getLocalizedMessage() + "");
                    }
                });
        return  super.onCreateView(inflater, container, savedInstanceState);
    }

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
                            JSONObject tmp=json_feed.optJSONObject("likes");
                            if(tmp!=null) {
                                JSONArray likes=tmp.optJSONArray("data");
                                fb_feed.setLikes(likes.length());
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

            Snackbar.make(swipeRefreshLayout, "Login to facebook", Snackbar.LENGTH_LONG)
                    .setAction("Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoginManager.getInstance().logInWithReadPermissions(FragmentFacebook.this, permissions);
                        }
                    }).show();
            stopRefresh();
        }
    }


    CallbackManager callbackManager;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivity result" + data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public FragmentFacebook() {
    }

    @Override
    protected void fillAdapter(List<Item> items) {
        DbHelper dbh=new DbHelper(getActivity());
        List<FacebookItem> fbItems=new ArrayList<FacebookItem>();
        for(Item i:items){
            fbItems.add((FacebookItem)i);
        }
        dbh.fillFacebookFeed(fbItems);
        super.fillAdapter(dbh.getFacebookFeeds(false));
    }
}
