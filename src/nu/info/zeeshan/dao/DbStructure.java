package nu.info.zeeshan.dao;

import android.provider.BaseColumns;

public class DbStructure {
	public static abstract class FeedTable implements BaseColumns{
		public final static String TABLE_NAME="feeds";
		
		public final static String COLUMN_TITLE="title";
		public final static String COLUMN_TEXT="text";
		public final static String COLUMN_TIME="time";
		public final static String COLUMN_LINK="link";
		public final static String COLUMN_IMAGE="image";
		public final static String COLUMN_TYPE="type"; //0 news 1 facebook
		public final static String COMMAND_CREATE="create table "+TABLE_NAME+DbConstants.BRACES_OPEN+_ID+DbConstants.TYPE_INT+DbConstants.CONSTRAIN_PRIMARY_KEY+DbConstants.COMMA
				+COLUMN_TITLE+DbConstants.TYPE_TEXT+DbConstants.COMMA
				+COLUMN_TEXT+DbConstants.TYPE_TEXT+DbConstants.COMMA
				+COLUMN_LINK+DbConstants.TYPE_TEXT+DbConstants.COMMA
				+COLUMN_TYPE+DbConstants.TYPE_INT+DbConstants.COMMA
				+COLUMN_IMAGE+DbConstants.TYPE_TEXT+DbConstants.COMMA
				+COLUMN_TIME+DbConstants.TYPE_TEXT+DbConstants.COMMA
				+DbConstants.UNIQUE+DbConstants.BRACES_OPEN+COLUMN_TITLE+DbConstants.COMMA+COLUMN_TIME+DbConstants.BRACES_CLOSE
				+DbConstants.CONFLICT_POLICY+DbConstants.BRACES_CLOSE;
		public final static String COMMAND_DROP="drop table "+TABLE_NAME;
	}
}
