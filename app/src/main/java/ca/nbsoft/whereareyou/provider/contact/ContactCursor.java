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
     * First name.
     * Can be {@code null}.
     */
    @Nullable
    public String getFirstName() {
        String res = getStringOrNull(ContactColumns.FIRST_NAME);
        return res;
    }

    /**
     * Last name.
     * Can be {@code null}.
     */
    @Nullable
    public String getLastName() {
        String res = getStringOrNull(ContactColumns.LAST_NAME);
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
    public String getAvatarUrl() {
        String res = getStringOrNull(ContactColumns.AVATAR_URL);
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
}
