package nu.info.zeeshan.rnf.dao;

import java.util.ArrayList;

import nu.info.zeeshan.rnf.utility.Feed;
import nu.info.zeeshan.rnf.utility.Utility;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
	public static int DATABASE_VERSION=6;
	private static String TAG="nu.info.zeeshan..rnf.dao.DbHelper";
	public static String DATABASE_NAME="redb.db";
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null,DATABASE_VERSION);
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
	public int feedRead(int id){
		int res;
		SQLiteDatabase db=this.getWritableDatabase();
		//ContentValues value=new ContentValues();
		//value.put(DbStructure.FeedTable.COLUMN_STATE, 1);
		//db.update(DbStructure.FeedTable.TABLE_NAME, values, whereClause, whereArgs);
		db.execSQL("update feeds set state=1-state where _id="+id);//DbConstants.UPDATE+DbStructure.FeedTable.TABLE_NAME+DbConstants.SET+DbStructure.FeedTable.COLUMN_STATE+DbConstants.EQUALS+DbConstants.ONE,null);
		Cursor c=db.rawQuery("select state from feeds where _id="+id,null);
		if(c.moveToFirst())
			res=c.getInt(c.getColumnIndexOrThrow(DbStructure.FeedTable.COLUMN_STATE));
		else 
			res=0;
		c.close();
		return res;
	}
	public void fillFeed(ArrayList<Feed> feeds,int type){
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues values;
		for(Feed f:feeds){
			values=new ContentValues();
			values.put(DbStructure.FeedTable.COLUMN_TITLE, f.getTitle());
			values.put(DbStructure.FeedTable.COLUMN_TEXT, f.getDesc());
			values.put(DbStructure.FeedTable.COLUMN_TIME, f.getTime());
			values.put(DbStructure.FeedTable.COLUMN_LINK, f.getLink());
			values.put(DbStructure.FeedTable.COLUMN_IMAGE, f.getImage());
			values.put(DbStructure.FeedTable.COLUMN_TYPE, type);
			db.insert(DbStructure.FeedTable.TABLE_NAME, null, values);
		}
		Utility.log(TAG, "inserted type"+type+" values "+feeds.size());
	}
	
}
