package ca.nbsoft.whereareyou.backend.data;

import com.google.appengine.api.users.User;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class UserProfile {

    @Id
    String userId;

    @Index
    private String regId;

    @Index
    private String email;

    // Contacts
    private List<String> contactsUserId = new ArrayList<>(0);

    // Contacts for whom this user is waiting for a confirmation
    private List<String> pendingContactRequestsUserId = new ArrayList<>(0);

    // Contacts who are waiting a confirmation from this user
    private List<String> waitingForConfirmationUserId = new ArrayList<>(0);

    public UserProfile() {
    }

    public UserProfile(User user) {
        userId = user.getUserId();
        email = user.getEmail();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public List<String> getContactsUserId() {
        return ImmutableList.copyOf(contactsUserId);
    }

    public void addContactUserId(String userId) {
        if(!contactsUserId.contains(userId))
            contactsUserId.add(userId);
    }

    public void removeContactUserId(String userId) {
        contactsUserId.remove(userId);
    }

    public boolean containsContactUserId(String userId) {
        return contactsUserId.contains(userId);
    }

    public void addPendingContactRequestUserId(String userId) {
        if(!pendingContactRequestsUserId.contains(userId))
            pendingContactRequestsUserId.add(userId);
    }

    public void removePendingContactRequestUserId(String userId) {
        pendingContactRequestsUserId.remove(userId);
    }

    public boolean containsPendingContactRequestUserId(String userId) {
        return pendingContactRequestsUserId.contains(userId);
    }

    public void removeWaitingForConfirmationUserId(String userId) {
        waitingForConfirmationUserId.remove(userId);
    }

    public void addWaitingForConfirmationUserId(String userId) {
        if(!waitingForConfirmationUserId.contains(userId))
            waitingForConfirmationUserId.add(userId);

    }

    public boolean containsWaitingForConfirmationUserId(String userId)
    {
        return waitingForConfirmationUserId.contains(userId);
    }
}