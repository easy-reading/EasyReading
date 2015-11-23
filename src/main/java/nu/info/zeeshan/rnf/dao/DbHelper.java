package nu.info.zeeshan.rnf.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	public static int DATABASE_VERSION = 6;
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
	public void fillFeed(ArrayList<Item> feeds) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values;
		int fbfeeds = 0;
		for (Feed f : feeds) {
			values = new ContentValues();
			values.put(DbStructure.FeedTable._ID,f.getId());
			values.put(DbStructure.FeedTable.COLUMN_TITLE, f.getTitle());
			values.put(DbStructure.FeedTable.COLUMN_TEXT, f.getDesc());
			values.put(DbStructure.FeedTable.COLUMN_TIME, f.getTime());
			values.put(DbStructure.FeedTable.COLUMN_IMAGE, f.getImage_url());
			db.insert(DbStructure.FacebookFeedTable.TABLE_NAME, null,
						values);
			fbfeeds++;
		}
		Utility.log(TAG, feeds.size() + " new feeds inserted, facebook:"+ fbfeeds);
	}
	
}
