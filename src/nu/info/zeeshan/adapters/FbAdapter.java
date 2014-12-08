package nu.info.zeeshan.adapters;

import nu.info.zeeshan.dao.DbStructure;
import nu.info.zeeshan.rnf.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FbAdapter extends CursorAdapter {

	Cursor c;
	Context context;
	ViewHolder holder;

	public FbAdapter(Context contxt, Cursor cc) {
		super(contxt, cc, 1);
		c = cc;
		context = contxt;
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		holder = (ViewHolder) view.getTag();
		holder.title.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TITLE)));
		holder.time.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TIME)));
		holder.desc.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TEXT)));
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		holder = new ViewHolder();
		View view = ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.feed_item_fb, parent, false);
		holder.title = (TextView) view.findViewById(R.id.textViewTitleFb);
		holder.desc = (TextView) view.findViewById(R.id.textViewDescFb);
		holder.time = (TextView) view.findViewById(R.id.textViewTimeFb);
		

		holder.title.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TITLE)));
		holder.time.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TIME)));
		holder.desc.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TEXT)));
		view.setTag(holder);
		return view;
	}

	static class ViewHolder {
		TextView title;
		TextView desc;
		TextView time;
	}
}
