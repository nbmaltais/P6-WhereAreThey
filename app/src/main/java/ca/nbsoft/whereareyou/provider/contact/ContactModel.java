package ca.nbsoft.whereareyou.provider.contact;

import ca.nbsoft.whereareyou.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A contact.
 */
public interface ContactModel extends BaseModel {

    /**
     * First name.
     * Can be {@code null}.
     */
    @Nullable
    String getFirstName();

    /**
     * Last name.
     * Can be {@code null}.
     */
    @Nullable
    String getLastName();

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
    String getAvatarUrl();

    /**
     * Get the {@code blocked} value.
     */
    boolean getBlocked();

    /**
     * Get the {@code auto_reply} value.
     */
    boolean getAutoReply();
}
