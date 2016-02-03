package ca.nbsoft.whereareyou.provider.contact;

import ca.nbsoft.whereareyou.provider.base.BaseModel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A contact.
 */
public interface ContactModel extends BaseModel {

    /**
     * User acount this contact is linked to
     * Cannot be {@code null}.
     */
    @NonNull
    String getAccount();

    /**
     * Name.
     * Can be {@code null}.
     */
    @Nullable
    String getName();

    /**
     * Get the {@code email} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getEmail();

    /**
     * Get the {@code userid} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getUserid();

    /**
     * Avatar url
     * Can be {@code null}.
     */
    @Nullable
    String getPhotoUrl();

    /**
     * Contact status
     */
    int getStatus();

    /**
     * Get the {@code blocked} value.
     */
    boolean getBlocked();

    /**
     * Get the {@code auto_reply} value.
     */
    boolean getAutoReply();

    /**
     * Get the {@code position_latitude} value.
     */
    double getPositionLatitude();

    /**
     * Get the {@code position_longitude} value.
     */
    double getPositionLongitude();

    /**
     * Get the {@code position_timestamp} value.
     */
    long getPositionTimestamp();
}
