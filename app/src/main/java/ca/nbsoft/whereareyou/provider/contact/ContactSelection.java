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

    public ContactSelection firstName(String... value) {
        addEquals(ContactColumns.FIRST_NAME, value);
        return this;
    }

    public ContactSelection firstNameNot(String... value) {
        addNotEquals(ContactColumns.FIRST_NAME, value);
        return this;
    }

    public ContactSelection firstNameLike(String... value) {
        addLike(ContactColumns.FIRST_NAME, value);
        return this;
    }

    public ContactSelection firstNameContains(String... value) {
        addContains(ContactColumns.FIRST_NAME, value);
        return this;
    }

    public ContactSelection firstNameStartsWith(String... value) {
        addStartsWith(ContactColumns.FIRST_NAME, value);
        return this;
    }

    public ContactSelection firstNameEndsWith(String... value) {
        addEndsWith(ContactColumns.FIRST_NAME, value);
        return this;
    }

    public ContactSelection orderByFirstName(boolean desc) {
        orderBy(ContactColumns.FIRST_NAME, desc);
        return this;
    }

    public ContactSelection orderByFirstName() {
        orderBy(ContactColumns.FIRST_NAME, false);
        return this;
    }

    public ContactSelection lastName(String... value) {
        addEquals(ContactColumns.LAST_NAME, value);
        return this;
    }

    public ContactSelection lastNameNot(String... value) {
        addNotEquals(ContactColumns.LAST_NAME, value);
        return this;
    }

    public ContactSelection lastNameLike(String... value) {
        addLike(ContactColumns.LAST_NAME, value);
        return this;
    }

    public ContactSelection lastNameContains(String... value) {
        addContains(ContactColumns.LAST_NAME, value);
        return this;
    }

    public ContactSelection lastNameStartsWith(String... value) {
        addStartsWith(ContactColumns.LAST_NAME, value);
        return this;
    }

    public ContactSelection lastNameEndsWith(String... value) {
        addEndsWith(ContactColumns.LAST_NAME, value);
        return this;
    }

    public ContactSelection orderByLastName(boolean desc) {
        orderBy(ContactColumns.LAST_NAME, desc);
        return this;
    }

    public ContactSelection orderByLastName() {
        orderBy(ContactColumns.LAST_NAME, false);
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

    public ContactSelection avatarUrl(String... value) {
        addEquals(ContactColumns.AVATAR_URL, value);
        return this;
    }

    public ContactSelection avatarUrlNot(String... value) {
        addNotEquals(ContactColumns.AVATAR_URL, value);
        return this;
    }

    public ContactSelection avatarUrlLike(String... value) {
        addLike(ContactColumns.AVATAR_URL, value);
        return this;
    }

    public ContactSelection avatarUrlContains(String... value) {
        addContains(ContactColumns.AVATAR_URL, value);
        return this;
    }

    public ContactSelection avatarUrlStartsWith(String... value) {
        addStartsWith(ContactColumns.AVATAR_URL, value);
        return this;
    }

    public ContactSelection avatarUrlEndsWith(String... value) {
        addEndsWith(ContactColumns.AVATAR_URL, value);
        return this;
    }

    public ContactSelection orderByAvatarUrl(boolean desc) {
        orderBy(ContactColumns.AVATAR_URL, desc);
        return this;
    }

    public ContactSelection orderByAvatarUrl() {
        orderBy(ContactColumns.AVATAR_URL, false);
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
}
