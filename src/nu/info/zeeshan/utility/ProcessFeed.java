package nu.info.zeeshan.utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nu.info.zeeshan.dao.DbHelper;
import nu.info.zeeshan.rnf.FragmentFacebook;
import nu.info.zeeshan.rnf.FragmentNews;
import nu.info.zeeshan.rnf.MainActivity;
import nu.info.zeeshan.utility.ProcessFeed.FeedInput;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.InputSource;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;

public class ProcessFeed extends AsyncTask<FeedInput, Void, Boolean> {

	private Context context;
	private static String TAG = "nu.info.zeeshan.ProcessFeed";
	private static String PROTOCOL = "http:";
	private static String TAG_ATTR_SRC = "src";
	private static String TAG_IMG = "img";
	private static String DOUBLE_SLASH = "//";

	// static class LoadXML extends
	public ProcessFeed(Context c) {
		context = c;
	}

	public static class FeedInput {
		public String url;
		public int type;

		public FeedInput(String str, int t) {
			url = str;
			type = t;
		}
	}

	@Override
	protected Boolean doInBackground(FeedInput... inputfeed) {
		try {
			// URL url=arg0[0];
			ArrayList<Feed> feeds = new ArrayList<Feed>();
			Feed f;
			String str;
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed;// = input.build(new XmlReader(new URL(url[0])));
			List<SyndEntry> list;// = feed.getEntries();
			InputSource inputSource;
			DbHelper dbh = new DbHelper(context);
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
						// Utility.log(TAG,doc.text());
						pubdate = e.getPublishedDate();
						if (pubdate == null) {
							f.setTime(new Date().getTime()); // set current date
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
			return true;
		} catch (Exception e) {
			Utility.log("doInBackgroud", "" + e + e.getLocalizedMessage());
			return false;
		}

	}

	@Override
	protected void onPostExecute(Boolean res) {
		// setAdapter();
		if (res) {
			Utility.log("onPostExecute", "done downloading and processing");
			FragmentNews.updateAdapter(context);
			FragmentFacebook.updateAdapter(context);
			Toast.makeText(context,"updated sucessfully!", Toast.LENGTH_LONG)
					.show();
			;
		} else {
			Utility.log("onPostExecute", "error downloading or processing");
			Toast.makeText(context, "Error occured while updating!",
					Toast.LENGTH_LONG).show();
		}
		MainActivity.updating=false;
	}
}
