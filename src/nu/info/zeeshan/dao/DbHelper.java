package nu.info.zeeshan.dao;

import java.util.ArrayList;

import nu.info.zeeshan.utility.Feed;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
	public static int DATABASE_VERSION=1;
	public static String DATABASE_NAME="nrf.db";
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null,1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DbStructure.FeedTable.COMMAND_CREATE);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DbStructure.FeedTable.COMMAND_DROP);
		onCreate(db);
	}
	public void fillFeed(ArrayList<Feed> feeds){
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(DbStructure.FeedTable.TABLE_NAME, null, null);
		ContentValues values=new ContentValues();
		for(Feed f:feeds){
			values.put(DbStructure.FeedTable.COLUMN_TITLE, f.getTitle());
			values.put(DbStructure.FeedTable.COLUMN_TEXT, f.getDesc());
			values.put(DbStructure.FeedTable.COLUMN_TIME, f.getTime());
			values.put(DbStructure.FeedTable.COLUMN_LINK, f.getLink());
			db.insert(DbStructure.FeedTable.TABLE_NAME, null, values);
		}
	}
}
