package nu.info.zeeshan.rnf;

import nu.info.zeeshan.adapters.NewsAdapter;
import nu.info.zeeshan.dao.DbConstants;
import nu.info.zeeshan.dao.DbHelper;
import nu.info.zeeshan.dao.DbStructure;
import nu.info.zeeshan.utility.Utility;
import nu.info.zeeshan.utility.Utility.Filter;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentNews extends Fragment {
	SharedPreferences spf;
	ViewHolder holder;
	static NewsAdapter adapter;
	static SQLiteDatabase db;
	static String TAG = "nu.info.zeeshan.rnf.FragmentNews";
	static int filter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		if (filter == 0)
			filter = Filter.UNREAD;
		if (spf == null)
			spf = ((MainActivity) getActivity()).getSharedPreferences(
					getString(R.string.pref_filename), Context.MODE_PRIVATE);
		if (db == null)
			db = new DbHelper(getActivity()).getReadableDatabase();
		updateAdapter(getActivity());
		if (holder == null) {
			holder = new ViewHolder();
			holder.list = (ListView) rootView.findViewById(R.id.listViewFeed);
			holder.list.setEmptyView(rootView
					.findViewById(R.id.linearViewError));
			holder.list.setAdapter(adapter);
			holder.errorMsg = (TextView) rootView
					.findViewById(R.id.textViewError);
			holder.errorView = (LinearLayout) rootView
					.findViewById(R.id.linearViewError);
		}
		rootView.setTag(holder);
		setHasOptionsMenu(true);
		return rootView;
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Utility.log(TAG, "creating fragment menu");
		inflater.inflate(R.menu.news_menu, menu);
	}

	public void onPrepareOptionsMenu(Menu menu) {
		switch (filter) {
		case 1:
			menu.findItem(R.id.action_newsfilter)
					.setIcon(
							getResources().getDrawable(
									R.drawable.ic_action_read_white));
			break;
		case 2:
			menu.findItem(R.id.action_newsfilter).setIcon(
					getResources()
							.getDrawable(R.drawable.ic_action_read_active));
			break;
		case 3:
			menu.findItem(R.id.action_newsfilter).setIcon(
					getResources().getDrawable(R.drawable.ic_action_all_white));
			break;
		}
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String msg;
		switch (item.getItemId()) {
		case R.id.action_newsfilter:
			switch (filter) {
			case Filter.UNREAD:
				item.setIcon(getResources().getDrawable(
						R.drawable.ic_action_read_active));
				msg=getString(R.string.toast_msg_read);
				filter = Filter.READ;
				break;
			case Filter.READ:
				item.setIcon(getResources().getDrawable(
						R.drawable.ic_action_all_white));
				msg=getString(R.string.toast_msg_all);
				filter = Filter.ALL;
				break;
			case Filter.ALL:
				item.setIcon(getResources().getDrawable(
						R.drawable.ic_action_read_white));
				msg=getString(R.string.toast_msg_unread);
				filter = Filter.UNREAD;
				break;
			default:
				msg=getString(R.string.toast_msg_error);
			}
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			updateAdapter(getActivity());
			return true;
		default:
			return false;
		}
	}

	public static void updateAdapter(Context context) {
		String[] select = { DbStructure.FeedTable._ID,
				DbStructure.FeedTable.COLUMN_TITLE,
				DbStructure.FeedTable.COLUMN_TEXT,
				DbStructure.FeedTable.COLUMN_TIME,
				DbStructure.FeedTable.COLUMN_TYPE,
				DbStructure.FeedTable.COLUMN_STATE,
				DbStructure.FeedTable.COLUMN_IMAGE,
				DbStructure.FeedTable.COLUMN_LINK, };
		String where = DbStructure.FeedTable.COLUMN_TYPE
				+ DbConstants.EQUALS
				+ DbConstants.Type.NEWS
				+ ((filter == Filter.READ || filter == Filter.UNREAD) ? (DbConstants.AND
						+ DbStructure.FeedTable.COLUMN_STATE
						+ DbConstants.EQUALS + (filter - 1))
						: "");
		Cursor c = db.query(DbStructure.FeedTable.TABLE_NAME, select, where,
				null, null, null, DbStructure.FeedTable.COLUMN_TIME
						+ DbConstants.DESC);
		if (adapter == null)
			adapter = new NewsAdapter(context, c);
		else
			adapter.changeCursor(c);
		adapter.notifyDataSetChanged();
		Utility.log(TAG,
				"dataset updated news " + where + " count is " + c.getCount());
	}

	static class ViewHolder {
		ListView list;
		LinearLayout errorView;
		TextView errorMsg;
	}
}
