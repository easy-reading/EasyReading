package nu.info.zeeshan.rnf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.DataFormatException;

import nu.info.zeeshan.rnf.adapters.FbAdapter;
import nu.info.zeeshan.rnf.dao.DbConstants;
import nu.info.zeeshan.rnf.dao.DbHelper;
import nu.info.zeeshan.rnf.dao.DbStructure;
import nu.info.zeeshan.rnf.utility.Constants;
import nu.info.zeeshan.rnf.utility.FacebookFeed;
import nu.info.zeeshan.rnf.utility.Feed;
import nu.info.zeeshan.rnf.utility.Utility;
import nu.info.zeeshan.rnf.utility.Utility.FeedInput;
import nu.info.zeeshan.rnf.utility.Utility.Filter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class FragmentFacebook extends Fragment implements OnRefreshListener {
	static String TAG = "nu.info.zeeshan.rnf.FragmentFacebook";
	int filter;
	public static boolean updating;
	SharedPreferences spf;
	ViewHolder holder;
	FbAdapter adapter;
	SQLiteDatabase db;
	SwipeRefreshLayout refreshlayout;
	Cursor c;
	private CallbackManager callbackManager;
	private LoginButton loginButton;
	private View login_layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utility.log(TAG, " in onCreate facebook");
		db = new DbHelper(getActivity()).getWritableDatabase();
		setspf(); // set shared preference

		super.onCreate(savedInstanceState);
	}

	private boolean isFacebookLoggedIn() {
		return AccessToken.getCurrentAccessToken() != null;
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
		View rootView = inflater
				.inflate(R.layout.fragment_fb, container, false);
		if (filter == 0)
			filter = Filter.UNREAD;
		setspf();
		db = new DbHelper(getActivity()).getWritableDatabase();
		updateAdapter();

		holder = new ViewHolder();
		holder.list = (ListView) rootView.findViewById(R.id.listViewFeed);
		holder.list.setEmptyView(rootView.findViewById(R.id.linearViewError));
		holder.list.setAdapter(adapter);
		login_layout = (View) rootView.findViewById(R.id.FacebookLoginLayout);
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
		loginButton = (LoginButton) rootView
				.findViewById(R.id.facebook_login_button);

		if (!isFacebookLoggedIn()) {
			loginButton.setReadPermissions("user_posts,news");
			loginButton.setFragment(this);
			callbackManager = CallbackManager.Factory.create();
			loginButton.registerCallback(callbackManager,
					new FacebookCallback<LoginResult>() {
						@Override
						public void onSuccess(LoginResult loginResult) {
							Utility.log(TAG, loginResult.toString());
							setLoginView(true);
							Utility.log(TAG, "login done");
						}

						@Override
						public void onCancel() {
							// App code
						}

						@Override
						public void onError(FacebookException exception) {
							// App code
						}
					});
			setLoginView(false);
		} else {
			setLoginView(true);
		}
		return rootView;
	}

	public void setLoginView(boolean logged_in) {
		if (logged_in) {
			login_layout.setVisibility(View.VISIBLE);
			loginButton.setVisibility(View.GONE);
		} else {
			login_layout.setVisibility(View.GONE);
			loginButton.setVisibility(View.VISIBLE);
		}
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fb_menu, menu);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		callbackManager.onActivityResult(requestCode, resultCode, data);
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
			c = db.rawQuery("select count(*) from facebookfeeds where "

			+ DbStructure.FeedTable.COLUMN_STATE + DbConstants.EQUALS
					+ DbConstants.State.READ, null);
			if (c.moveToFirst()) {

				int limit = Integer.parseInt(spf.getString(
						getString(R.string.pref_limit),
						Constants.DEFAULT_FEED_LIMIT));
				if (c.getInt(0) > limit) {
					// delete extra feeds
					db.execSQL("delete from facebookfeeds where type="
							+ DbConstants.Type.FB
							+ " and state="
							+ DbConstants.State.READ
							+ " and _id NOT IN (select _id from facebookfeeds where type="
							+ DbConstants.Type.FB + " and state="
							+ DbConstants.State.READ
							+ " order by time desc limit " + limit + ")");
				}
			}
			c.close();
			String[] select = { DbStructure.FeedTable._ID,
					DbStructure.FeedTable.COLUMN_TITLE,
					DbStructure.FeedTable.COLUMN_TEXT,
					DbStructure.FeedTable.COLUMN_IMAGE,
					DbStructure.FeedTable.COLUMN_TIME,
					DbStructure.FeedTable.COLUMN_STATE,
					DbStructure.FeedTable.COLUMN_LINK, };
			String where = (((filter == Filter.READ || filter == Filter.UNREAD) ? DbStructure.FeedTable.COLUMN_STATE
					+ DbConstants.EQUALS + (filter - 1)
					: ""));
			c = db.query(DbStructure.FacebookFeedTable.TABLE_NAME, select,
					where, null, null, null, DbStructure.FeedTable.COLUMN_TIME
							+ DbConstants.DESC);
			if (adapter == null)
				adapter = new FbAdapter(context, c);
			else
				adapter.swapCursor(c).close();

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

			ConnectivityManager cm = (ConnectivityManager) getActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni != null && ni.isConnected()) {
				if (isFacebookLoggedIn()) {
					msg = getString(R.string.toast_msg_fbfeedok);
					fetch();
				} else {
					msg = null;
					stopRefresh();
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

	public class ProcessNewFeed extends AsyncTask<FeedInput, Void, Boolean> {

		@Override
		protected Boolean doInBackground(FeedInput... arg0) {

			return null;
		}

	}

	public void fetch() {
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
									fb_feed.setLink(json_feed.getString("link"));
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
						new DbHelper(getActivity()).fillFeed(fb_feeds);
						stopRefresh();
						updateAdapter();

					}
				});
		request.executeAsync();
	}

	@Override
	public void onDestroy() {
		if (c != null)
			c.close();
		super.onDestroy();
	}
}
