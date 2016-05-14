package nu.info.zeeshan.rnf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import nu.info.zeeshan.rnf.dao.DbConstants;
import nu.info.zeeshan.rnf.dao.DbHelper;
import nu.info.zeeshan.rnf.model.FacebookItem;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.util.Constants;
import nu.info.zeeshan.rnf.util.Util;

/**
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentFacebook extends FragmentMain {

    public static String TAG = "FragmentFacebook";
    ArrayList<String> permissions;
    CallbackManager callbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        callbackManager = CallbackManager.Factory.create();
        permissions = new ArrayList<>();
        permissions.addAll(Constants.FACEBOOK_PERMISSIONS);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Util.log(TAG, loginResult.toString());
                        Util.log(TAG, "login done");
                    }

                    @Override
                    public void onCancel() {
                        Util.log(TAG, "cancled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Util.log(TAG, "error->" + exception.getLocalizedMessage() + "");
                        Toast.makeText(getActivity().getApplicationContext(), "Error occured. Try again!!", Toast.LENGTH_SHORT).show();
                    }
                });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void startFetchingFeed() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.log(TAG, "onActivity result" + data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public FragmentFacebook() {
    }

    protected void fillAdapter(List<Item> items) {
        Util.fillDb(getActivity().getApplicationContext(),items);
        DbHelper dbh = new DbHelper(getActivity());
        if(items!=null && items.size()>0) {
            List<FacebookItem> fbItems = new ArrayList<FacebookItem>();
            for (Item i : items) {
                fbItems.add((FacebookItem) i);
            }
            dbh.fillFacebookFeed(fbItems);
        }
        itemAdapter.addAll(dbh.getFacebookFeeds(false));
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            new DbHelper(getContext()).markAllAs(DbConstants.State.READ, DbConstants.Type.FB);
        } catch (Exception ex) {
            Util.log(TAG, ex.getMessage() + "");
        }
        fillAdapter(null);
    }
}
