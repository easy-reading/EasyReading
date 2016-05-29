package nu.info.zeeshan.rnf.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import nu.info.zeeshan.rnf.model.FacebookItem;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.model.MultimediaItem;
import nu.info.zeeshan.rnf.model.NewsItem;
import nu.info.zeeshan.rnf.util.Util;

public class FeedsDbHelper extends SQLiteOpenHelper {

    public static int DATABASE_VERSION = 11;
    private static String TAG = FeedsDbHelper.class.getSimpleName();
    public static String DATABASE_NAME = "redb.db";
    public static SQLiteQueryBuilder facebookQuery;
    public static SQLiteQueryBuilder newsQuery;

    static {
        facebookQuery = new SQLiteQueryBuilder();
        facebookQuery.setTables(FeedContract.FeedTable.TABLE_NAME + " INNER JOIN " + FeedContract
                .FacebookFeedTable.TABLE_NAME + " ON " + FeedContract.FeedTable.TABLE_NAME + "."
                + FeedContract.FeedTable._ID + "=" + FeedContract.FacebookFeedTable.TABLE_NAME +
                "." + FeedContract.FacebookFeedTable.COLUMN_FEED_ID);
        newsQuery = new SQLiteQueryBuilder();
        newsQuery.setTables(FeedContract.FeedTable.TABLE_NAME + " INNER JOIN " + FeedContract
                .NewsFeedTable.TABLE_NAME + " ON " + FeedContract.FeedTable.TABLE_NAME + "."
                + FeedContract.FeedTable._ID + "=" + FeedContract.NewsFeedTable.TABLE_NAME +
                "." + FeedContract.NewsFeedTable.COLUMN_FEED_ID);
    }

    public FeedsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Util.log(TAG, FeedContract.FeedTable.COMMAND_CREATE);
        db.execSQL(FeedContract.FeedTable.COMMAND_CREATE);
        Util.log(TAG, FeedContract.NewsFeedTable.COMMAND_CREATE);
        db.execSQL(FeedContract.NewsFeedTable.COMMAND_CREATE);
        Util.log(TAG, FeedContract.FacebookFeedTable.COMMAND_CREATE);
        db.execSQL(FeedContract.FacebookFeedTable.COMMAND_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(FeedContract.NewsFeedTable.COMMAND_DROP);
        db.execSQL(FeedContract.FacebookFeedTable.COMMAND_DROP);
        db.execSQL(FeedContract.FeedTable.COMMAND_DROP);
        onCreate(db);
    }

