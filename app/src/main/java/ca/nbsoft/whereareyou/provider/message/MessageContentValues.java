package ca.nbsoft.whereareyou.provider.message;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ca.nbsoft.whereareyou.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code message} table.
 */
public class MessageContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return MessageColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable MessageSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable MessageSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public MessageContentValues putContactId(long value) {
        mContentValues.put(MessageColumns.CONTACT_ID, value);
        return this;
    }


    public MessageContentValues putContent(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("content must not be null");
        mContentValues.put(MessageColumns.CONTENT, value);
        return this;
    }


    public MessageContentValues putUserissender(boolean value) {
        mContentValues.put(MessageColumns.USERISSENDER, value);
        return this;
    }


    public MessageContentValues putTimestamp(long value) {
        mContentValues.put(MessageColumns.TIMESTAMP, value);
        return this;
    }

}
