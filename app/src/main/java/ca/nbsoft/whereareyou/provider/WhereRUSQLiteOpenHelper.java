package ca.nbsoft.whereareyou.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import ca.nbsoft.whereareyou.BuildConfig;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.message.MessageColumns;

public class WhereRUSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = WhereRUSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "wru.db";
    private static final int DATABASE_VERSION = 1;
    private static WhereRUSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final WhereRUSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_CONTACT = "CREATE TABLE IF NOT EXISTS "
            + ContactColumns.TABLE_NAME + " ( "
            + ContactColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ContactColumns.ACCOUNT + " TEXT NOT NULL, "
            + ContactColumns.NAME + " TEXT, "
            + ContactColumns.EMAIL + " TEXT NOT NULL, "
            + ContactColumns.USERID + " TEXT NOT NULL, "
            + ContactColumns.PHOTO_URL + " TEXT, "
            + ContactColumns.STATUS + " INTEGER NOT NULL DEFAULT 0, "
            + ContactColumns.BLOCKED + " INTEGER NOT NULL DEFAULT 0, "
            + ContactColumns.AUTO_REPLY + " INTEGER NOT NULL DEFAULT 0, "
            + ContactColumns.POSITION_LATITUDE + " REAL NOT NULL DEFAULT 0, "
            + ContactColumns.POSITION_LONGITUDE + " REAL NOT NULL DEFAULT 0, "
            + ContactColumns.POSITION_TIMESTAMP + " INTEGER NOT NULL DEFAULT 0 "
            + ", CONSTRAINT unique_email UNIQUE (email) ON CONFLICT REPLACE"
            + ", CONSTRAINT unique_usrerId UNIQUE (userId) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_CONTACT_ACCOUNT = "CREATE INDEX IDX_CONTACT_ACCOUNT "
            + " ON " + ContactColumns.TABLE_NAME + " ( " + ContactColumns.ACCOUNT + " );";

    public static final String SQL_CREATE_INDEX_CONTACT_USERID = "CREATE INDEX IDX_CONTACT_USERID "
            + " ON " + ContactColumns.TABLE_NAME + " ( " + ContactColumns.USERID + " );";

    public static final String SQL_CREATE_TABLE_MESSAGE = "CREATE TABLE IF NOT EXISTS "
            + MessageColumns.TABLE_NAME + " ( "
            + MessageColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MessageColumns.CONTACT_ID + " INTEGER NOT NULL, "
            + MessageColumns.CONTENT + " TEXT NOT NULL, "
            + MessageColumns.USERISSENDER + " INTEGER NOT NULL, "
            + MessageColumns.TIMESTAMP + " INTEGER NOT NULL "
            + ", CONSTRAINT fk_contact_id FOREIGN KEY (" + MessageColumns.CONTACT_ID + ") REFERENCES contact (_id) ON DELETE CASCADE"
            + " );";

    public static final String SQL_CREATE_INDEX_MESSAGE_CONTACT_ID = "CREATE INDEX IDX_MESSAGE_CONTACT_ID "
            + " ON " + MessageColumns.TABLE_NAME + " ( " + MessageColumns.CONTACT_ID + " );";

    // @formatter:on

    public static WhereRUSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static WhereRUSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static WhereRUSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new WhereRUSQLiteOpenHelper(context);
    }

    private WhereRUSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new WhereRUSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static WhereRUSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new WhereRUSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private WhereRUSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new WhereRUSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_CONTACT);
        db.execSQL(SQL_CREATE_INDEX_CONTACT_ACCOUNT);
        db.execSQL(SQL_CREATE_INDEX_CONTACT_USERID);
        db.execSQL(SQL_CREATE_TABLE_MESSAGE);
        db.execSQL(SQL_CREATE_INDEX_MESSAGE_CONTACT_ID);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