    /*
        public int toggleFeedState(Object id) {
            int res;
            Cursor c;
            SQLiteDatabase db = this.getWritableDatabase();
            if (id instanceof Integer) {
                db.execSQL("update newsfeeds set state=1-state where _id=" + id);// DbConstants
                .UPDATE+FeedContract.FeedTable.TABLE_NAME+DbConstants.SET+FeedContract.FeedTable
                .COLUMN_STATE+DbConstants.EQUALS+DbConstants.ONE,null);
                c = db.rawQuery("select state from newsfeeds where _id=" + id, null);
            } else {
                db.execSQL("update facebookfeeds set state=1-state where _id='"
                        + id + "'");
                // DbConstants.UPDATE+FeedContract.FeedTable.TABLE_NAME+DbConstants
                .SET+FeedContract.FeedTable.COLUMN_STATE+DbConstants.EQUALS+DbConstants.ONE,null);
                c = db.rawQuery("select state from facebookfeeds where _id='" + id
                        + "'", null);
            }
            if (c.moveToFirst())
                res = c.getInt(c
                        .getColumnIndexOrThrow(FeedContract.FeedTable.COLUMN_STATE));
            else
                res = 0;
            c.close();
            return res;
        }
*/
    public List<Item> getFacebookFeeds(boolean latestOnly) {
        List<Item> feeds = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;
        if (latestOnly) {
            c = facebookQuery.query(db, null, FeedContract.FeedTable.COLUMN_TIME + ">= " +
                    "(CURRENT_TIMESTAMP - 86400000) " + "", null, null, null, FeedContract
                    .FeedTable.COLUMN_TIME + DbConstants.DESC);
        } else {
            c = facebookQuery.query(db, null, null, null, null, null,
                    FeedContract.FeedTable.COLUMN_TIME + DbConstants.DESC);
        }
        FacebookItem item;
        if (c.moveToFirst()) {
            do {
                item = new FacebookItem();
                item.setTime(c.getLong(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_TIME)));
                item.setLink(c.getString(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_LINK)));
                item.setDesc(c.getString(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_TEXT)));
                item.setTitle(c.getString(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_TITLE)));
                item.setImage_url(c.getString(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_IMAGE)));
                item.setId(c.getString(c.getColumnIndexOrThrow(FeedContract.FeedTable._ID)));
                item.setState(c.getShort(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_STATE)));
                item.setLikes(c.getInt(c.getColumnIndexOrThrow(FeedContract.FacebookFeedTable
                        .COLUMN_LIKES)));
                feeds.add(item);
            } while (c.moveToNext());
        }
        if (db != null)
            db.close();
        return feeds;
    }

    public void fillFacebookFeed(List<FacebookItem> feeds) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues feedValues, fbFeedValues;
        int fbfeeds = 0;
        long feedId = -1;
        db.beginTransaction();
        for (FacebookItem f : feeds) {
            feedValues = new ContentValues();
//            feedValues.put(FeedContract.FeedTable._ID, f.getId());
            feedValues.put(FeedContract.FeedTable.COLUMN_TITLE, f.getTitle());
            feedValues.put(FeedContract.FeedTable.COLUMN_TEXT, f.getDesc());
            feedValues.put(FeedContract.FeedTable.COLUMN_LINK, f.getLink());
            feedValues.put(FeedContract.FeedTable.COLUMN_IMAGE, f.getImage_url());
            feedValues.put(FeedContract.FeedTable.COLUMN_TIME, f.getTime());
            feedId = db.insertWithOnConflict(FeedContract.FeedTable.TABLE_NAME, null,
                    feedValues, SQLiteDatabase.CONFLICT_IGNORE);
            //skip current feed if inserting in feed table fails
            if (feedId == -1)
                continue;
            fbFeedValues = new ContentValues();
            fbFeedValues.put(FeedContract.FacebookFeedTable.COLUMN_FEED_ID, feedId);
            fbFeedValues.put(FeedContract.FacebookFeedTable.COLUMN_LIKES, f.getLikes());
            db.insertWithOnConflict(FeedContract.FacebookFeedTable.TABLE_NAME, null,
                    fbFeedValues, SQLiteDatabase.CONFLICT_IGNORE);
            fbfeeds++;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        if (db != null)
            db.close();
        Util.log(TAG, feeds.size() + " facebook feeds inserted" + fbfeeds);
    }

    public List<Item> getNewsFeeds(boolean latestOnly) {
        List<Item> feeds = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        if (latestOnly) {
            c = newsQuery.query(db, null, FeedContract.FeedTable.COLUMN_TIME + ">= " +
                    "(CURRENT_TIMESTAMP - 86400000) ", null, null, null, FeedContract.FeedTable
                    .COLUMN_TIME + DbConstants.DESC);
        } else {
            c = newsQuery.query(db, null, null, null, null, null,
                    FeedContract.FeedTable.COLUMN_TIME + DbConstants.DESC);
        }
        NewsItem item;
        if (c.moveToFirst()) {
            do {
                item = new NewsItem();
                item.setPublishedDate(c.getLong(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_TIME)));
                item.setUrl(c.getString(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_LINK)));
                item.setAbstractt(c.getString(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_TEXT)));
                item.setTitle(c.getString(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_TITLE)));
                List<MultimediaItem> multimediaList = new ArrayList<>();
                MultimediaItem multimediaItem = new MultimediaItem();
                multimediaItem.setUrl(c.getString(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_IMAGE)));
                multimediaItem.setFormat(MultimediaItem.TYPE.MEDUIM);
                multimediaList.add(multimediaItem);
                item.setMultimedia(multimediaList);
