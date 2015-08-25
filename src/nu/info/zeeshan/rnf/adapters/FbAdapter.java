package nu.info.zeeshan.rnf.adapters;

import java.util.Date;

import com.nostra13.universalimageloader.core.ImageLoader;

import nu.info.zeeshan.rnf.R;
import nu.info.zeeshan.rnf.dao.DbStructure;
import nu.info.zeeshan.rnf.utility.Utility;
import nu.info.zeeshan.rnf.utility.Utility.FacebookViewHolder;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FbAdapter extends CursorAdapter {
	private static final String TAG = "nu.info.zeeshan.dao.adapters.FbAdapter";
	Cursor c;
	Context context;
	FacebookViewHolder holder;
	Date date = new Date();

	public FbAdapter(Context contxt, Cursor cc) {
		super(contxt, cc, 1);
		c = cc;
		context = contxt;
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		holder = (FacebookViewHolder) view.getTag();
		holder.title.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TITLE)));
		date.setTime(c.getLong(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TIME)));
		holder.time.setText(Utility.dformat.format(date));
		holder.desc.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TEXT)));
		holder.setId(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable._ID)));
		holder.state = c.getInt(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_STATE));
		holder.image.setImageDrawable(context.getResources().getDrawable(
				R.drawable.default_news));

		if (holder.state == 1) {
			holder.check.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_action_read_active));
			// Utility.log(TAG, holder.id + " is checked");
		} else {
			holder.check.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_action_read_white));
			// Utility.log(TAG, holder.id + " is unchecked");
		}
		String imgsrc = c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_IMAGE));
		if (imgsrc != null && !imgsrc.trim().isEmpty()){
			ImageLoader.getInstance().displayImage(imgsrc, holder.image);
		holder.image.setVisibility(View.VISIBLE);
		}
		else
			holder.image.setVisibility(View.GONE);
		view.setTag(holder);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		holder = new FacebookViewHolder();
		View view = ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.feed_item_fb, parent, false);
		holder.title = (TextView) view.findViewById(R.id.textViewTitleFb);
		holder.desc = (TextView) view.findViewById(R.id.textViewDescFb);
		holder.time = (TextView) view.findViewById(R.id.textViewTimeFb);
		holder.check = (ImageButton) view.findViewById(R.id.imageButtonReadFb);
		holder.image = (ImageView) view.findViewById(R.id.imageViewFb);

		holder.title.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TITLE)));
		date.setTime(c.getLong(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TIME)));
		holder.time.setText(Utility.dformat.format(date));
		holder.desc.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TEXT)));
		holder.setId(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable._ID)));
		holder.state = c.getInt(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_STATE));

		String imgsrc = c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_IMAGE));
		if (imgsrc != null && !imgsrc.trim().isEmpty()) {
			ImageLoader.getInstance().displayImage(imgsrc, holder.image);
			holder.image.setVisibility(View.VISIBLE);
		} else
			holder.image.setVisibility(View.GONE);
		if (holder.state == 1) {
			holder.check.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_action_read_active));
			// Utility.log(TAG, holder.id + " is checked");
		} else {
			holder.check.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_action_read_white));
			// Utility.log(TAG, holder.id + " is unchecked");
		}
		view.setTag(holder);
		return view;
	}
}
