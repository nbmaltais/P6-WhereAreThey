package ca.nbsoft.whereareyou.provider.contact;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ca.nbsoft.whereareyou.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code contact} table.
 */
public class ContactCursor extends AbstractCursor implements ContactModel {
    public ContactCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(ContactColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * User acount this contact is linked to
     * Cannot be {@code null}.
     */
    @NonNull
    public String getAccount() {
        String res = getStringOrNull(ContactColumns.ACCOUNT);
        if (res == null)
            throw new NullPointerException("The value of 'account' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Name.
     * Can be {@code null}.
     */
    @Nullable
    public String getName() {
        String res = getStringOrNull(ContactColumns.NAME);
        return res;
    }

    /**
     * Get the {@code email} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getEmail() {
        String res = getStringOrNull(ContactColumns.EMAIL);
        if (res == null)
            throw new NullPointerException("The value of 'email' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code userid} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getUserid() {
        String res = getStringOrNull(ContactColumns.USERID);
        if (res == null)
            throw new NullPointerException("The value of 'userid' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Avatar url
     * Can be {@code null}.
     */
    @Nullable
    public String getPhotoUrl() {
        String res = getStringOrNull(ContactColumns.PHOTO_URL);
        return res;
    }

    /**
     * Contact status
     */
    public int getStatus() {
        Integer res = getIntegerOrNull(ContactColumns.STATUS);
        if (res == null)
            throw new NullPointerException("The value of 'status' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code blocked} value.
     */
    public boolean getBlocked() {
        Boolean res = getBooleanOrNull(ContactColumns.BLOCKED);
        if (res == null)
            throw new NullPointerException("The value of 'blocked' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code auto_reply} value.
     */
    public boolean getAutoReply() {
        Boolean res = getBooleanOrNull(ContactColumns.AUTO_REPLY);
        if (res == null)
            throw new NullPointerException("The value of 'auto_reply' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code position_latitude} value.
     */
    public double getPositionLatitude() {
        Double res = getDoubleOrNull(ContactColumns.POSITION_LATITUDE);
        if (res == null)
            throw new NullPointerException("The value of 'position_latitude' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code position_longitude} value.
     */
    public double getPositionLongitude() {
        Double res = getDoubleOrNull(ContactColumns.POSITION_LONGITUDE);
        if (res == null)
            throw new NullPointerException("The value of 'position_longitude' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code position_timestamp} value.
     */
    public long getPositionTimestamp() {
        Long res = getLongOrNull(ContactColumns.POSITION_TIMESTAMP);
        if (res == null)
            throw new NullPointerException("The value of 'position_timestamp' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
