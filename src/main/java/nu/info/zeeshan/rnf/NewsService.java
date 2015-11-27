package nu.info.zeeshan.rnf;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import nu.info.zeeshan.rnf.model.FacebookItem;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.model.NewsItem;
import nu.info.zeeshan.rnf.util.Constants;
import nu.info.zeeshan.rnf.util.Util;

public class NewsService extends Service {

    private PowerManager.WakeLock wakelock;
    private static String TAG = "nu.info.zeeshan.rnf.NewsService";
    private static int EASY_READING_NOTIFICATION_ID = 0;
    private List<Item> summaryItems=new ArrayList<>();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void handleIntent(Intent intent) {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakelock.acquire();
        Util.log(TAG, "m on work :D");
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Util.log(TAG, "I am a service but you have no internet");
            stopSelf();
            return;
        }
        // handle intent
        fetchFacebookFeeds();
        fetchNewsFeeds();
    }

    public void fetchFacebookFeeds() {
        if (!FacebookSdk.isInitialized())
            FacebookSdk.sdkInitialize(getApplicationContext());
        if (isFacebookLoggedIn()) {
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
                        Util.fillDb(getApplicationContext(), fb_feeds);
                        summaryItems.addAll(fb_feeds);
                        setNotification();
                        Util.log(TAG, "fb feeds done in service");
                    }
                });
                request.executeAsync();
            }
        }
    }
    private RequestQueue requestQueue;
    private RequestQueue getRequestQueue(){
        if(requestQueue==null)
            requestQueue= Volley.newRequestQueue(this);
        return requestQueue;
    }
    public void fetchNewsFeeds() {
        Response.ErrorListener errorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.log(TAG, error + "");
            }
        };
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<Item> feeds = new ArrayList<>();
                try {
                    JSONObject jobj = new JSONObject(response);
                    jobj = jobj.optJSONObject("responseData");
                    JSONArray jarr = jobj.getJSONArray("results");
                    DateFormat dateFormat=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                    String dateString;
                    for (int i = 0; i < jarr.length(); i++) {
                        jobj = jarr.getJSONObject(i);
                        NewsItem item = new NewsItem();
                        item.setTitle(Html.fromHtml(jobj.optString("title")).toString());
                        item.setDesc(Html.fromHtml(jobj.optString("content")).toString());
                        if (jobj.has("image"))
                            item.setImage_url(jobj.optJSONObject("image").optString("tbUrl"));

                        dateString=jobj.optString("publishedDate");
                        if(dateString!=null){
                            try {
                                Date date = dateFormat.parse(dateString);
                                item.setTime(date.getTime());
                            }catch(ParseException ex){
                                Util.log(TAG, "invalid date format");
                            }
                        }

                        item.setLink(jobj.optString("unescapedUrl"));
                        item.setPublisher(jobj.optString("publisher"));
                        feeds.add(item);
                    }
                } catch (JSONException ex) {
                    Util.log(TAG, "exp in response parsing" + ex.getLocalizedMessage());
                }
                Util.log(TAG, feeds.size() + " -> " + feeds);
                Util.log(TAG, "news feeds done in service");
                Util.fillDb(getApplicationContext(), feeds);
                summaryItems.addAll(feeds);
                setNotification();
            }
        };
        SharedPreferences spf=getApplicationContext().getSharedPreferences(getString(R.string.pref_filename), Context.MODE_PRIVATE);
        Set<String> interests=spf.getStringSet(getString(R.string.pref_news_keywords), Constants.DEFAULT_NEWS_KEYWORDS);
        for (String s:interests){
            StringRequest strReq = new StringRequest(Constants.News.URL + "&q=india,"+s, responseListener, errorListener);
            getRequestQueue().add(strReq);
        }
    }

    private void setNotification() {
        Util.log(TAG, "notification in service");
        NotificationCompat.Builder builder = null;
        int size = summaryItems.size();
        if (size > 0) {
            Util.log(TAG, "have some data");
            int fbItem=0;
            int newsItem=0;
            for(Item i:summaryItems){
                if(i instanceof FacebookItem)
                    fbItem++;
                else if(i instanceof NewsItem)
                    newsItem++;
            }
            Context context = getApplicationContext();
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setSound(
                            RingtoneManager
                                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentText("New Stories");

            if (size > 1) {
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(getString(R.string.app_name));
                for (Item feed : summaryItems) {
                    if (feed.getTitle()!=null)
                        inboxStyle.addLine(feed.getTitle().trim());
                }
                inboxStyle.setSummaryText("New Stories");
                builder.setStyle(inboxStyle);
            }
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pintent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pintent);

            NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            notifyMgr.notify(EASY_READING_NOTIFICATION_ID, builder.build());
        }else{
            Util.log(TAG, "nothing in sample service");
        }
        stopSelf();
    }

    private boolean isFacebookLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handleIntent(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        wakelock.release();
    }
}
