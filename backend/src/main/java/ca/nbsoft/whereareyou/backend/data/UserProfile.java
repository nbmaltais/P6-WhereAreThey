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

    private List<String> contactsUserId = new ArrayList<>(0);

    private List<String> pendingContactRequestsUserId = new ArrayList<>(0);

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

    public List<String> getContactsKeys() {
        return ImmutableList.copyOf(contactsUserId);
    }

    public void addContactUserId(String contactKey) {
        contactsUserId.add(contactKey);
    }

    public void removeContactUserId(String contactKey) {
        contactsUserId.remove(contactKey);
    }

    public boolean containsContactUserId(String contactKey) {
        return contactsUserId.contains(contactKey);
    }

    public void addPendingContactRequestUserId(String contactKey) {
        pendingContactRequestsUserId.add(contactKey);
    }

    public void removePendingContactRequestUserId(String contactKey) {
        pendingContactRequestsUserId.remove(contactKey);
    }

    public boolean containsPendingContactRequestUserId(String contactKey) {
        return pendingContactRequestsUserId.contains(contactKey);
    }
}