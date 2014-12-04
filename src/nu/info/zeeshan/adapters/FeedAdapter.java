package nu.info.zeeshan.adapters;

import java.util.ArrayList;

import nu.info.zeeshan.rnf.R;
import nu.info.zeeshan.utility.Feed;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FeedAdapter extends ArrayAdapter<Feed>{
	ArrayList<Feed> values;
	Context context;
	public FeedAdapter(Context contxt, int resource, ArrayList<Feed> objects) {
		super(contxt, resource, objects);
		context=contxt;
		values=objects;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=inflater.inflate(R.layout.feed_item, parent,false);
			holder=new ViewHolder();
			holder.desc=(TextView)convertView.findViewById(R.id.textViewDesc);
			holder.time=(TextView)convertView.findViewById(R.id.textViewTime);
			holder.title=(TextView)convertView.findViewById(R.id.textViewTitle);
			convertView.setTag(holder);
		}
		else{
			holder=(ViewHolder)convertView.getTag();
		}
		holder.title.setText(values.get(position).getTitle());
		holder.desc.setText(values.get(position).getDesc());
		holder.time.setText(values.get(position).getTime());
		return convertView;
	}
	
	static class ViewHolder{
		TextView title;
		TextView desc;
		TextView time;
	}
}
