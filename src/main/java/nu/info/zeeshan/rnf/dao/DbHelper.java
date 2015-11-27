package nu.info.zeeshan.rnf.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import nu.info.zeeshan.rnf.model.FacebookItem;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.model.NewsItem;
import nu.info.zeeshan.rnf.util.Util;

public class DbHelper extends SQLiteOpenHelper {

	public static int DATABASE_VERSION = 8;
	private static String TAG = "nu.info.zeeshan..rnf.dao.DbHelper";
	public static String DATABASE_NAME = "redb.db";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DbStructure.NewsFeedTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FacebookFeedTable.COMMAND_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DbStructure.NewsFeedTable.COMMAND_DROP);
		db.execSQL(DbStructure.FacebookFeedTable.COMMAND_DROP);
		onCreate(db);
	}
/*
	public int toggleFeedState(Object id) {
		int res;
		Cursor c;
		SQLiteDatabase db = this.getWritableDatabase();
		if (id instanceof Integer) {
			db.execSQL("update newsfeeds set state=1-state where _id=" + id);// DbConstants.UPDATE+DbStructure.FeedTable.TABLE_NAME+DbConstants.SET+DbStructure.FeedTable.COLUMN_STATE+DbConstants.EQUALS+DbConstants.ONE,null);
			c = db.rawQuery("select state from newsfeeds where _id=" + id, null);
		} else {
			db.execSQL("update facebookfeeds set state=1-state where _id='"
					+ id + "'");
			// DbConstants.UPDATE+DbStructure.FeedTable.TABLE_NAME+DbConstants.SET+DbStructure.FeedTable.COLUMN_STATE+DbConstants.EQUALS+DbConstants.ONE,null);
			c = db.rawQuery("select state from facebookfeeds where _id='" + id
					+ "'", null);
		}
		if (c.moveToFirst())
			res = c.getInt(c
					.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_STATE));
		else
			res = 0;
		c.close();
		return res;
	}
*/
	public List<Item> getFacebookFeeds(boolean latestOnly){
		List<Item> feeds=new ArrayList<>();
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor c;
		if(latestOnly) {
			c = db.query(DbStructure.FacebookFeedTable.TABLE_NAME, null, DbStructure.FacebookFeedTable.COLUMN_TIME +">= (CURRENT_TIMESTAMP - 86400000) ", null, null, null, DbStructure.FeedTable.COLUMN_TIME+DbConstants.DESC);
		}else{
			c = db.query(DbStructure.FacebookFeedTable.TABLE_NAME, null, null, null, null, null, DbStructure.FeedTable.COLUMN_TIME+DbConstants.DESC);
		}
		FacebookItem item;
		if(c.moveToFirst()){
			do{
				item=new FacebookItem();
				item.setTime(c.getLong(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TIME)));
				item.setLink(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_LINK)));
				item.setDesc(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TEXT)));
				item.setTitle(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TITLE)));
				item.setImage_url(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_IMAGE)));
				item.setId(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable._ID)));
				item.setLikes(c.getInt(c.getColumnIndexOrThrow(DbStructure.FacebookFeedTable.COLUMN_LIKES)));
				feeds.add(item);
			}while(c.moveToNext());
		}
		if(db!=null)
			db.close();
		return feeds;
	}
	public void fillFacebookFeed(List<FacebookItem> feeds) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values;
		int fbfeeds = 0;
		for (FacebookItem f : feeds) {
			values = new ContentValues();
			values.put(DbStructure.FeedTable._ID,f.getId());
			values.put(DbStructure.FeedTable.COLUMN_TITLE, f.getTitle());
			values.put(DbStructure.FeedTable.COLUMN_TEXT, f.getDesc());
			values.put(DbStructure.FeedTable.COLUMN_LINK, f.getLink());
			values.put(DbStructure.FacebookFeedTable.COLUMN_LIKES, f.getLikes());
			values.put(DbStructure.FeedTable.COLUMN_IMAGE, f.getImage_url());
			values.put(DbStructure.FeedTable.COLUMN_TIME, f.getTime());
			db.insertWithOnConflict(DbStructure.FacebookFeedTable.TABLE_NAME, null,
						values,SQLiteDatabase.CONFLICT_REPLACE);
			fbfeeds++;
		}
		if(db!=null)
			db.close();
		Util.log(TAG, feeds.size() + " facebook feeds inserted" + fbfeeds);
	}
	public List<Item> getNewsFeeds(boolean latestOnly){
		List<Item> feeds=new ArrayList<>();
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor c;
		if(latestOnly) {
			c = db.query(DbStructure.NewsFeedTable.TABLE_NAME, null, DbStructure.NewsFeedTable.COLUMN_TIME +">= (CURRENT_TIMESTAMP - 86400000) ", null, null, null,DbStructure.FeedTable.COLUMN_TIME+DbConstants.DESC);
		}else{
			c = db.query(DbStructure.NewsFeedTable.TABLE_NAME, null, null, null, null, null, DbStructure.FeedTable.COLUMN_TIME+DbConstants.DESC);
		}
		NewsItem item;
		if(c.moveToFirst()){
			do{
				item=new NewsItem();
				item.setTime(c.getLong(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TIME)));
				item.setLink(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_LINK)));
				item.setDesc(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TEXT)));
				item.setTitle(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_TITLE)));
				item.setImage_url(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_IMAGE)));
				item.setId(c.getString(c.getColumnIndexOrThrow(DbStructure.FeedTable._ID)));
				item.setPublisher(c.getString(c.getColumnIndexOrThrow(DbStructure.NewsFeedTable.COLUMN_PUBLISHER)));
				feeds.add(item);
			}while(c.moveToNext());
		}
		if(db!=null)
			db.close();
		return feeds;
	}
	public void fillNewsFeed(List<NewsItem> feeds) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values;
		int fbfeeds = 0;
		for (NewsItem f : feeds) {
			values = new ContentValues();
			values.put(DbStructure.FeedTable._ID,f.getId());
			values.put(DbStructure.FeedTable.COLUMN_TITLE, f.getTitle());
			values.put(DbStructure.FeedTable.COLUMN_TEXT, f.getDesc());
			values.put(DbStructure.FeedTable.COLUMN_TIME, f.getTime());
            values.put(DbStructure.FeedTable.COLUMN_LINK, f.getLink());
			values.put(DbStructure.NewsFeedTable.COLUMN_PUBLISHER, f.getPublisher());
			values.put(DbStructure.FeedTable.COLUMN_IMAGE, f.getImage_url());
			db.insertWithOnConflict(DbStructure.NewsFeedTable.TABLE_NAME, null,
					values,SQLiteDatabase.CONFLICT_REPLACE);
			fbfeeds++;
		}

			Util.log(TAG, feeds.size() + " news feeds inserted" + fbfeeds);
		if(db!=null)
			db.close();
	}
}