//                item.setId(c.getString(c.getColumnIndexOrThrow(FeedContract.FeedTable._ID)));
                item.setState(c.getShort(c.getColumnIndexOrThrow(FeedContract.FeedTable
                        .COLUMN_STATE)));
                item.setSubsection(c.getString(c.getColumnIndexOrThrow(FeedContract
                        .NewsFeedTable.COLUMN_PUBLISHER)));
                feeds.add(item);
            } while (c.moveToNext());
        }
        if (db != null)
            db.close();
        return feeds;
    }

    /**
     * insert list of NewsFeeds int database
     *
     * @param feeds
     */
    public void fillNewsFeed(List<NewsItem> feeds) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues feedValues, newsValues;
        long feedId = -1;
        int fbfeeds = 0;
        for (NewsItem f : feeds) {
            feedValues = new ContentValues();
//            feedValues.put(FeedContract.FeedTable._ID, f.getId());

            feedValues.put(FeedContract.FeedTable.COLUMN_TITLE, f.getTitle());
            feedValues.put(FeedContract.FeedTable.COLUMN_TEXT, f.getAbstractt());
            feedValues.put(FeedContract.FeedTable.COLUMN_TIME, f.getPublishedDate().getTime());
            feedValues.put(FeedContract.FeedTable.COLUMN_LINK, f.getUrl());
            if (f.getMultiMediaItem() != null)
                feedValues.put(FeedContract.FeedTable.COLUMN_IMAGE, f.getMultiMediaItem().getUrl());
            feedValues.put(FeedContract.FeedTable.COLUMN_STATE, f.getState());
            feedId = db.insertWithOnConflict(FeedContract.FeedTable.TABLE_NAME, null,
                    feedValues, SQLiteDatabase.CONFLICT_IGNORE);
            //skip this item as inserting in feed table failed
            if (feedId == -1)
                continue;
            newsValues = new ContentValues();
            newsValues.put(FeedContract.NewsFeedTable.COLUMN_FEED_ID, feedId);
            newsValues.put(FeedContract.NewsFeedTable.COLUMN_PUBLISHER, f.getSubsection());

            db.insertWithOnConflict(FeedContract.NewsFeedTable.TABLE_NAME, null,
                    newsValues, SQLiteDatabase.CONFLICT_IGNORE);
            fbfeeds++;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        Util.log(TAG, feeds.size() + " news feeds inserted" + fbfeeds);
        if (db != null)
            db.close();
    }

    public void markAllAs(int state, int type) throws InvalidStateException, InvalidTypeException {
        if (state != DbConstants.State.READ && state != DbConstants.State.UNREAD)
            throw new InvalidStateException();
        else if (type != DbConstants.Type.FB && type != DbConstants.Type.NEWS)
            throw new InvalidTypeException();
        else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FeedContract.FeedTable.COLUMN_STATE, state);
            db.update(FeedContract.FeedTable.TABLE_NAME, values, null, null);
            db.close();
        }
    }

    public List<Item> getTopUnread() {
        List<Item> unread = new ArrayList<>();
//        unread = getNewsFeeds(true);
        unread.addAll(getFacebookFeeds(true));
        ListIterator<Item> it = unread.listIterator();
        Item item;
        Util.log(TAG, "before\n" + unread.size());
        while (it.hasNext()) {
            item = it.next();
            if (item.getState() == DbConstants.State.READ)
                it.remove();
        }
        Util.log(TAG, "after\n" + unread.size());
        return unread;
    }

    public static class InvalidStateException extends Exception {
        @Override
        public String getMessage() {
            return "Invalid state";
        }
    }

    public static class InvalidTypeException extends Exception {
        @Override
        public String getMessage() {
            return "Invalid type";
        }
    }

}