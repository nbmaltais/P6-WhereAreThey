package ca.nbsoft.whereareyou.provider.contact;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ca.nbsoft.whereareyou.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code contact} table.
 */
public class ContactContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return ContactColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable ContactSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable ContactSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * First name.
     */
    public ContactContentValues putFirstName(@Nullable String value) {
        mContentValues.put(ContactColumns.FIRST_NAME, value);
        return this;
    }

    public ContactContentValues putFirstNameNull() {
        mContentValues.putNull(ContactColumns.FIRST_NAME);
        return this;
    }

    /**
     * Last name.
     */
    public ContactContentValues putLastName(@Nullable String value) {
        mContentValues.put(ContactColumns.LAST_NAME, value);
        return this;
    }

    public ContactContentValues putLastNameNull() {
        mContentValues.putNull(ContactColumns.LAST_NAME);
        return this;
    }

    public ContactContentValues putEmail(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("email must not be null");
        mContentValues.put(ContactColumns.EMAIL, value);
        return this;
    }


    public ContactContentValues putUserid(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("userid must not be null");
        mContentValues.put(ContactColumns.USERID, value);
        return this;
    }


    /**
     * Avatar url
     */
    public ContactContentValues putAvatarUrl(@Nullable String value) {
        mContentValues.put(ContactColumns.AVATAR_URL, value);
        return this;
    }

    public ContactContentValues putAvatarUrlNull() {
        mContentValues.putNull(ContactColumns.AVATAR_URL);
        return this;
    }

    public ContactContentValues putBlocked(boolean value) {
        mContentValues.put(ContactColumns.BLOCKED, value);
        return this;
    }


    public ContactContentValues putAutoReply(boolean value) {
        mContentValues.put(ContactColumns.AUTO_REPLY, value);
        return this;
    }

}
