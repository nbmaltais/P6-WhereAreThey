package ca.nbsoft.whereareyou.provider.contact;

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
     * User acount this contact is linked to
     */
    public ContactContentValues putAccount(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("account must not be null");
        mContentValues.put(ContactColumns.ACCOUNT, value);
        return this;
    }


    /**
     * Name.
     */
    public ContactContentValues putName(@Nullable String value) {
        mContentValues.put(ContactColumns.NAME, value);
        return this;
    }

    public ContactContentValues putNameNull() {
        mContentValues.putNull(ContactColumns.NAME);
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
    public ContactContentValues putPhotoUrl(@Nullable String value) {
        mContentValues.put(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public ContactContentValues putPhotoUrlNull() {
        mContentValues.putNull(ContactColumns.PHOTO_URL);
        return this;
    }

    /**
     * Contact status
     */
    public ContactContentValues putStatus(int value) {
        mContentValues.put(ContactColumns.STATUS, value);
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


    public ContactContentValues putPositionLatitude(double value) {
        mContentValues.put(ContactColumns.POSITION_LATITUDE, value);
        return this;
    }


    public ContactContentValues putPositionLongitude(double value) {
        mContentValues.put(ContactColumns.POSITION_LONGITUDE, value);
        return this;
    }


    public ContactContentValues putPositionTimestamp(long value) {
        mContentValues.put(ContactColumns.POSITION_TIMESTAMP, value);
        return this;
    }

}
