package ca.nbsoft.whereareyou;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import ca.nbsoft.whereareyou.provider.contact.ContactCursor;

/**
 * Created by Nicolas on 2015-12-20.
 */
public class Contact implements Parcelable {



    private long mId;
    private String mUserId;
    private String mName;
    private String mPhotoUrl;
    private String mEmail;
    private double mLatitude;
    private double mLongitude;

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }



    public long getId() {
        return mId;
    }

    public String getUserId() {
        return mUserId;
    }

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
        c.mId = cursor.getId();
        c.mUserId = cursor.getUserid();
        c.mEmail = cursor.getEmail();
        c.mName = cursor.getName();
        c.mPhotoUrl = cursor.getPhotoUrl();
        c.mLatitude = cursor.getPositionLatitude();
        c.mLongitude = cursor.getPositionLongitude();
        return c;
    }

    public String getDisplayName()
    {
        return mName;
    }

    public LatLng getLatLong() {
        return new LatLng(mLatitude,mLongitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mUserId);
        dest.writeString(this.mName);
        dest.writeString(this.mPhotoUrl);
        dest.writeString(this.mEmail);
        dest.writeDouble(this.mLatitude);
        dest.writeDouble(this.mLongitude);
    }

    protected Contact(Parcel in) {
        this.mId = in.readLong();
        this.mUserId = in.readString();
        this.mName = in.readString();
        this.mPhotoUrl = in.readString();
        this.mEmail = in.readString();
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
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
