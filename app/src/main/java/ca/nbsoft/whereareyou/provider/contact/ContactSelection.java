package ca.nbsoft.whereareyou.provider.contact;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import ca.nbsoft.whereareyou.provider.base.AbstractSelection;

/**
 * Selection for the {@code contact} table.
 */
public class ContactSelection extends AbstractSelection<ContactSelection> {
    @Override
    protected Uri baseUri() {
        return ContactColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code ContactCursor} object, which is positioned before the first entry, or null.
     */
    public ContactCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new ContactCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public ContactCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code ContactCursor} object, which is positioned before the first entry, or null.
     */
    public ContactCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new ContactCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public ContactCursor query(Context context) {
        return query(context, null);
    }


    public ContactSelection id(long... value) {
        addEquals("contact." + ContactColumns._ID, toObjectArray(value));
        return this;
    }

    public ContactSelection idNot(long... value) {
        addNotEquals("contact." + ContactColumns._ID, toObjectArray(value));
        return this;
    }

    public ContactSelection orderById(boolean desc) {
        orderBy("contact." + ContactColumns._ID, desc);
        return this;
    }

    public ContactSelection orderById() {
        return orderById(false);
    }

    public ContactSelection account(String... value) {
        addEquals(ContactColumns.ACCOUNT, value);
        return this;
    }

    public ContactSelection accountNot(String... value) {
        addNotEquals(ContactColumns.ACCOUNT, value);
        return this;
    }

    public ContactSelection accountLike(String... value) {
        addLike(ContactColumns.ACCOUNT, value);
        return this;
    }

    public ContactSelection accountContains(String... value) {
        addContains(ContactColumns.ACCOUNT, value);
        return this;
    }

    public ContactSelection accountStartsWith(String... value) {
        addStartsWith(ContactColumns.ACCOUNT, value);
        return this;
    }

    public ContactSelection accountEndsWith(String... value) {
        addEndsWith(ContactColumns.ACCOUNT, value);
        return this;
    }

    public ContactSelection orderByAccount(boolean desc) {
        orderBy(ContactColumns.ACCOUNT, desc);
        return this;
    }

    public ContactSelection orderByAccount() {
        orderBy(ContactColumns.ACCOUNT, false);
        return this;
    }

    public ContactSelection name(String... value) {
        addEquals(ContactColumns.NAME, value);
        return this;
    }

    public ContactSelection nameNot(String... value) {
        addNotEquals(ContactColumns.NAME, value);
        return this;
    }

    public ContactSelection nameLike(String... value) {
        addLike(ContactColumns.NAME, value);
        return this;
    }

    public ContactSelection nameContains(String... value) {
        addContains(ContactColumns.NAME, value);
        return this;
    }

    public ContactSelection nameStartsWith(String... value) {
        addStartsWith(ContactColumns.NAME, value);
        return this;
    }

    public ContactSelection nameEndsWith(String... value) {
        addEndsWith(ContactColumns.NAME, value);
        return this;
    }

    public ContactSelection orderByName(boolean desc) {
        orderBy(ContactColumns.NAME, desc);
        return this;
    }

    public ContactSelection orderByName() {
        orderBy(ContactColumns.NAME, false);
        return this;
    }

    public ContactSelection email(String... value) {
        addEquals(ContactColumns.EMAIL, value);
        return this;
    }

    public ContactSelection emailNot(String... value) {
        addNotEquals(ContactColumns.EMAIL, value);
        return this;
    }

    public ContactSelection emailLike(String... value) {
        addLike(ContactColumns.EMAIL, value);
        return this;
    }

    public ContactSelection emailContains(String... value) {
        addContains(ContactColumns.EMAIL, value);
        return this;
    }

    public ContactSelection emailStartsWith(String... value) {
        addStartsWith(ContactColumns.EMAIL, value);
        return this;
    }

    public ContactSelection emailEndsWith(String... value) {
        addEndsWith(ContactColumns.EMAIL, value);
        return this;
    }

    public ContactSelection orderByEmail(boolean desc) {
        orderBy(ContactColumns.EMAIL, desc);
        return this;
    }

    public ContactSelection orderByEmail() {
        orderBy(ContactColumns.EMAIL, false);
        return this;
    }

    public ContactSelection userid(String... value) {
        addEquals(ContactColumns.USERID, value);
        return this;
    }

    public ContactSelection useridNot(String... value) {
        addNotEquals(ContactColumns.USERID, value);
        return this;
    }

    public ContactSelection useridLike(String... value) {
        addLike(ContactColumns.USERID, value);
        return this;
    }

    public ContactSelection useridContains(String... value) {
        addContains(ContactColumns.USERID, value);
        return this;
    }

    public ContactSelection useridStartsWith(String... value) {
        addStartsWith(ContactColumns.USERID, value);
        return this;
    }

    public ContactSelection useridEndsWith(String... value) {
        addEndsWith(ContactColumns.USERID, value);
        return this;
    }

    public ContactSelection orderByUserid(boolean desc) {
        orderBy(ContactColumns.USERID, desc);
        return this;
    }

    public ContactSelection orderByUserid() {
        orderBy(ContactColumns.USERID, false);
        return this;
    }

    public ContactSelection photoUrl(String... value) {
        addEquals(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public ContactSelection photoUrlNot(String... value) {
        addNotEquals(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public ContactSelection photoUrlLike(String... value) {
        addLike(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public ContactSelection photoUrlContains(String... value) {
        addContains(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public ContactSelection photoUrlStartsWith(String... value) {
        addStartsWith(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public ContactSelection photoUrlEndsWith(String... value) {
        addEndsWith(ContactColumns.PHOTO_URL, value);
        return this;
    }

    public ContactSelection orderByPhotoUrl(boolean desc) {
        orderBy(ContactColumns.PHOTO_URL, desc);
        return this;
    }

    public ContactSelection orderByPhotoUrl() {
        orderBy(ContactColumns.PHOTO_URL, false);
        return this;
    }

    public ContactSelection blocked(boolean value) {
        addEquals(ContactColumns.BLOCKED, toObjectArray(value));
        return this;
    }

    public ContactSelection orderByBlocked(boolean desc) {
        orderBy(ContactColumns.BLOCKED, desc);
        return this;
    }

    public ContactSelection orderByBlocked() {
        orderBy(ContactColumns.BLOCKED, false);
        return this;
    }

    public ContactSelection autoReply(boolean value) {
        addEquals(ContactColumns.AUTO_REPLY, toObjectArray(value));
        return this;
    }

    public ContactSelection orderByAutoReply(boolean desc) {
        orderBy(ContactColumns.AUTO_REPLY, desc);
        return this;
    }

    public ContactSelection orderByAutoReply() {
        orderBy(ContactColumns.AUTO_REPLY, false);
        return this;
    }

    public ContactSelection positionLatitude(float... value) {
        addEquals(ContactColumns.POSITION_LATITUDE, toObjectArray(value));
        return this;
    }

    public ContactSelection positionLatitudeNot(float... value) {
        addNotEquals(ContactColumns.POSITION_LATITUDE, toObjectArray(value));
        return this;
    }

    public ContactSelection positionLatitudeGt(float value) {
        addGreaterThan(ContactColumns.POSITION_LATITUDE, value);
        return this;
    }

    public ContactSelection positionLatitudeGtEq(float value) {
        addGreaterThanOrEquals(ContactColumns.POSITION_LATITUDE, value);
        return this;
    }

    public ContactSelection positionLatitudeLt(float value) {
        addLessThan(ContactColumns.POSITION_LATITUDE, value);
        return this;
    }

    public ContactSelection positionLatitudeLtEq(float value) {
        addLessThanOrEquals(ContactColumns.POSITION_LATITUDE, value);
        return this;
    }

    public ContactSelection orderByPositionLatitude(boolean desc) {
        orderBy(ContactColumns.POSITION_LATITUDE, desc);
        return this;
    }

    public ContactSelection orderByPositionLatitude() {
        orderBy(ContactColumns.POSITION_LATITUDE, false);
        return this;
    }

    public ContactSelection positionLongitude(float... value) {
        addEquals(ContactColumns.POSITION_LONGITUDE, toObjectArray(value));
        return this;
    }

    public ContactSelection positionLongitudeNot(float... value) {
        addNotEquals(ContactColumns.POSITION_LONGITUDE, toObjectArray(value));
        return this;
    }

    public ContactSelection positionLongitudeGt(float value) {
        addGreaterThan(ContactColumns.POSITION_LONGITUDE, value);
        return this;
    }

    public ContactSelection positionLongitudeGtEq(float value) {
        addGreaterThanOrEquals(ContactColumns.POSITION_LONGITUDE, value);
        return this;
    }

    public ContactSelection positionLongitudeLt(float value) {
        addLessThan(ContactColumns.POSITION_LONGITUDE, value);
        return this;
    }

    public ContactSelection positionLongitudeLtEq(float value) {
        addLessThanOrEquals(ContactColumns.POSITION_LONGITUDE, value);
        return this;
    }

    public ContactSelection orderByPositionLongitude(boolean desc) {
        orderBy(ContactColumns.POSITION_LONGITUDE, desc);
        return this;
    }

    public ContactSelection orderByPositionLongitude() {
        orderBy(ContactColumns.POSITION_LONGITUDE, false);
        return this;
    }

    public ContactSelection positionTimstamp(float... value) {
        addEquals(ContactColumns.POSITION_TIMSTAMP, toObjectArray(value));
        return this;
    }

    public ContactSelection positionTimstampNot(float... value) {
        addNotEquals(ContactColumns.POSITION_TIMSTAMP, toObjectArray(value));
        return this;
    }

    public ContactSelection positionTimstampGt(float value) {
        addGreaterThan(ContactColumns.POSITION_TIMSTAMP, value);
        return this;
    }

    public ContactSelection positionTimstampGtEq(float value) {
        addGreaterThanOrEquals(ContactColumns.POSITION_TIMSTAMP, value);
        return this;
    }

    public ContactSelection positionTimstampLt(float value) {
        addLessThan(ContactColumns.POSITION_TIMSTAMP, value);
        return this;
    }

    public ContactSelection positionTimstampLtEq(float value) {
        addLessThanOrEquals(ContactColumns.POSITION_TIMSTAMP, value);
        return this;
    }

    public ContactSelection orderByPositionTimstamp(boolean desc) {
        orderBy(ContactColumns.POSITION_TIMSTAMP, desc);
        return this;
    }

    public ContactSelection orderByPositionTimstamp() {
        orderBy(ContactColumns.POSITION_TIMSTAMP, false);
        return this;
    }
}
