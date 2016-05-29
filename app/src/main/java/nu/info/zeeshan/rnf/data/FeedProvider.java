package nu.info.zeeshan.rnf.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import nu.info.zeeshan.rnf.util.Util;

/**
 * Created by Zeeshan Khan on 5/22/2016.
 */
public class FeedProvider extends ContentProvider {

    public static final UriMatcher feedsUriMatcher = buildUriMatcher();
    public static final int FACEBOOK = 1;
    public static final int NEWS = 2;
    private static final SQLiteQueryBuilder newsQueryBuilder, facebookQueryBuilder;
    private FeedsDbHelper feedsDbHelper;
    public static final String TAG = FeedProvider.class.getSimpleName();

    static {
        newsQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //feeds INNER JOIN newsfeeds ON newsfeeds.feed_id = feeds._id
        newsQueryBuilder.setTables(
                FeedContract.FeedTable.TABLE_NAME + " INNER JOIN " +
                        FeedContract.NewsFeedTable.TABLE_NAME +
                        " ON " + FeedContract.NewsFeedTable.TABLE_NAME +
                        "." + FeedContract.NewsFeedTable.COLUMN_FEED_ID +
                        " = " + FeedContract.FeedTable.TABLE_NAME +
                        "." + FeedContract.FeedTable._ID);

        facebookQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //feeds INNER JOIN facebookfeeds ON facebookfeeds.feed_id = feeds._id
        facebookQueryBuilder.setTables(
                FeedContract.FeedTable.TABLE_NAME + " INNER JOIN " +
                        FeedContract.FacebookFeedTable.TABLE_NAME +
                        " ON " + FeedContract.FacebookFeedTable.TABLE_NAME +
                        "." + FeedContract.FacebookFeedTable.COLUMN_FEED_ID +
                        " = " + FeedContract.FeedTable.TABLE_NAME +
                        "." + FeedContract.FeedTable._ID);
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FeedContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, FeedContract.PATH_FACEBOOK, FACEBOOK);
        matcher.addURI(authority, FeedContract.PATH_NEWS, NEWS);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        feedsDbHelper = new FeedsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (feedsUriMatcher.match(uri)) {
            case FACEBOOK:
                retCursor = facebookQueryBuilder.query(feedsDbHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NEWS:
                retCursor = newsQueryBuilder.query(feedsDbHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = feedsUriMatcher.match(uri);
        switch (match) {
            case FACEBOOK:
                return FeedContract.FacebookFeedTable.CONTENT_TYPE;
            case NEWS:
                return FeedContract.NewsFeedTable.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = feedsUriMatcher.match(uri);
        SQLiteDatabase db;
        long feedId;
        switch (match) {
            case FACEBOOK:
                //insert into feed and facebookFeed table
                db = feedsDbHelper.getWritableDatabase();
                feedId = insertFeed(values, db);
                if (feedId != -1) {
                    values.put(FeedContract.FacebookFeedTable.COLUMN_FEED_ID, feedId);
                    db.insert(FeedContract.FacebookFeedTable.TABLE_NAME, null, values);
                }
                db.close();
                break;
            case NEWS:
                //insert into feed and newsfeed table
                db = feedsDbHelper.getWritableDatabase();
                feedId = insertFeed(values, db);
                if (feedId != -1) {
                    values.put(FeedContract.NewsFeedTable.COLUMN_FEED_ID, feedId);
                    db.insert(FeedContract.NewsFeedTable.TABLE_NAME, null, values);
                }
                db.close();
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return null;
    }

    private long insertFeed(ContentValues values, SQLiteDatabase db) {
        return db.insert(FeedContract.FeedTable.TABLE_NAME, null, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = feedsUriMatcher.match(uri);
        final SQLiteDatabase db = feedsDbHelper.getWritableDatabase();
        String[] columns;
        Cursor cursor;
        int COL_FEED_ID_INDEX = 0;
        List<String> feedSelectionArgs;
        StringBuilder feedSelection;
        int deletedItemCount;
        switch (match) {
            case FACEBOOK:
                columns = new String[]{
                    FeedContract.FacebookFeedTable.COLUMN_FEED_ID
            };

                cursor = db.query(FeedContract.FacebookFeedTable.TABLE_NAME, columns,
                        selection, selectionArgs, null, null, null);
                feedSelectionArgs = new ArrayList<>();
                feedSelection = new StringBuilder("_id IN (");
                if (cursor.moveToFirst()) {
                    do {
                        feedSelectionArgs.add(String.valueOf(cursor.getLong(COL_FEED_ID_INDEX)));
                        feedSelection.append("?,");
                    } while (cursor.moveToNext());
                }
                cursor.close();
                db.delete(FeedContract.FacebookFeedTable.TABLE_NAME,
                        selection, selectionArgs);
                feedSelection.deleteCharAt(feedSelection.lastIndexOf(","));
                feedSelection.append(")");
                deletedItemCount=db.delete(FeedContract.FeedTable.TABLE_NAME, feedSelection
                        .toString(), feedSelectionArgs.toArray(new String[]{}));
                break;
            case NEWS:
                columns = new String[]{
                        FeedContract.NewsFeedTable.COLUMN_FEED_ID
                };

                cursor = db.query(FeedContract.NewsFeedTable.TABLE_NAME, columns,
                        selection, selectionArgs, null, null, null);
                feedSelectionArgs = new ArrayList<>();
                feedSelection = new StringBuilder("_id IN (");
                if (cursor.moveToFirst()) {
                    do {
                        feedSelectionArgs.add(String.valueOf(cursor.getLong(COL_FEED_ID_INDEX)));
                        feedSelection.append("?,");
                    } while (cursor.moveToNext());
                }
                cursor.close();
                db.delete(FeedContract.NewsFeedTable.TABLE_NAME,
                        selection, selectionArgs);
                feedSelection.deleteCharAt(feedSelection.lastIndexOf(","));
                feedSelection.append(")");
                deletedItemCount=db.delete(FeedContract.FeedTable.TABLE_NAME, feedSelection
                        .toString(), feedSelectionArgs.toArray(new String[]{}));
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return deletedItemCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
