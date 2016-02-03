package ca.nbsoft.whereareyou.provider.message;

import ca.nbsoft.whereareyou.provider.base.BaseModel;

import android.support.annotation.NonNull;

/**
 * Data model for the {@code message} table.
 */
public interface MessageModel extends BaseModel {

    /**
     * Get the {@code contact_id} value.
     */
    long getContactId();

    /**
     * Get the {@code content} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getContent();

    /**
     * Get the {@code userissender} value.
     */
    boolean getUserissender();

    /**
     * Get the {@code timestamp} value.
     */
    long getTimestamp();
}
