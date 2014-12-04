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
		public static ArrayList<Feed> feeds=new ArrayList<Feed>();
		public static final String TAG="nu.info.zeeshan.FragmentMain"; 
		FeedAdapter adapter;
		ViewHolder holder;
		Bundle b;
		SharedPreferences spf;
		static Feed feed;
		static String newsfeed;
		static String facebookfeed;
		static int url_changed=-1;
		
		private static String TAG_CHANNEL = "channel";
	    private static String TAG_TITLE = "title";
	    private static String TAG_LINK = "link";
	    private static String TAG_DESRIPTION = "description";
	    private static String TAG_LANGUAGE = "language";
	    private static String TAG_ITEM = "item";
	    private static String TAG_PUB_DATE = "pubDate";
	    private static String TAG_GUID = "guid";
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
			rootView.setTag(holder);
			b=this.getArguments();
			holder.position=b.getInt(getString(R.string.bundle_arg_position));
			
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
		class LoadXML extends AsyncTask<String, Void, Boolean>{

			@Override
			protected Boolean doInBackground(String... arg0) {
				try {
					//URL url=arg0[0];
					Document doc=getDomElement(getXmlFromUrl(arg0[0]));
					NodeList nodeList = doc.getElementsByTagName(TAG_CHANNEL);
					Element e = (Element) nodeList.item(0);
	                NodeList items = e.getElementsByTagName(TAG_ITEM);
	                int len=items.getLength();
	                for(int i = 0; i <len ; i++){
	                	Element e1 = (Element) items.item(i);
	                    feed=new Feed();
	                    feed.setTitle(getValue(e1, TAG_TITLE));
	                    feed.setLink(getValue(e1, TAG_LINK));
	                    feed.setDesc(getValue(e1, TAG_DESRIPTION));
	                    feed.setTime(getValue(e1, TAG_PUB_DATE));
	                    feeds.add(feed);
	                }
	                DbHelper dbh=new DbHelper(getActivity());
	                dbh.fillFeed(feeds);
	                //for(Feed f:feeds)
	               	//Utility.log(TAG,""+f.getLink());
				/*	HttpURLConnection conn=(HttpURLConnection)url.openConnection();
					conn.setReadTimeout(10000 );
	                  conn.setConnectTimeout(15000);
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
	                  */
	                  
	            return true;      
				} catch (Exception e) {
					feeds.clear();
					Utility.log("doInBackgroud", ""+e+e.getLocalizedMessage());
					return false;
				}
			
			}
			
			@Override
			protected void onPostExecute(Boolean res){
				setAdapter();
				if(res){
					Utility.log("onPostExecute", "done downloading");
				}
				else{
					showError(getString(R.string.error_feed));
				}
			}
		}
		public void setAdapter(){
		//	Utility.log("SET", ""+feeds.size());
			adapter.clear();
		//	Utility.log("SET", ""+feeds.size());
			adapter.addAll(feeds);
		//	Utility.log("SET", ""+adapter.getCount());
			adapter.notifyDataSetChanged();
		}
		static class ViewHolder{
			ListView list;
			ProgressBar progress;
			LinearLayout errorView;
			TextView errorMsg;
			int position;
		}
		 /**
	     * Method to get xml content from url HTTP Get request
	     * */
	    public String getXmlFromUrl(String url) {
	        String xml = null;
	 
	        try {
	            // request method is GET
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpGet httpGet = new HttpGet(url);
	 
	            HttpResponse httpResponse = httpClient.execute(httpGet);
	            HttpEntity httpEntity = httpResponse.getEntity();
	            xml = EntityUtils.toString(httpEntity);
	 
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        // return XML
	        return xml;
	    }
	 
		 public static final Document getDomElement(String xml) {
		        Document doc = null;
		        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		        try {
		 
		            DocumentBuilder db = dbf.newDocumentBuilder();
		 
		            InputSource is = new InputSource();
		            is.setCharacterStream(new StringReader(xml));
		            doc = (Document) db.parse(is);
		 
		        } catch (ParserConfigurationException e) {
		            Log.e(TAG, e+e.getMessage());
		            return null;
		        } catch (SAXException e) {
		            Log.e(TAG, e+e.getMessage());
		            return null;
		        } catch (IOException e) {
		            Log.e(TAG, e+e.getMessage());
		            return null;
		        }
		 
		        return doc;
		    }
		 public String getValue(Element item, String str) {
			 NodeList n = item.getElementsByTagName(str);
			 return this.getElementValue(n.item(0));
		 }
		 /**
		     * Getting node value
		     * 
		     * @param elem element
		     */
		    public final String getElementValue(Node elem) {
		        Node child;
		        if (elem != null) {
		            if (elem.hasChildNodes()) {
		                for (child = elem.getFirstChild(); child != null; child = child
		                        .getNextSibling()) {
		                    if (child.getNodeType() == Node.TEXT_NODE || ( child.getNodeType() == Node.CDATA_SECTION_NODE)) {
		                        return child.getNodeValue();
		                    }
		                }
		            }
		        }
		        return "";
		    }
}
