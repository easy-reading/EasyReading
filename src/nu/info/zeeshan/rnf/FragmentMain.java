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
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentMain extends Fragment{
		public ArrayList<Feed> feeds=new ArrayList<Feed>();
		ListView feedlist;
		FeedAdapter adapter;
		Bundle b;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,false);
			feedlist=(ListView)rootView.findViewById(R.id.listViewFeed);
			return rootView;
		}
		
		@Override
		public void onStart(){
			super.onStart();
			//show progress dialog
			//
			try {
				b=this.getArguments();
				new LoadXML().execute(new URL(b.getString("URL")));
			} catch (MalformedURLException e) {
				
				Utility.log("error", e.getMessage());
			}
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
	                  feeds.clear();
	                  while (event != XmlPullParser.END_DOCUMENT) {
	                	  tagName=myparser.getName();
	                	  
	                	  switch(event){
	                	  	case XmlPullParser.TEXT:
	                	  		tmptext = new String(myparser.getText());
	                	  		break;
		                    case XmlPullParser.END_TAG:
		                    	if(tagName.equalsIgnoreCase("title")){
		                    		Utility.log("NEW FEED", "NEW FEED");
		                    		f=new Feed();
		                    	 f.title=Html.fromHtml(tmptext).toString();
		                    	 Utility.log("TITLE", ""+f.title);
		                    	}
		                    	else if(tagName.equalsIgnoreCase("pubDate")){
		                    	 f.time=Html.fromHtml(tmptext).toString();
		                    	 Utility.log("TIME", ""+f.time);
		                    	 feeds.add(f);
		                    		Utility.log("SIZE",""+feeds.size());
		                    	}
		                    	else if(tagName.equalsIgnoreCase("description")){
		                    		f.description=Html.fromHtml(tmptext).toString();
		                    		Utility.log("PARSED DATA", ""+f.description);
		                    		
		                    	}
		                    	break;
	                	  }
	                	  event=myparser.next();
	                  }
	                  
	                  
				} catch (Exception e) {
					Utility.log("error", ""+e.getMessage()+e.getLocalizedMessage());
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Boolean res){
				setAdapter();
				Utility.log("yaha", "done async task");
			}
		}
		public void setAdapter(){
			adapter=new FeedAdapter(getActivity(), R.layout.feed_item, feeds);
			feedlist.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
}
