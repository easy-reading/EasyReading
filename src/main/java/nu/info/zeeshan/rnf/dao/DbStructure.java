package nu.info.zeeshan.rnf.dao;

import android.provider.BaseColumns;

public class DbStructure {
	public static abstract class FeedTable implements BaseColumns {

		public final static String COLUMN_TITLE = "title";
		public final static String COLUMN_TEXT = "text";
		public final static String COLUMN_TIME = "time";
		public final static String COLUMN_LINK = "link";
		public final static String COLUMN_IMAGE = "image";
		public final static String COLUMN_STATE = "state";

	}

	public static abstract class NewsFeedTable extends FeedTable {
		public final static String TABLE_NAME = "newsfeeds";
		public final static String COLUMN_PUBLISHER="publisher";
		public final static String COMMAND_CREATE = "create table "
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_TITLE + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_TEXT + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_LINK + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_PUBLISHER + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_STATE + DbConstants.TYPE_INT
				+ DbConstants.DEFAULT + DbConstants.State.UNREAD
				+ DbConstants.COMMA + COLUMN_IMAGE + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_TIME + DbConstants.TYPE_INT
				+ DbConstants.COMMA + DbConstants.UNIQUE
				+ DbConstants.BRACES_OPEN + COLUMN_TITLE + DbConstants.BRACES_CLOSE
				+ DbConstants.CONFLICT_POLICY + DbConstants.BRACES_CLOSE;
		public final static String COMMAND_DROP = "drop table " + TABLE_NAME;
	}

	public static abstract class FacebookFeedTable extends FeedTable {
		public final static String TABLE_NAME = "facebookfeeds";

		public final static String COLUMN_LIKES="likes";
		public final static String COMMAND_CREATE = "create table "
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_TEXT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_TITLE + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_TEXT + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_LINK + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_LIKES + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_STATE + DbConstants.TYPE_INT
				+ DbConstants.DEFAULT + DbConstants.State.UNREAD
				+ DbConstants.COMMA + COLUMN_IMAGE + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_TIME + DbConstants.TYPE_INT
				+ DbConstants.COMMA + DbConstants.UNIQUE
				+ DbConstants.BRACES_OPEN + COLUMN_TITLE +  DbConstants.BRACES_CLOSE
				+ DbConstants.CONFLICT_POLICY + DbConstants.BRACES_CLOSE;
		public final static String COMMAND_DROP = "drop table " + TABLE_NAME;
	}
}
