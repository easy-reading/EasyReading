package nu.info.zeeshan.rnf;

import nu.info.zeeshan.adapters.FbAdapter;
import nu.info.zeeshan.dao.DbConstants;
import nu.info.zeeshan.dao.DbHelper;
import nu.info.zeeshan.dao.DbStructure;
import nu.info.zeeshan.utility.Utility;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentFacebook extends Fragment {
	SharedPreferences spf;

	static ViewHolder holder;
	public static FbAdapter adapter;
	static SQLiteDatabase db;
	static String TAG="nu.info.zeeshan.rnf.FragmentFacebook";
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		spf = ((MainActivity) getActivity()).getSharedPreferences(
				getString(R.string.pref_filename), Context.MODE_PRIVATE);
		db = new DbHelper(getActivity()).getReadableDatabase();
		updateAdapter(getActivity());
		holder = new ViewHolder();
		holder.list = (ListView) rootView.findViewById(R.id.listViewFeed);
		holder.list.setEmptyView(rootView.findViewById(R.id.linearViewError));
		holder.list.setAdapter(adapter);
		holder.errorMsg = (TextView) rootView.findViewById(R.id.textViewError);
		holder.errorView = (LinearLayout) rootView
				.findViewById(R.id.linearViewError);
		rootView.setTag(holder);
		return rootView;
	}

	public static void updateAdapter(Context context) {
		String[] select = { DbStructure.FeedTable._ID,
				DbStructure.FeedTable.COLUMN_TITLE,
				DbStructure.FeedTable.COLUMN_TEXT,
				DbStructure.FeedTable.COLUMN_TIME,
				DbStructure.FeedTable.COLUMN_LINK, };
		Cursor c = db.query(DbStructure.FeedTable.TABLE_NAME, select, "type=2",
				null, null, null, DbStructure.FeedTable.COLUMN_TIME+DbConstants.DESC);
		if (adapter == null)
			adapter = new FbAdapter(context, c);
		else
			adapter.changeCursor(c);
		adapter.notifyDataSetChanged();
		Utility.log(TAG,"dataset updated facebook");
	}

	public void showError(String msg) {
		holder.errorMsg.setText(msg);
		holder.errorView.setVisibility(View.VISIBLE);
	}

	public void hideError() {
		holder.errorView.setVisibility(View.GONE);
	}

	static class ViewHolder {
		ListView list;
		LinearLayout errorView;
		TextView errorMsg;
	}
}
