package belaevstanislav.feedagregator.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import belaevstanislav.feedagregator.feeditem.core.FeedItemCore;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.util.Constant;

public class FeedItemDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = Constant.DATABASE_NAME;
    private static final int DATABASE_VERSION = Constant.DATABASE_VERSION;
    private static final String COMMON_INFORMATION_TABLE_NAME = Constant.COMMON_INFORMATION_TABLE_NAME;
    private static final String FEED_ITEM_BYTECODE_TABLE_NAME = Constant.FEED_ITEM_BYTECODE_TABLE_NAME;
    private static final String KEY_TABLE_ID = Constant.KEY_TABLE_ID;
    private static final String KEY_TABLE_TIME = Constant.KEY_TABLE_TIME;
    private static final String KEY_TABLE_SOURCE = Constant.KEY_TABLE_SOURCE;
    private static final String KEY_TABLE_BYTECODE = Constant.KEY_TABLE_BYTECODE;

    public FeedItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String CREATE_TABLE_1 = "CREATE TABLE " + COMMON_INFORMATION_TABLE_NAME + " ("
                + KEY_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
                + KEY_TABLE_SOURCE + " TEXT, "
                + KEY_TABLE_TIME + " INTEGER)";
        database.execSQL(CREATE_TABLE_1);
        String CREATE_TABLE_2 = "CREATE TABLE " + FEED_ITEM_BYTECODE_TABLE_NAME + " ("
                + KEY_TABLE_ID + " INTEGER PRIMARY KEY UNIQUE, "
                + KEY_TABLE_BYTECODE + " BLOB)";
        database.execSQL(CREATE_TABLE_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + COMMON_INFORMATION_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + FEED_ITEM_BYTECODE_TABLE_NAME);
            onCreate(database);
        }
    }

    public long insertCore(FeedItemCore core) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TABLE_SOURCE, core.getType().toString());
        contentValues.put(KEY_TABLE_TIME, core.getDate());
        return database.insert(COMMON_INFORMATION_TABLE_NAME, null, contentValues);
    }

    public long insertFeedItem(FeedItem feedItem) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TABLE_ID, feedItem.getId());
        contentValues.put(KEY_TABLE_BYTECODE, feedItem.serialize());
        return database.insert(FEED_ITEM_BYTECODE_TABLE_NAME, null, contentValues);
    }

    public Cursor getAll() {
        SQLiteDatabase database = getWritableDatabase();
        /*String queryBoth = "SELECT a." + KEY_TABLE_ID + ", a." + KEY_TABLE_SOURCE + ", b." + KEY_TABLE_BYTECODE
                + " FROM " + COMMON_INFORMATION_TABLE_NAME + " a INNER JOIN " + FEED_ITEM_BYTECODE_TABLE_NAME
                + " b ON a." + KEY_TABLE_ID + " = b." + KEY_TABLE_ID + " ORDER BY "+ KEY_TABLE_TIME +" DESC";*/
        String query = "SELECT " + KEY_TABLE_ID + " FROM "
                + COMMON_INFORMATION_TABLE_NAME + " ORDER BY " + KEY_TABLE_TIME + " DESC";
        return database.rawQuery(query, null);
    }

    public void delete(long id) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(FEED_ITEM_BYTECODE_TABLE_NAME, KEY_TABLE_ID + " = ?", new String[]{String.valueOf(id)});
        database.delete(COMMON_INFORMATION_TABLE_NAME, KEY_TABLE_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteAll() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(COMMON_INFORMATION_TABLE_NAME, "1", null);
        database.delete(FEED_ITEM_BYTECODE_TABLE_NAME, "1", null);
    }
}
