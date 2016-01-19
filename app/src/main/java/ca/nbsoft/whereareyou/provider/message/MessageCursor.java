package ca.nbsoft.whereareyou.provider.message;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ca.nbsoft.whereareyou.provider.base.AbstractCursor;
import ca.nbsoft.whereareyou.provider.contact.*;

/**
 * Cursor wrapper for the {@code message} table.
 */
public class MessageCursor extends AbstractCursor implements MessageModel {
    public MessageCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(MessageColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code contact_id} value.
     */
    public long getContactId() {
        Long res = getLongOrNull(MessageColumns.CONTACT_ID);
        if (res == null)
            throw new NullPointerException("The value of 'contact_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * User acount this contact is linked to
     * Cannot be {@code null}.
     */
    @NonNull
    public String getContactAccount() {
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
    public String getContactName() {
        String res = getStringOrNull(ContactColumns.NAME);
        return res;
    }

    /**
     * Get the {@code email} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getContactEmail() {
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
    public String getContactUserid() {
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
    public String getContactPhotoUrl() {
        String res = getStringOrNull(ContactColumns.PHOTO_URL);
        return res;
    }

    /**
     * Contact status
     */
    public int getContactStatus() {
        Integer res = getIntegerOrNull(ContactColumns.STATUS);
        if (res == null)
            throw new NullPointerException("The value of 'status' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code blocked} value.
     */
    public boolean getContactBlocked() {
        Boolean res = getBooleanOrNull(ContactColumns.BLOCKED);
        if (res == null)
            throw new NullPointerException("The value of 'blocked' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code auto_reply} value.
     */
    public boolean getContactAutoReply() {
        Boolean res = getBooleanOrNull(ContactColumns.AUTO_REPLY);
        if (res == null)
            throw new NullPointerException("The value of 'auto_reply' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code position_latitude} value.
     */
    public double getContactPositionLatitude() {
        Double res = getDoubleOrNull(ContactColumns.POSITION_LATITUDE);
        if (res == null)
            throw new NullPointerException("The value of 'position_latitude' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code position_longitude} value.
     */
    public double getContactPositionLongitude() {
        Double res = getDoubleOrNull(ContactColumns.POSITION_LONGITUDE);
        if (res == null)
            throw new NullPointerException("The value of 'position_longitude' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code position_timestamp} value.
     */
    public long getContactPositionTimestamp() {
        Long res = getLongOrNull(ContactColumns.POSITION_TIMESTAMP);
        if (res == null)
            throw new NullPointerException("The value of 'position_timestamp' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code content} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getContent() {
        String res = getStringOrNull(MessageColumns.CONTENT);
        if (res == null)
            throw new NullPointerException("The value of 'content' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code userissender} value.
     */
    public boolean getUserissender() {
        Boolean res = getBooleanOrNull(MessageColumns.USERISSENDER);
        if (res == null)
            throw new NullPointerException("The value of 'userissender' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code timestamp} value.
     */
    public long getTimestamp() {
        Long res = getLongOrNull(MessageColumns.TIMESTAMP);
        if (res == null)
            throw new NullPointerException("The value of 'timestamp' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
