package nu.info.zeeshan.adapters;

import java.util.Date;

import nu.info.zeeshan.dao.DbStructure;
import nu.info.zeeshan.rnf.R;
import nu.info.zeeshan.utility.Utility;
import nu.info.zeeshan.utility.Utility.ViewHolder;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class NewsAdapter extends CursorAdapter {
	private static final String TAG = "nu.info.zeeshan.dao.adapters.NewsAdapter";
	Cursor c;
	Context context;
	ViewHolder holder;
	Date date = new Date();

	public NewsAdapter(Context contxt, Cursor cc) {
		super(contxt, cc, 1);
		c = cc;
		context = contxt;
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		holder = (ViewHolder) view.getTag();
		holder.title.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TITLE)));
		date.setTime(c.getLong(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TIME)));
		holder.time.setText(Utility.dformat.format(date));
		holder.desc.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TEXT)));
		holder.image.setImageDrawable(context.getResources().getDrawable(
				R.drawable.default_news));
		holder.id = c
				.getInt(c.getColumnIndexOrThrow(DbStructure.FeedTable._ID));
		holder.state = c.getInt(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_STATE));
		holder.type = c.getInt(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TYPE));
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
		String imgsrc = c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_IMAGE));
		if (imgsrc != null && !imgsrc.trim().isEmpty())
			ImageLoader
					.getInstance()
					.displayImage(
							c.getString(c
									.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_IMAGE)),
							holder.image);

	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		holder = new ViewHolder();
		View view = ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.feed_item_news, parent, false);
		holder.title = (TextView) view.findViewById(R.id.textViewTitleNews);
		holder.desc = (TextView) view.findViewById(R.id.textViewDescNews);
		holder.time = (TextView) view.findViewById(R.id.textViewTimeNews);
		holder.image = (ImageView) view.findViewById(R.id.imageViewNews);
		holder.check = (ImageButton) view
				.findViewById(R.id.imageButtonReadNews);

		holder.title.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TITLE)));
		date.setTime(c.getLong(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TIME)));
		holder.time.setText(Utility.dformat.format(date));
		holder.desc.setText(c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TEXT)));
		holder.image.setImageDrawable(context.getResources().getDrawable(
				R.drawable.default_news));

		String imgsrc = c.getString(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_IMAGE));
		if (imgsrc != null && !imgsrc.trim().isEmpty())
			ImageLoader.getInstance().displayImage(imgsrc, holder.image);
		holder.id = c
				.getInt(c.getColumnIndexOrThrow(DbStructure.FeedTable._ID));
		holder.state = c.getInt(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_STATE));
		holder.type = c.getInt(c
				.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TYPE));
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
