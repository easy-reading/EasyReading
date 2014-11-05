package nu.info.zeeshan.rnf;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import nu.info.zeeshan.adapters.FeedAdapter;
import nu.info.zeeshan.utility.Feed;
import nu.info.zeeshan.utility.Utility;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FragmentMain extends Fragment{
		public ArrayList<Feed> feeds=new ArrayList<Feed>();
		public static final String TAG="nu.info.zeeshan.FragmentMain"; 
		FeedAdapter adapter;
		ViewHolder holder;
		Bundle b;
		static String newsfeed="http://www.thehindu.com/news/national/?service=rss";
		static String facebookfeed="https://www.facebook.com/feeds/notifications.php?id=100004366590327&viewer=100004366590327&key=AWjLdGV4a_ncSjfj&format=rss20";
		static int url_changed=-1;
		static String errorMsg="Check RSS Feeds in settings";
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
			rootView.setTag(holder);
			b=this.getArguments();
			holder.position=b.getInt("position");
			load();
			return rootView;
		}
		
		
		@Override
		public void onResume() {
			super.onResume();
			hideError();
				try {
					if(url_changed==0 && holder.position==0){
						Utility.log("RESET","NEWS"+newsfeed);
						new LoadXML().execute(new URL(newsfeed));
						url_changed=-1;
					}
					else if(url_changed==1 && holder.position==1){
						Utility.log("RESET","FACEBOOK "+facebookfeed);
						new LoadXML().execute(new URL(facebookfeed));
						url_changed=-1;
					}
				} catch (MalformedURLException e) {
					showError("Please Check the RSS Feed");
					Utility.log("onResume",e.getClass()+":"+e.getMessage()+","+e.getLocalizedMessage()+" "+getActivity().getSharedPreferences(getActivity().getString(R.string.pref_filename), Context.MODE_PRIVATE).getString(getActivity().getString(R.string.pref_newsrss), null));
				}
		}
		public void showError(String msg){
			
			holder.errorMsg.setText(msg);
			holder.errorView.setVisibility(View.VISIBLE);

		}
		public void load(){
			hideError();
			try {
				switch(holder.position){
				case 0:
					new LoadXML().execute(new URL(newsfeed));
					break;
				case 1:
					new LoadXML().execute(new URL(facebookfeed));
					break;
				}
			} catch (MalformedURLException e) {
				showError(errorMsg);
				Utility.log("mar gaye","yaha");
			}
		}
		public void hideError(){
			holder.errorView.setVisibility(View.GONE);
			}
		class LoadXML extends AsyncTask<URL, Void, Boolean>{

			@Override
			protected Boolean doInBackground(URL... arg0) {
				try {
					URL url=arg0[0];
					HttpURLConnection conn=(HttpURLConnection)url.openConnection();
					conn.setReadTimeout(10000 /* milliseconds */);
	                  conn.setConnectTimeout(15000 /* milliseconds */);
	                  conn.setRequestMethod("GET");
	                  conn.setDoInput(true);
	                  conn.connect();
	                  InputStream stream=conn.getInputStream();
	                  
	                  XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
	                  XmlPullParser myparser = xmlFactoryObject.newPullParser();
	                  myparser.setInput(stream, null);
	                  int event;
	                  String tmptext="",tagName;
	                  Feed f=null;
	                  event=myparser.getEventType();
	                  feeds=new ArrayList<Feed>();
	                  while (event != XmlPullParser.END_DOCUMENT) {
	                	  tagName=myparser.getName();
	                	  
	                	  switch(event){
	                	  	case XmlPullParser.TEXT:
	                	  		tmptext = new String(myparser.getText());
	                	  		break;
		                    case XmlPullParser.END_TAG:
		                    	if(tagName.equalsIgnoreCase("title")){
		                    	//	Utility.log("NEW FEED", "NEW FEED");
		                    		f=new Feed();
		                    	 f.title=Html.fromHtml(tmptext).toString();
		                    	// Utility.log("TITLE", ""+f.title);
		                    	}
		                    	else if(tagName.equalsIgnoreCase("pubDate")){
		                    	 f.time=Html.fromHtml(tmptext).toString();
		                    	// Utility.log("TIME", ""+f.time);
		                    	 feeds.add(f);
		                    	//	Utility.log("SIZE",""+feeds.size());
		                    	}
		                    	else if(tagName.equalsIgnoreCase("description")){
		                    		f.description=Html.fromHtml(tmptext).toString();
		                    		//Utility.log("PARSED DATA", ""+f.description);
		                    		
		                    	}
		                    	break;
	                	  }
	                	  event=myparser.next();
	                  }
	                  
	            return true;      
				} catch (Exception e) {
					feeds.clear();
					Utility.log("doInBackgroud", e.getClass()+":"+e.getMessage()+","+e.getLocalizedMessage());
					return false;
				}
			
			}
			
			@Override
			protected void onPostExecute(Boolean res){
				setAdapter();
				if(res){
					Utility.log("onPostExecute", "done async task");
				}
				else{
					showError(errorMsg);
				}
			}
		}
		public void setAdapter(){
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
