package nu.info.zeeshan.utility;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nu.info.zeeshan.dao.DbHelper;

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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ProcessFeed extends AsyncTask<String, Void, Boolean>{
	
	private Context context;
	private int type;
	private static String TAG_CHANNEL = "channel";
    private static String TAG_TITLE = "title";
    private static String TAG_LINK = "link";
    private static String TAG_DESRIPTION = "description";
    private static String TAG_ITEM = "item";
    private static String TAG_PUB_DATE = "pubDate";
    private static String TAG = "nu.info.zeeshan.ProcessFeed";
	//static class LoadXML extends 
    public ProcessFeed(Context c,int t){
    	context=c;
    	type=t;
    }
		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				//URL url=arg0[0];
				ArrayList<Feed> feeds=new ArrayList<Feed>();
				Feed feed;
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
                DbHelper dbh=new DbHelper(context);
                dbh.fillFeed(feeds,type);
                //for(Feed f:feeds)
               	//Utility.log(TAG,""+f.getLink());
                  
            return true;      
			} catch (Exception e) {
				Utility.log("doInBackgroud", ""+e+e.getLocalizedMessage());
				return false;
			}
		
		}
		
		@Override
		protected void onPostExecute(Boolean res){
	//		setAdapter();
			if(res){
				Utility.log("onPostExecute", "done downloading");
			}
			else{
		//		showError(getString(R.string.error_feed));
			}
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
 
	 public final Document getDomElement(String xml) {
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
	 public  String getValue(Element item, String str) {
		 NodeList n = item.getElementsByTagName(str);
		 return getElementValue(n.item(0));
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
