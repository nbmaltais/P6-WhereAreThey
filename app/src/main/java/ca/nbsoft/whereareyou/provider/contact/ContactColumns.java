package ca.nbsoft.whereareyou.provider.contact;

import android.net.Uri;
import android.provider.BaseColumns;

import ca.nbsoft.whereareyou.provider.WhereRUProvider;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;

/**
 * A contact.
 */
public class ContactColumns implements BaseColumns {
    public static final String TABLE_NAME = "contact";
    public static final Uri CONTENT_URI = Uri.parse(WhereRUProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * First name.
     */
    public static final String FIRST_NAME = "first_name";

    /**
     * Last name.
     */
    public static final String LAST_NAME = "last_name";

    public static final String EMAIL = "email";

    public static final String USERID = "userId";

    /**
     * Avatar url
     */
    public static final String AVATAR_URL = "avatar_url";

    public static final String BLOCKED = "blocked";

    public static final String AUTO_REPLY = "auto_reply";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            FIRST_NAME,
            LAST_NAME,
            EMAIL,
            USERID,
            AVATAR_URL,
            BLOCKED,
            AUTO_REPLY
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(FIRST_NAME) || c.contains("." + FIRST_NAME)) return true;
            if (c.equals(LAST_NAME) || c.contains("." + LAST_NAME)) return true;
            if (c.equals(EMAIL) || c.contains("." + EMAIL)) return true;
            if (c.equals(USERID) || c.contains("." + USERID)) return true;
            if (c.equals(AVATAR_URL) || c.contains("." + AVATAR_URL)) return true;
            if (c.equals(BLOCKED) || c.contains("." + BLOCKED)) return true;
            if (c.equals(AUTO_REPLY) || c.contains("." + AUTO_REPLY)) return true;
        }
        return false;
    }

}
