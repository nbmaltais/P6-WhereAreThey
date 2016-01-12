package ca.nbsoft.whereareyou;

import android.os.Parcel;
import android.os.Parcelable;

import ca.nbsoft.whereareyou.provider.contact.ContactCursor;

/**
 * Created by Nicolas on 2015-12-20.
 */
public class Contact implements Parcelable {


    private String mName;
    private String mPhotoUrl;
    private String mEmail;


    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    public Contact() {
    }

    static public Contact fromCursor( ContactCursor cursor)
    {
        Contact c = new Contact();
        c.mEmail = cursor.getEmail();
        c.mName = cursor.getName();
        c.mPhotoUrl = cursor.getPhotoUrl();

        return c;
    }

    public String getDisplayName()
    {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mPhotoUrl);
        dest.writeString(this.mEmail);
    }

    protected Contact(Parcel in) {
        this.mName = in.readString();
        this.mPhotoUrl = in.readString();
        this.mEmail = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
