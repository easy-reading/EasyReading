package nu.info.zeeshan.rnf.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class FeedContract {
    public static final String CONTENT_AUTHORITY = "nu.info.zeeshan.rnf";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_NEWS = "news";
    public static final String PATH_FACEBOOK = "facebook";

    public static abstract class FeedTable implements BaseColumns {

        public final static String TABLE_NAME = "feeds";

        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_TEXT = "text";
        public final static String COLUMN_TIME = "time";
        public final static String COLUMN_LINK = "link";
        public final static String COLUMN_IMAGE = "image";
        public final static String COLUMN_STATE = "state";

        public final static String COMMAND_CREATE = "create table "
                + TABLE_NAME + DbConstants.BRACES_OPEN + _ID
                + DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
                + DbConstants.COMMA + COLUMN_TITLE + DbConstants.TYPE_TEXT
                + DbConstants.COMMA + COLUMN_TEXT + DbConstants.TYPE_TEXT
                + DbConstants.COMMA + COLUMN_LINK + DbConstants.TYPE_TEXT
                + DbConstants.COMMA + COLUMN_STATE + DbConstants.TYPE_INT
                + DbConstants.DEFAULT + DbConstants.State.UNREAD
                + DbConstants.COMMA + COLUMN_IMAGE + DbConstants.TYPE_TEXT
                + DbConstants.COMMA + COLUMN_TIME + DbConstants.TYPE_INT
                + DbConstants.COMMA + DbConstants.UNIQUE
                + DbConstants.BRACES_OPEN + COLUMN_TITLE + DbConstants.BRACES_CLOSE
                + DbConstants.CONFLICT_POLICY_IGNORE + DbConstants.BRACES_CLOSE;
        public final static String COMMAND_DROP = "drop table " + TABLE_NAME;
    }

    public static abstract class NewsFeedTable implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS)
                .build();
        public final static String TABLE_NAME = "newsfeeds";

        public final static String COLUMN_FEED_ID = "feed_id";
        public final static String COLUMN_PUBLISHER = "publisher";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;

        public final static String COMMAND_CREATE = "create table "
                + TABLE_NAME + DbConstants.BRACES_OPEN + _ID
                + DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
                + DbConstants.COMMA + COLUMN_FEED_ID + DbConstants.TYPE_INT + DbConstants.NOT_NULL
                + DbConstants.COMMA + COLUMN_PUBLISHER + DbConstants.TYPE_TEXT
                + DbConstants.DEFAULT + DbConstants.State.UNREAD
                + DbConstants.COMMA + DbConstants.CONSTRAIN_FOREIGN_KEY
                + DbConstants.BRACES_OPEN + COLUMN_FEED_ID + DbConstants.BRACES_CLOSE +
                DbConstants.REFERENCES + FeedTable.TABLE_NAME + DbConstants.BRACES_OPEN +
                FeedTable._ID + DbConstants.BRACES_CLOSE + DbConstants.BRACES_CLOSE;
        public final static String COMMAND_DROP = "drop table " + TABLE_NAME;
    }

    public static abstract class FacebookFeedTable implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath
                (PATH_FACEBOOK).build();
        public final static String TABLE_NAME = "facebookfeeds";

        public final static String COLUMN_FEED_ID = "feed_id";
        public final static String COLUMN_LIKES = "likes";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_FACEBOOK;

        public final static String COMMAND_CREATE = "create table "
                + TABLE_NAME + DbConstants.BRACES_OPEN + _ID
                + DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
                + DbConstants.COMMA + COLUMN_FEED_ID + DbConstants.TYPE_INT + DbConstants.NOT_NULL
                + DbConstants.COMMA + COLUMN_LIKES + DbConstants.TYPE_INT
                + DbConstants.DEFAULT + DbConstants.State.UNREAD
                + DbConstants.COMMA + DbConstants.CONSTRAIN_FOREIGN_KEY
                + DbConstants.BRACES_OPEN + COLUMN_FEED_ID + DbConstants.BRACES_CLOSE +
                DbConstants.REFERENCES + FeedTable.TABLE_NAME + DbConstants.BRACES_OPEN +
                FeedTable._ID + DbConstants.BRACES_CLOSE + DbConstants.BRACES_CLOSE;
        public final static String COMMAND_DROP = "drop table " + TABLE_NAME;
    }
}
