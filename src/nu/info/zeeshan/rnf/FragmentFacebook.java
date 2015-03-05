package nu.info.zeeshan.rnf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nu.info.zeeshan.rnf.adapters.FbAdapter;
import nu.info.zeeshan.rnf.dao.DbConstants;
import nu.info.zeeshan.rnf.dao.DbHelper;
import nu.info.zeeshan.rnf.dao.DbStructure;
import nu.info.zeeshan.rnf.utility.Constants;
import nu.info.zeeshan.rnf.utility.Feed;
import nu.info.zeeshan.rnf.utility.Utility;
import nu.info.zeeshan.rnf.utility.Utility.FeedInput;
import nu.info.zeeshan.rnf.utility.Utility.Filter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.InputSource;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;

public class FragmentFacebook extends Fragment implements OnRefreshListener {
	static String TAG = "nu.info.zeeshan.rnf.FragmentFacebook";
	int filter;
	public static boolean updating;
	SharedPreferences spf;
	ViewHolder holder;
	FbAdapter adapter;
	SQLiteDatabase db;
	SwipeRefreshLayout refreshlayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utility.log(TAG, " in onCreate facebook");
		db = new DbHelper(getActivity()).getWritableDatabase();
		setspf(); // set shared preference
		super.onCreate(savedInstanceState);
	}

	private void setspf() {
		spf = ((MainActivity) getActivity()).getSharedPreferences(
				getString(R.string.pref_filename), Context.MODE_PRIVATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// null checks creating empty fragments
		Utility.log(TAG, " in onCreateView facebook");
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		if (filter == 0)
			filter = Filter.UNREAD;
		setspf();
		db = new DbHelper(getActivity()).getWritableDatabase();
		updateAdapter();

		holder = new ViewHolder();
		holder.list = (ListView) rootView.findViewById(R.id.listViewFeed);
		holder.list.setEmptyView(rootView.findViewById(R.id.linearViewError));
		holder.list.setAdapter(adapter);

		// holder.errorMsg = (TextView)
		// rootView.findViewById(R.id.textViewError);
		holder.errorView = (SwipeRefreshLayout) rootView
				.findViewById(R.id.linearViewError);
		holder.errorView.setOnRefreshListener(this);
		rootView.setTag(holder);
		setHasOptionsMenu(true);
		refreshlayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.refreshLayout);
		refreshlayout.setOnRefreshListener(this);
		updating = false;
		stopRefresh();
		return rootView;
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fb_menu, menu);
	}

	public void onPrepareOptionsMenu(Menu menu) {
		switch (filter) {
		case Filter.UNREAD:
			menu.findItem(R.id.action_fbfilter)
					.setIcon(
							getResources().getDrawable(
									R.drawable.ic_action_read_white));
			break;
		case Filter.READ:
			menu.findItem(R.id.action_fbfilter).setIcon(
					getResources()
							.getDrawable(R.drawable.ic_action_read_active));
			break;
		case Filter.ALL:
			menu.findItem(R.id.action_fbfilter).setIcon(
					getResources().getDrawable(R.drawable.ic_action_all_white));
			break;
		}
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String msg;
		switch (item.getItemId()) {
		case R.id.action_fbfilter:
			switch (filter) {
			case Filter.UNREAD:
				item.setIcon(getResources().getDrawable(
						R.drawable.ic_action_read_active));
				msg = getString(R.string.toast_msg_read);
				filter = Filter.READ;
				break;
			case Filter.READ:
				item.setIcon(getResources().getDrawable(
						R.drawable.ic_action_all_white));
				msg = getString(R.string.toast_msg_all);
				filter = Filter.ALL;
				break;
			case Filter.ALL:
				item.setIcon(getResources().getDrawable(
						R.drawable.ic_action_read_white));
				msg = getString(R.string.toast_msg_unread);
				filter = Filter.UNREAD;
				break;
			default:
				msg = getString(R.string.toast_msg_error);
			}
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			updateAdapter();
			return true;
		default:
			return false;
		}
	}

	public void updateAdapter() {
		Context context = getActivity();
		if (context != null) {
			if (db == null)
				db = new DbHelper(context).getWritableDatabase();
			Cursor c = db.rawQuery("select count(*) from feeds where "
					+ DbStructure.FeedTable.COLUMN_TYPE + DbConstants.EQUALS
					+ DbConstants.Type.FB + DbConstants.AND
					+ DbStructure.FeedTable.COLUMN_STATE + DbConstants.EQUALS
					+ DbConstants.State.READ, null);
			if (c.moveToFirst()) {

				int limit = Integer.parseInt(spf.getString(
						getString(R.string.pref_limit),
						Constants.DEFAULT_FEED_LIMIT));
				if (c.getInt(0) > limit) {
					// delete extra feeds
					db.execSQL("delete from feeds where type="
							+ DbConstants.Type.FB
							+ " and state="
							+ DbConstants.State.READ
							+ " and _id NOT IN (select _id from feeds where type="
							+ DbConstants.Type.FB + " and state="
							+ DbConstants.State.READ
							+ " order by time desc limit " + limit + ")");
				}
			}
			c.close();
			String[] select = { DbStructure.FeedTable._ID,
					DbStructure.FeedTable.COLUMN_TITLE,
					DbStructure.FeedTable.COLUMN_TEXT,
					DbStructure.FeedTable.COLUMN_TYPE,
					DbStructure.FeedTable.COLUMN_TIME,
					DbStructure.FeedTable.COLUMN_STATE,
					DbStructure.FeedTable.COLUMN_LINK, };
			String where = DbStructure.FeedTable.COLUMN_TYPE
					+ DbConstants.EQUALS
					+ DbConstants.Type.FB
					+ (((filter == Filter.READ || filter == Filter.UNREAD) ? DbConstants.AND
							+ DbStructure.FeedTable.COLUMN_STATE
							+ DbConstants.EQUALS + (filter - 1)
							: ""));
			c = db.query(DbStructure.FeedTable.TABLE_NAME, select, where, null,
					null, null, DbStructure.FeedTable.COLUMN_TIME
							+ DbConstants.DESC);
			if (adapter == null)
				adapter = new FbAdapter(context, c);
			else
				adapter.changeCursor(c);

			adapter.notifyDataSetChanged();
			Utility.log(TAG, "dataset updated facebook" + where + " count is "
					+ c.getCount());
		} else {
			Utility.log(TAG, "cannot get context");
		}
	}

	@Override
	public void onDetach() {
		if (db != null)
			db.close();
		super.onDetach();
	}

	static class ViewHolder {
		ListView list;
		SwipeRefreshLayout errorView;
	}

	/**
	 * called on refresh action performed by SwipeRefreshLayout
	 */
	@Override
	public void onRefresh() {
		String msg;
		if (!updating) {
			updating = true;
			String fbfeed = spf.getString(getString(R.string.pref_facebookrss),
					null);
			ConnectivityManager cm = (ConnectivityManager) getActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni != null && ni.isConnected()) {
				if (fbfeed == null) {
					msg = null;
					stopRefresh();
				} else {
					msg = getString(R.string.toast_msg_fbfeedok);
					fetch(new FeedInput(fbfeed));
				}
			} else {
				msg = getString(R.string.no_internet);
				stopRefresh();
			}
		} else {
			msg = getString(R.string.toast_msg_wait);
			stopRefresh();
		}
		if (msg != null)
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

	}

	public void stopRefresh() {
		if (refreshlayout != null)
			refreshlayout.setRefreshing(false);
		else
			Utility.log(TAG, "refreshlayout is null");
		if (holder != null && holder.errorView != null)
			holder.errorView.setRefreshing(false);
		else
			Utility.log(TAG, "errorlayout is null");
		updating = false;

	}

	public class ProcessFeed extends AsyncTask<FeedInput, Void, Boolean> {

		private static final String TAG = "nu.info.zeeshan.ProcessFeed";
		private static final String PROTOCOL = "http:";
		private static final String TAG_ATTR_SRC = "src";
		private static final String TAG_IMG = "img";
		private static final String DOUBLE_SLASH = "//";

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
				DbHelper dbh = new DbHelper(getActivity());
				Date pubdate;
				Document doc;

				FeedInput fe = inputfeed[0];

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
				updateAdapter();

			} else {
				Utility.log("onPostExecute", "error downloading or processing");
			}
			stopRefresh();
		}
	}

	public void fetch(FeedInput feed) {
		new ProcessFeed().execute(feed);
	}
}
