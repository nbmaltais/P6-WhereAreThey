package ca.nbsoft.whereareyou.provider.message;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import ca.nbsoft.whereareyou.provider.base.AbstractSelection;
import ca.nbsoft.whereareyou.provider.contact.*;

/**
 * Selection for the {@code message} table.
 */
public class MessageSelection extends AbstractSelection<MessageSelection> {
    @Override
    protected Uri baseUri() {
        return MessageColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code MessageCursor} object, which is positioned before the first entry, or null.
     */
    public MessageCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new MessageCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public MessageCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code MessageCursor} object, which is positioned before the first entry, or null.
     */
    public MessageCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new MessageCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public MessageCursor query(Context context) {
        return query(context, null);
    }


    public MessageSelection id(long... value) {
        addEquals("message." + MessageColumns._ID, toObjectArray(value));
        return this;
    }

    public MessageSelection idNot(long... value) {
        addNotEquals("message." + MessageColumns._ID, toObjectArray(value));
        return this;
    }

    public MessageSelection orderById(boolean desc) {
        orderBy("message." + MessageColumns._ID, desc);
        return this;
    }

    public MessageSelection orderById() {
        return orderById(false);
    }

    public MessageSelection contactId(long... value) {
        addEquals(MessageColumns.CONTACT_ID, toObjectArray(value));
        return this;
    }

    public MessageSelection contactIdNot(long... value) {
        addNotEquals(MessageColumns.CONTACT_ID, toObjectArray(value));
        return this;
    }

    public MessageSelection contactIdGt(long value) {
        addGreaterThan(MessageColumns.CONTACT_ID, value);
        return this;
    }

    public MessageSelection contactIdGtEq(long value) {
        addGreaterThanOrEquals(MessageColumns.CONTACT_ID, value);
        return this;
    }

    public MessageSelection contactIdLt(long value) {
        addLessThan(MessageColumns.CONTACT_ID, value);
        return this;
    }

    public MessageSelection contactIdLtEq(long value) {
        addLessThanOrEquals(MessageColumns.CONTACT_ID, value);
        return this;
    }

    public MessageSelection orderByContactId(boolean desc) {
        orderBy(MessageColumns.CONTACT_ID, desc);
        return this;
    }

    public MessageSelection orderByContactId() {
        orderBy(MessageColumns.CONTACT_ID, false);
        return this;
    }

    public MessageSelection contactAccount(String... value) {
        addEquals(ContactColumns.ACCOUNT, value);
        return this;
    }

    public MessageSelection contactAccountNot(String... value) {
        addNotEquals(ContactColumns.ACCOUNT, value);
        return this;
    }

    public MessageSelection contactAccountLike(String... value) {
        addLike(ContactColumns.ACCOUNT, value);
        return this;
    }

    public MessageSelection contactAccountContains(String... value) {
        addContains(ContactColumns.ACCOUNT, value);
        return this;
    }

    public MessageSelection contactAccountStartsWith(String... value) {
        addStartsWith(ContactColumns.ACCOUNT, value);
        return this;
    }

    public MessageSelection contactAccountEndsWith(String... value) {
        addEndsWith(ContactColumns.ACCOUNT, value);
        return this;
    }

    public MessageSelection orderByContactAccount(boolean desc) {
        orderBy(ContactColumns.ACCOUNT, desc);
        return this;
    }

    public MessageSelection orderByContactAccount() {
        orderBy(ContactColumns.ACCOUNT, false);
        return this;
    }

    public MessageSelection contactName(String... value) {
        addEquals(ContactColumns.NAME, value);
        return this;
    }

    public MessageSelection contactNameNot(String... value) {
        addNotEquals(ContactColumns.NAME, value);
        return this;
    }

    public MessageSelection contactNameLike(String... value) {
        addLike(ContactColumns.NAME, value);
        return this;
    }

    public MessageSelection contactNameContains(String... value) {
        addContains(ContactColumns.NAME, value);
        return this;
    }

    public MessageSelection contactNameStartsWith(String... value) {
        addStartsWith(ContactColumns.NAME, value);
        return this;
    }

    public MessageSelection contactNameEndsWith(String... value) {
        addEndsWith(ContactColumns.NAME, value);
        return this;
    }

    public MessageSelection orderByContactName(boolean desc) {
        orderBy(ContactColumns.NAME, desc);
        return this;
    }

    public MessageSelection orderByContactName() {
        orderBy(ContactColumns.NAME, false);
        return this;
    }

    public MessageSelection contactEmail(String... value) {
        addEquals(ContactColumns.EMAIL, value);
        return this;
    }

    public MessageSelection contactEmailNot(String... value) {
        addNotEquals(ContactColumns.EMAIL, value);
        return this;
    }

    public MessageSelection contactEmailLike(String... value) {
        addLike(ContactColumns.EMAIL, value);
        return this;
    }

    public MessageSelection contactEmailContains(String... value) {
        addContains(ContactColumns.EMAIL, value);
        return this;
    }

    public MessageSelection contactEmailStartsWith(String... value) {
        addStartsWith(ContactColumns.EMAIL, value);
        return this;
    }

    public MessageSelection contactEmailEndsWith(String... value) {
        addEndsWith(ContactColumns.EMAIL, value);
        return this;
    }

    public MessageSelection orderByContactEmail(boolean desc) {
        orderBy(ContactColumns.EMAIL, desc);
        return this;
    }

    public MessageSelection orderByContactEmail() {
        orderBy(ContactColumns.EMAIL, false);
        return this;
    }

    public MessageSelection contactUserid(String... value) {
        addEquals(ContactColumns.USERID, value);
        return this;
    }

    public MessageSelection contactUseridNot(String... value) {
        addNotEquals(ContactColumns.USERID, value);
        return this;
    }

    public MessageSelection contactUseridLike(String... value) {
        addLike(ContactColumns.USERID, value);
        return this;
    }

    public MessageSelection contactUseridContains(String... value) {
        addContains(ContactColumns.USERID, value);
        return this;
    }

    public MessageSelection contactUseridStartsWith(String... value) {
        addStartsWith(ContactColumns.USERID, value);
        return this;
    }

    public MessageSelection contactUseridEndsWith(String... value) {
        addEndsWith(ContactColumns.USERID, value);
        return this;
    }

    public MessageSelection orderByContactUserid(boolean desc) {
        orderBy(ContactColumns.USERID, desc);
        return this;
    }

    public MessageSelection orderByContactUserid() {
        orderBy(ContactColumns.USERID, false);
        return this;
    }

    public MessageSelection contactPhotoUrl(String... value) {
        addEquals(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public MessageSelection contactPhotoUrlNot(String... value) {
        addNotEquals(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public MessageSelection contactPhotoUrlLike(String... value) {
        addLike(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public MessageSelection contactPhotoUrlContains(String... value) {
        addContains(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public MessageSelection contactPhotoUrlStartsWith(String... value) {
        addStartsWith(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public MessageSelection contactPhotoUrlEndsWith(String... value) {
        addEndsWith(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public MessageSelection orderByContactPhotoUrl(boolean desc) {
        orderBy(ContactColumns.PHOTO_URL, desc);
        return this;
    }

    public MessageSelection orderByContactPhotoUrl() {
        orderBy(ContactColumns.PHOTO_URL, false);
        return this;
    }

    public MessageSelection contactBlocked(boolean value) {
        addEquals(ContactColumns.BLOCKED, toObjectArray(value));
        return this;
    }

    public MessageSelection orderByContactBlocked(boolean desc) {
        orderBy(ContactColumns.BLOCKED, desc);
        return this;
    }

    public MessageSelection orderByContactBlocked() {
        orderBy(ContactColumns.BLOCKED, false);
        return this;
    }

    public MessageSelection contactAutoReply(boolean value) {
        addEquals(ContactColumns.AUTO_REPLY, toObjectArray(value));
        return this;
    }

    public MessageSelection orderByContactAutoReply(boolean desc) {
        orderBy(ContactColumns.AUTO_REPLY, desc);
        return this;
    }

    public MessageSelection orderByContactAutoReply() {
        orderBy(ContactColumns.AUTO_REPLY, false);
        return this;
    }

    public MessageSelection contactPositionLatitude(double... value) {
        addEquals(ContactColumns.POSITION_LATITUDE, toObjectArray(value));
        return this;
    }

    public MessageSelection contactPositionLatitudeNot(double... value) {
        addNotEquals(ContactColumns.POSITION_LATITUDE, toObjectArray(value));
        return this;
    }

    public MessageSelection contactPositionLatitudeGt(double value) {
        addGreaterThan(ContactColumns.POSITION_LATITUDE, value);
        return this;
    }

    public MessageSelection contactPositionLatitudeGtEq(double value) {
        addGreaterThanOrEquals(ContactColumns.POSITION_LATITUDE, value);
        return this;
    }

    public MessageSelection contactPositionLatitudeLt(double value) {
        addLessThan(ContactColumns.POSITION_LATITUDE, value);
        return this;
    }

    public MessageSelection contactPositionLatitudeLtEq(double value) {
        addLessThanOrEquals(ContactColumns.POSITION_LATITUDE, value);
        return this;
    }

    public MessageSelection orderByContactPositionLatitude(boolean desc) {
        orderBy(ContactColumns.POSITION_LATITUDE, desc);
        return this;
    }

    public MessageSelection orderByContactPositionLatitude() {
        orderBy(ContactColumns.POSITION_LATITUDE, false);
        return this;
    }

    public MessageSelection contactPositionLongitude(double... value) {
        addEquals(ContactColumns.POSITION_LONGITUDE, toObjectArray(value));
        return this;
    }

    public MessageSelection contactPositionLongitudeNot(double... value) {
        addNotEquals(ContactColumns.POSITION_LONGITUDE, toObjectArray(value));
        return this;
    }

    public MessageSelection contactPositionLongitudeGt(double value) {
        addGreaterThan(ContactColumns.POSITION_LONGITUDE, value);
        return this;
    }

    public MessageSelection contactPositionLongitudeGtEq(double value) {
        addGreaterThanOrEquals(ContactColumns.POSITION_LONGITUDE, value);
        return this;
    }

    public MessageSelection contactPositionLongitudeLt(double value) {
        addLessThan(ContactColumns.POSITION_LONGITUDE, value);
        return this;
    }

    public MessageSelection contactPositionLongitudeLtEq(double value) {
        addLessThanOrEquals(ContactColumns.POSITION_LONGITUDE, value);
        return this;
    }

    public MessageSelection orderByContactPositionLongitude(boolean desc) {
        orderBy(ContactColumns.POSITION_LONGITUDE, desc);
        return this;
    }

    public MessageSelection orderByContactPositionLongitude() {
        orderBy(ContactColumns.POSITION_LONGITUDE, false);
        return this;
    }

    public MessageSelection contactPositionTimestamp(long... value) {
        addEquals(ContactColumns.POSITION_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public MessageSelection contactPositionTimestampNot(long... value) {
        addNotEquals(ContactColumns.POSITION_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public MessageSelection contactPositionTimestampGt(long value) {
        addGreaterThan(ContactColumns.POSITION_TIMESTAMP, value);
        return this;
    }

    public MessageSelection contactPositionTimestampGtEq(long value) {
        addGreaterThanOrEquals(ContactColumns.POSITION_TIMESTAMP, value);
        return this;
    }

    public MessageSelection contactPositionTimestampLt(long value) {
        addLessThan(ContactColumns.POSITION_TIMESTAMP, value);
        return this;
    }

    public MessageSelection contactPositionTimestampLtEq(long value) {
        addLessThanOrEquals(ContactColumns.POSITION_TIMESTAMP, value);
        return this;
    }

    public MessageSelection orderByContactPositionTimestamp(boolean desc) {
        orderBy(ContactColumns.POSITION_TIMESTAMP, desc);
        return this;
    }

    public MessageSelection orderByContactPositionTimestamp() {
        orderBy(ContactColumns.POSITION_TIMESTAMP, false);
        return this;
    }

    public MessageSelection content(String... value) {
        addEquals(MessageColumns.CONTENT, value);
        return this;
    }

    public MessageSelection contentNot(String... value) {
        addNotEquals(MessageColumns.CONTENT, value);
        return this;
    }

    public MessageSelection contentLike(String... value) {
        addLike(MessageColumns.CONTENT, value);
        return this;
    }

    public MessageSelection contentContains(String... value) {
        addContains(MessageColumns.CONTENT, value);
        return this;
    }

    public MessageSelection contentStartsWith(String... value) {
        addStartsWith(MessageColumns.CONTENT, value);
        return this;
    }

    public MessageSelection contentEndsWith(String... value) {
        addEndsWith(MessageColumns.CONTENT, value);
        return this;
    }

    public MessageSelection orderByContent(boolean desc) {
        orderBy(MessageColumns.CONTENT, desc);
        return this;
    }

    public MessageSelection orderByContent() {
        orderBy(MessageColumns.CONTENT, false);
        return this;
    }

    public MessageSelection userissender(boolean value) {
        addEquals(MessageColumns.USERISSENDER, toObjectArray(value));
        return this;
    }

    public MessageSelection orderByUserissender(boolean desc) {
        orderBy(MessageColumns.USERISSENDER, desc);
        return this;
    }

    public MessageSelection orderByUserissender() {
        orderBy(MessageColumns.USERISSENDER, false);
        return this;
    }

    public MessageSelection timestamp(long... value) {
        addEquals(MessageColumns.TIMESTAMP, toObjectArray(value));
        return this;
    }

    public MessageSelection timestampNot(long... value) {
        addNotEquals(MessageColumns.TIMESTAMP, toObjectArray(value));
        return this;
    }

    public MessageSelection timestampGt(long value) {
        addGreaterThan(MessageColumns.TIMESTAMP, value);
        return this;
    }

    public MessageSelection timestampGtEq(long value) {
        addGreaterThanOrEquals(MessageColumns.TIMESTAMP, value);
        return this;
    }

    public MessageSelection timestampLt(long value) {
        addLessThan(MessageColumns.TIMESTAMP, value);
        return this;
    }

    public MessageSelection timestampLtEq(long value) {
        addLessThanOrEquals(MessageColumns.TIMESTAMP, value);
        return this;
    }

    public MessageSelection orderByTimestamp(boolean desc) {
        orderBy(MessageColumns.TIMESTAMP, desc);
        return this;
    }

    public MessageSelection orderByTimestamp() {
        orderBy(MessageColumns.TIMESTAMP, false);
        return this;
    }
}
