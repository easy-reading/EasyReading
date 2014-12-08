package nu.info.zeeshan.rnf;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nu.info.zeeshan.adapters.FeedAdapter;
import nu.info.zeeshan.dao.DbHelper;
import nu.info.zeeshan.utility.Feed;
import nu.info.zeeshan.utility.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FragmentMain extends Fragment{
		
		public static final String TAG="nu.info.zeeshan.FragmentMain"; 
		FeedAdapter adapter;
		ViewHolder holder;
		Bundle b;
		static SharedPreferences spf;
		static String newsfeed;
		static String facebookfeed;
		static int url_changed=-1;
		
	//	static String errorMsg="Check RSS Feeds in settings";
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,false);
			holder =new ViewHolder();
			holder.list=(ListView)rootView.findViewById(R.id.listViewFeed);
			holder.list.setEmptyView(rootView.findViewById(R.id.progressBar));
			adapter=new FeedAdapter(getActivity(), R.layout.feed_item, feeds);
			holder.list.setAdapter(adapter);
			holder.errorMsg=(TextView)rootView.findViewById(R.id.textViewError);
			holder.errorView=(LinearLayout)rootView.findViewById(R.id.linearViewError);
			b=this.getArguments();
			holder.position=b.getInt(getString(R.string.bundle_arg_position));
			rootView.setTag(holder);
			spf=((MainActivity)getActivity()).getSharedPreferences(getString(R.string.pref_filename),Context.MODE_PRIVATE);
			facebookfeed=spf.getString(getString(R.string.pref_facebookrss), getString(R.string.empty_feed));
			newsfeed=spf.getString(getString(R.string.pref_newsrss), getString(R.string.empty_feed));
			load();
			return rootView;
		}
		
		
		@Override
		public void onResume() {
			super.onResume();
			hideError();
			facebookfeed=spf.getString(getString(R.string.pref_facebookrss), getString(R.string.empty_feed));
			newsfeed=spf.getString(getString(R.string.pref_newsrss), getString(R.string.empty_feed));
			if(url_changed==0 && holder.position==0){
					//Utility.log("RESET","NEWS"+newsfeed);
					new LoadXML().execute(newsfeed);
					url_changed=-1;
				}
				else if(url_changed==1 && holder.position==1){
					//	Utility.log("RESET","FACEBOOK "+facebookfeed);
					new LoadXML().execute(facebookfeed);
					url_changed=-1;
				}
		}
		public void showError(String msg){
			
			holder.errorMsg.setText(msg);
			holder.errorView.setVisibility(View.VISIBLE);

		}
		 public void load(){
			hideError();
			switch(holder.position){
			case 0:
				new LoadXML().execute(newsfeed);
				break;
			case 1:
				new LoadXML().execute(facebookfeed);
				break;
			}
		}
		public void hideError(){
			holder.errorView.setVisibility(View.GONE);
			}
		
		
		public static void setAdapter(){
			Utility.log("SET", ""+feeds.size());
			adapter.clear();
			Utility.log("SET", ""+feeds.size());
			adapter.addAll(feeds);
			Utility.log("SET", ""+adapter.getCount());
			adapter.notifyDataSetChanged();
		}
		static class ViewHolder{
			ListView list;
			ProgressBar progress;
			LinearLayout errorView;
			TextView errorMsg;
			int position;
		}
		
}
