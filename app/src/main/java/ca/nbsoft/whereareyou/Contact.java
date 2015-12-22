package ca.nbsoft.whereareyou;

import android.os.Parcel;
import android.os.Parcelable;

import ca.nbsoft.whereareyou.provider.contact.ContactCursor;

/**
 * Created by Nicolas on 2015-12-20.
 */
public class Contact implements Parcelable {
    private String mFirstName;
    private String mLastName;
    private String mEmail;

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mFirstName);
        dest.writeString(this.mLastName);
        dest.writeString(this.mEmail);
    }

    public Contact() {
    }

    static public Contact fromCursor( ContactCursor cursor)
    {
        Contact c = new Contact();
        c.mEmail = cursor.getEmail();
        c.mFirstName = cursor.getFirstName();
        c.mLastName = cursor.getLastName();

        return c;
    }

    public String getDisplayName()
    {
        // TODO
        return mEmail;
    }

    protected Contact(Parcel in) {
        this.mFirstName = in.readString();
        this.mLastName = in.readString();
        this.mEmail = in.readString();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
