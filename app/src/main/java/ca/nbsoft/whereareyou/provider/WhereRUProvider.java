package ca.nbsoft.whereareyou.provider;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import ca.nbsoft.whereareyou.BuildConfig;
import ca.nbsoft.whereareyou.provider.base.BaseContentProvider;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.message.MessageColumns;

public class WhereRUProvider extends BaseContentProvider {
    private static final String TAG = WhereRUProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "ca.nbsoft.whereareyou.provider";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_CONTACT = 0;
    private static final int URI_TYPE_CONTACT_ID = 1;

    private static final int URI_TYPE_MESSAGE = 2;
    private static final int URI_TYPE_MESSAGE_ID = 3;



    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, ContactColumns.TABLE_NAME, URI_TYPE_CONTACT);
        URI_MATCHER.addURI(AUTHORITY, ContactColumns.TABLE_NAME + "/#", URI_TYPE_CONTACT_ID);
        URI_MATCHER.addURI(AUTHORITY, MessageColumns.TABLE_NAME, URI_TYPE_MESSAGE);
        URI_MATCHER.addURI(AUTHORITY, MessageColumns.TABLE_NAME + "/#", URI_TYPE_MESSAGE_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return WhereRUSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_CONTACT:
                return TYPE_CURSOR_DIR + ContactColumns.TABLE_NAME;
            case URI_TYPE_CONTACT_ID:
                return TYPE_CURSOR_ITEM + ContactColumns.TABLE_NAME;

            case URI_TYPE_MESSAGE:
                return TYPE_CURSOR_DIR + MessageColumns.TABLE_NAME;
            case URI_TYPE_MESSAGE_ID:
                return TYPE_CURSOR_ITEM + MessageColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_CONTACT:
            case URI_TYPE_CONTACT_ID:
                res.table = ContactColumns.TABLE_NAME;
                res.idColumn = ContactColumns._ID;
                res.tablesWithJoins = ContactColumns.TABLE_NAME;
                res.orderBy = ContactColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_MESSAGE:
            case URI_TYPE_MESSAGE_ID:
                res.table = MessageColumns.TABLE_NAME;
                res.idColumn = MessageColumns._ID;
                res.tablesWithJoins = MessageColumns.TABLE_NAME;
                if (ContactColumns.hasColumns(projection)) {
                    res.tablesWithJoins += " LEFT OUTER JOIN " + ContactColumns.TABLE_NAME + " AS " + MessageColumns.PREFIX_CONTACT + " ON " + MessageColumns.TABLE_NAME + "." + MessageColumns.CONTACT_ID + "=" + MessageColumns.PREFIX_CONTACT + "." + ContactColumns._ID;
                }
                res.orderBy = MessageColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_CONTACT_ID:
            case URI_TYPE_MESSAGE_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
