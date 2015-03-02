package nu.info.zeeshan.rnf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nu.info.zeeshan.rnf.dao.DbHelper;
import nu.info.zeeshan.rnf.utility.Feed;
import nu.info.zeeshan.rnf.utility.ProcessFeed.FeedInput;
import nu.info.zeeshan.rnf.utility.Utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.InputSource;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

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

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void handleIntent(Intent intent) {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wakelock.acquire();

		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null || !ni.isConnected()) {
			Utility.log(TAG, "I am a service but you have no internet");
			stopSelf();
			return;
		}

		// handle intent
		SharedPreferences spf = getSharedPreferences(
				getString(R.string.pref_filename), Context.MODE_PRIVATE);
		new FetchNews().execute(
				new FeedInput(spf.getString(getString(R.string.pref_newsrss),
						""), 1),
				new FeedInput(spf.getString(
						getString(R.string.pref_facebookrss), ""), 0));
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

	private class FetchNews extends AsyncTask<FeedInput, Void, Void> {

		@Override
		protected Void doInBackground(FeedInput... inputfeed) {
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
							if (fe.type == 1) {
								str = doc.getElementsByTag(TAG_IMG).get(0)
										.attr(TAG_ATTR_SRC);
								f.setImage(str.startsWith(DOUBLE_SLASH) ? (PROTOCOL + str)
										: str);
							}
							feeds.add(f);
						} catch (Exception ee) {
							Utility.log(TAG, "skipped a entry " + ee);
						}
					}
					dbh.fillFeed(feeds, fe.type);
				}

			} catch (Exception e) {
				Utility.log("doInBackgroud", "" + e + e.getLocalizedMessage());

			}
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			Utility.log(TAG, "done fetching data by service :D");
			stopSelf();
		}
	}
}