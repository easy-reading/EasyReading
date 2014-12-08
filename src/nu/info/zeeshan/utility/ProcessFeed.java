package nu.info.zeeshan.utility;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nu.info.zeeshan.dao.DbHelper;
import nu.info.zeeshan.rnf.FragmentFacebook;
import nu.info.zeeshan.rnf.FragmentNews;
import nu.info.zeeshan.utility.ProcessFeed.FeedInput;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;

public class ProcessFeed extends AsyncTask<FeedInput, Void, Boolean> {

	private Context context;
	private int type;
	private static String TAG_CHANNEL = "channel";
	private static String TAG_TITLE = "title";
	private static String TAG_LINK = "link";
	private static String TAG_DESRIPTION = "description";
	private static String TAG_ITEM = "item";
	private static String TAG_PUB_DATE = "pubDate";
	private static String TAG = "nu.info.zeeshan.ProcessFeed";
	private SimpleDateFormat dformat = new SimpleDateFormat("E, d MMM y");

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
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed;// = input.build(new XmlReader(new URL(url[0])));
			List<SyndEntry> list;// = feed.getEntries();
			DbHelper dbh = new DbHelper(context);
			for (FeedInput fe : inputfeed) {
				feeds.clear();
				feed = input.build(new XmlReader(new URL(fe.url)));
				list = feed.getEntries();
				for (SyndEntry e : list) {
					try {
						f = new Feed();
						f.setTitle(e.getTitle());
						f.setTime(dformat.format(e.getPublishedDate()));
						Document doc = Jsoup.parse(e.getDescription()
								.getValue());
						f.setDesc(doc.text());
						f.setLink(e.getLink());
						if (fe.type == 1)
							f.setImage(doc.getElementsByTag("img").get(0)
									.attr("src"));
						feeds.add(f);
					} catch (Exception ee) {
						Utility.log(TAG, "skipped a entry" + e);
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
			Utility.log("onPostExecute", "done downloading");
			FragmentNews.updateAdapter(context);
			FragmentFacebook.updateAdapter(context);
		} else {
			Toast.makeText(context, "Error occured while updating!",
					Toast.LENGTH_LONG);
		}
	}

	/**
	 * Method to get xml content from url HTTP Get request
	 * */
	public String getXmlFromUrl(String url) {
		String xml = null;

		try {
			// request method is GET
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return XML
		return xml;
	}

	public final Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = (Document) db.parse(is);

		} catch (ParserConfigurationException e) {
			Log.e(TAG, e + e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e(TAG, e + e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e(TAG, e + e.getMessage());
			return null;
		}

		return doc;
	}

	public String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return getElementValue(n.item(0));
	}

	/**
	 * Getting node value
	 * 
	 * @param elem
	 *            element
	 */
	public final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE
							|| (child.getNodeType() == Node.CDATA_SECTION_NODE)) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}
}
