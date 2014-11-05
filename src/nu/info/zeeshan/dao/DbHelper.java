package nu.info.zeeshan.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
	public static int DATABASE_VERSION=1;
	public static String DATABASE_NAME="nrf.db";
	public DbHelper(Context context,int version) {
		super(context, DATABASE_NAME, null, version);
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

}
