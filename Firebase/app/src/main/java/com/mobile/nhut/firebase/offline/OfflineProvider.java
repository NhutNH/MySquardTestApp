package com.mobile.nhut.firebase.offline;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.util.HashMap;

public class OfflineProvider extends ContentProvider {

  private static final String PROVIDER_NAME = "com.mobile.nhut.firebase.offline.OfflineProvider";

  private static final String QUERY_URI = "chatrooms";
  private static final String URL = "content://" + PROVIDER_NAME + "/" + QUERY_URI;

  public static final Uri CONTENT_URI = Uri.parse(URL);

  public static final String ID = "_id";

  public static final String GROUP_NAME = "GROUP_NAME";

  public static final String ID_MESSAGE = "ID_MESSAGE";

  public static final String MESSAGE = "MESSAGE";

  public static final String AUTHOR = "AUTHOR";

  public static final String STATE_OFFLINE = "STATE_OFFLINE";

  private static HashMap<String, String> STUDENTS_PROJECTION_MAP;

  private static final int CHATROOMS_QUERY = 1;

  private static final int CHATROOM_ID_QUERY = 2;

  private static final int MESSAGE_ID_QUERY = 3;

  private static final UriMatcher uriMatcher;

  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(PROVIDER_NAME, QUERY_URI, CHATROOMS_QUERY);
    uriMatcher.addURI(PROVIDER_NAME, QUERY_URI + "/#", CHATROOM_ID_QUERY);
    uriMatcher.addURI(PROVIDER_NAME, QUERY_URI + "/#", MESSAGE_ID_QUERY);
  }

  /**
   * Database specific constant declarations
   */
  private SQLiteDatabase db;

  static final String DATABASE_NAME = "firebase.db";

  static final String CHATROOM_TABLE_NAME = "chatroom_tbl";

  static final int DATABASE_VERSION = 1;

  static final String CREATE_DB_TABLE =
          " CREATE TABLE " + CHATROOM_TABLE_NAME + " (" +
                  ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                  GROUP_NAME + " TEXT NOT NULL, " +
                  AUTHOR + " TEXT NOT NULL," +
                  ID_MESSAGE + " TEXT," +
                  MESSAGE + " TEXT NOT NULL," +
                  STATE_OFFLINE + " INTEGER NOT NULL);";

  /**
   * Helper class that actually creates and manages
   * the provider's underlying data repository.
   */
  private static class DatabaseHelper extends SQLiteOpenHelper {

    DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(CREATE_DB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + CHATROOM_TABLE_NAME);
      onCreate(db);
    }
  }

  @Override
  public boolean onCreate() {
    Context context = getContext();
    DatabaseHelper dbHelper = new DatabaseHelper(context);

    /**
     * Create a write able database which will trigger its
     * creation if it doesn't already exist.
     */
    db = dbHelper.getWritableDatabase();
    return (db == null) ? false : true;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    /**
     * Add a new student record
     */
    long rowID = db.insert(CHATROOM_TABLE_NAME, "", values);

    /**
     * If record is added successfully
     */

    if (rowID > 0) {
      Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
      getContext().getContentResolver().notifyChange(_uri, null);
      return _uri;
    }
    throw new SQLException("Failed to add a record into " + uri);
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    qb.setTables(CHATROOM_TABLE_NAME);

    switch (uriMatcher.match(uri)) {
      case CHATROOMS_QUERY:
        qb.setProjectionMap(STUDENTS_PROJECTION_MAP);
        break;

      case CHATROOM_ID_QUERY:
        qb.appendWhere(ID + "=" + uri.getPathSegments().get(1));
        break;

      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    if (sortOrder == null || sortOrder == "") {
      /**
       * By default sort on student names
       */
      sortOrder = ID;
    }
    Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

    /**
     * register to watch a content URI for changes
     */
    c.setNotificationUri(getContext().getContentResolver(), uri);
    return c;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    int count = 0;

    switch (uriMatcher.match(uri)) {
      case CHATROOMS_QUERY:
        count = db.delete(CHATROOM_TABLE_NAME, selection, selectionArgs);
        break;

      case CHATROOM_ID_QUERY:
        String id = uri.getPathSegments().get(1);
        count = db.delete(CHATROOM_TABLE_NAME, ID + " = " + id +
            (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
        break;

      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    int count = 0;

    switch (uriMatcher.match(uri)) {
      case CHATROOMS_QUERY:
        count = db.update(CHATROOM_TABLE_NAME, values, selection, selectionArgs);
        break;

      case CHATROOM_ID_QUERY:
        count = db.update(CHATROOM_TABLE_NAME, values, ID + " = " + uri.getPathSegments().get(1) +
            (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
        break;

      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

  @Override
  public String getType(Uri uri) {
    switch (uriMatcher.match(uri)) {
      /**
       * Get all student records
       */
      case CHATROOMS_QUERY:
        return ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE;

      /**
       * Get a particular student
       */
      case CHATROOM_ID_QUERY:
        return ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;

      default:
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }
  }
}