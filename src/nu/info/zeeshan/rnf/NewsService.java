package nu.info.zeeshan.rnf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nu.info.zeeshan.rnf.dao.DbHelper;
import nu.info.zeeshan.rnf.utility.FacebookFeed;
import nu.info.zeeshan.rnf.utility.Feed;
import nu.info.zeeshan.rnf.utility.Utility;
import nu.info.zeeshan.rnf.utility.Utility.FeedInput;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.InputSource;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;

public class NewsService extends Service {
	private WakeLock wakelock;
	private static String TAG = "nu.info.zeeshan.rnf.NewsService";
	private static String PROTOCOL = "http:";
	private static String TAG_ATTR_SRC = "src";
	private static String TAG_IMG = "img";
	private static String DOUBLE_SLASH = "//";
	private static String NEW_LINE = "\n";
	private static int NEWS_NOTIFICATION_ID = 0;
	private static int FB_NOTIFICATION_ID = 1;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void handleIntent(Intent intent) {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wakelock.acquire();
		Utility.log(TAG, "m on work :D");
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null || !ni.isConnected()) {
			Utility.log(TAG, "I am a service but you have no internet");
			stopSelf();
			return;
		}

		// handle intent
		if (isFacebookLoggedIn()) {
			Bundle parameters = new Bundle();
			parameters
					.putString("fields",
							"name,story,description,link,message,created_time,object_id,likes,picture");
			GraphRequest request = new GraphRequest(
					AccessToken.getCurrentAccessToken(), "/me/feed",
					parameters, HttpMethod.GET, new GraphRequest.Callback() {

						@Override
						public void onCompleted(GraphResponse response) {
							JSONArray data;
							try {
								data = response.getJSONObject().getJSONArray(
										"data");
							} catch (JSONException ex) {
								data = new JSONArray();
							}
							// fill the data in db
							int len = data.length();
							JSONObject json_feed;
							ArrayList<Feed> fb_feeds = new ArrayList<Feed>();
							FacebookFeed fb_feed;
							for (int i = 0; i < len; i++) {
								try {
									json_feed = data.getJSONObject(i);
									fb_feed = new FacebookFeed();

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
										fb_feed.setImage(json_feed
												.getString("picture"));
									if (json_feed.has("link"))
										fb_feed.setLink(json_feed
												.getString("link"));
									try {
										if (json_feed.has("created_time")) {
											SimpleDateFormat format = new SimpleDateFormat(
													"yyyy-MM-dd'T'HH:mm:ssZ");
											Date datetime = format.parse(json_feed
													.getString("created_time"));
											fb_feed.setTime(datetime.getTime());
										}
									} catch (ParseException e) {
										fb_feed.setTime(new Date().getTime());
										e.printStackTrace();
									}

									fb_feeds.add(fb_feed);
								} catch (JSONException ex) {
									json_feed = null;
									Utility.log(TAG, ex.getLocalizedMessage());
								}
							}
							new DbHelper(getApplicationContext())
									.fillFeed(fb_feeds);
							setFbNotification(fb_feeds);
						}
					});
			request.executeAsync();

		}

		SharedPreferences spf = getSharedPreferences(
				getString(R.string.pref_filename), Context.MODE_PRIVATE);

		String newsfeed = spf.getString(getString(R.string.pref_newsrss), null);
		if (newsfeed == null && newsfeed == null) {
			stopSelf(); // no feeds to process
		} else {
			new FetchNews().execute(new FeedInput(newsfeed));
		}

	}

	private void setFbNotification(ArrayList<Feed> result) {
		NotificationCompat.Builder builder = null;
		int size = result.size();
		if (size > 0) {
			Context context = getApplicationContext();
			builder = new NotificationCompat.Builder(context)
					.setSmallIcon(R.drawable.ic_notification)
					.setContentTitle(getString(R.string.app_name))
					.setAutoCancel(true)
					.setSound(
							RingtoneManager
									.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
					.setContentText("New facebook feeds");

			if (size > 1) {
				NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
				inboxStyle.setBigContentTitle(getString(R.string.app_name));
				for (Feed feed : result) {
					if(feed.getTitle().trim().length()>0)
						inboxStyle.addLine(feed.getTitle());
				}
				inboxStyle.setSummaryText("New facebook updates");
				builder.setStyle(inboxStyle);
			}
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pintent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pintent);

			NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// Builds the notification and issues it.
			notifyMgr.notify(FB_NOTIFICATION_ID, builder.build());
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

	private class FetchNews extends
			AsyncTask<FeedInput, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(FeedInput... inputfeed) {
			ArrayList<String> title_noti = new ArrayList<String>();
			try {
				// URL url=arg0[0];

				ArrayList<Feed> feeds = new ArrayList<Feed>();
				Feed f;
				String str;
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed;// = input.build(new XmlReader(new URL(url[0])));
				List<SyndEntry> list;// = feed.getEntries();
				InputSource inputSource;
				DbHelper dbh = new DbHelper(getApplicationContext());
				Date pubdate;
				Document doc;
				for (FeedInput fe : inputfeed) {
					feeds.clear();

					inputSource = new InputSource(fe.url);
					inputSource.setEncoding("UTF-8");
					feed = input.build(inputSource);
					list = feed.getEntries();
					for (SyndEntry e : list) {
						try {
							f = new Feed();
							f.setTitle(e.getTitle());
							title_noti.add(e.getTitle());
							doc = Jsoup.parse(e.getDescription().getValue());
							f.setDesc(doc.text());
							pubdate = e.getPublishedDate();
							if (pubdate == null) {
								f.setTime(new Date().getTime()); // set current
																	// date
								// need to fetch whatever in the pubdate tag
							} else {
								f.setTime(pubdate.getTime());
							}
							f.setLink(e.getLink());

							str = doc.getElementsByTag(TAG_IMG).get(0)
									.attr(TAG_ATTR_SRC);
							f.setImage(str.startsWith(DOUBLE_SLASH) ? (PROTOCOL + str)
									: str);

							feeds.add(f);
						} catch (Exception ee) {
							Utility.log(TAG, "skipped a entry " + ee);
						}
					}
					dbh.fillFeed(feeds);
				}

			} catch (Exception e) {
				Utility.log("doInBackgroud", "" + e + e.getLocalizedMessage());

			}
			return title_noti;

		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			NotificationCompat.Builder builder = null;
			int size = result.size();
			if (size > 0) {
				Context context = getApplicationContext();
				builder = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_notification)
						.setContentTitle(getString(R.string.app_name))
						.setAutoCancel(true)
						.setSound(
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
						.setContentText("New news feeds");

				if (size > 1) {
					NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
					inboxStyle.setBigContentTitle(getString(R.string.app_name));
					for (String str : result) {
						inboxStyle.addLine(str);
					}
					inboxStyle.setSummaryText("latest news updates");
					builder.setStyle(inboxStyle);
				}
				Intent intent = new Intent(context, MainActivity.class);
				PendingIntent pintent = PendingIntent.getActivity(context, 0,
						intent, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(pintent);

				NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				// Builds the notification and issues it.
				notifyMgr.notify(NEWS_NOTIFICATION_ID, builder.build());
			}
			stopSelf();
		}
	}
}
