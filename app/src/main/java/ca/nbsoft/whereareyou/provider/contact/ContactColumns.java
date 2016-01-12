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
     * User acount this contact is linked to
     */
    public static final String ACCOUNT = "account";

    /**
     * Name.
     */
    public static final String NAME = "name";

    public static final String EMAIL = "email";

    public static final String USERID = "userId";

    /**
     * Avatar url
     */
    public static final String PHOTO_URL = "photo_url";

    public static final String BLOCKED = "blocked";

    public static final String AUTO_REPLY = "auto_reply";

    public static final String POSITION_LATITUDE = "position_latitude";

    public static final String POSITION_LONGITUDE = "position_longitude";

    public static final String POSITION_TIMSTAMP = "position_timstamp";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            ACCOUNT,
            NAME,
            EMAIL,
            USERID,
            PHOTO_URL,
            BLOCKED,
            AUTO_REPLY,
            POSITION_LATITUDE,
            POSITION_LONGITUDE,
            POSITION_TIMSTAMP
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(ACCOUNT) || c.contains("." + ACCOUNT)) return true;
            if (c.equals(NAME) || c.contains("." + NAME)) return true;
            if (c.equals(EMAIL) || c.contains("." + EMAIL)) return true;
            if (c.equals(USERID) || c.contains("." + USERID)) return true;
            if (c.equals(PHOTO_URL) || c.contains("." + PHOTO_URL)) return true;
            if (c.equals(BLOCKED) || c.contains("." + BLOCKED)) return true;
            if (c.equals(AUTO_REPLY) || c.contains("." + AUTO_REPLY)) return true;
            if (c.equals(POSITION_LATITUDE) || c.contains("." + POSITION_LATITUDE)) return true;
            if (c.equals(POSITION_LONGITUDE) || c.contains("." + POSITION_LONGITUDE)) return true;
            if (c.equals(POSITION_TIMSTAMP) || c.contains("." + POSITION_TIMSTAMP)) return true;
        }
        return false;
    }

}
