package ca.nbsoft.whereareyou.provider.message;

import android.net.Uri;
import android.provider.BaseColumns;

import ca.nbsoft.whereareyou.provider.WhereRUProvider;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;

/**
 * Columns for the {@code message} table.
 */
public class MessageColumns implements BaseColumns {
    public static final String TABLE_NAME = "message";
    public static final Uri CONTENT_URI = Uri.parse(WhereRUProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String CONTACT_ID = "contact_id";

    public static final String CONTENT = "content";

    public static final String USERISSENDER = "userIsSender";

    public static final String TIMESTAMP = "timestamp";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            CONTACT_ID,
            CONTENT,
            USERISSENDER,
            TIMESTAMP
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(CONTACT_ID) || c.contains("." + CONTACT_ID)) return true;
            if (c.equals(CONTENT) || c.contains("." + CONTENT)) return true;
            if (c.equals(USERISSENDER) || c.contains("." + USERISSENDER)) return true;
            if (c.equals(TIMESTAMP) || c.contains("." + TIMESTAMP)) return true;
        }
        return false;
    }

    public static final String PREFIX_CONTACT = TABLE_NAME + "__" + ContactColumns.TABLE_NAME;
}
