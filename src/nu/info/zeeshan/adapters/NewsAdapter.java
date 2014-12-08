package nu.info.zeeshan.adapters;

import com.nostra13.universalimageloader.core.ImageLoader;

import nu.info.zeeshan.dao.DbStructure;
import nu.info.zeeshan.rnf.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends CursorAdapter {
	Cursor c;
	Context context;
	ViewHolder holder;
	public NewsAdapter(Context contxt,Cursor cc) {
		super(contxt, cc, 1);
		c=cc;
		context=contxt;
	}
	@Override
	public void bindView(View view, Context context, Cursor c) {
		holder=(ViewHolder)view.getTag();
		holder.title.setText(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TITLE)));
		holder.time.setText(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TIME)));
		holder.desc.setText(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TEXT)));
		holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.default_news));
		ImageLoader.getInstance().displayImage(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_IMAGE)),holder.image);
		
	}
	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		holder=new ViewHolder();
		View view=((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.feed_item_news, parent, false);
		holder.title=(TextView)view.findViewById(R.id.textViewTitleNews);
		holder.desc=(TextView)view.findViewById(R.id.textViewDescNews);
		holder.time=(TextView)view.findViewById(R.id.textViewTimeNews);
		holder.image=(ImageView)view.findViewById(R.id.imageViewNews);
		
		holder.title.setText(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TITLE)));
		holder.time.setText(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TIME)));
		holder.desc.setText(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TEXT)));
		holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.default_news));
		ImageLoader.getInstance().displayImage(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_IMAGE)),holder.image);
		
		view.setTag(holder);
		return view;
	}
	static class ViewHolder{
		TextView title;
		TextView desc;
		TextView time;
		ImageView image;
	}
}
