package ca.nbsoft.whereareyou.backend.data;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * This class is taken directly from the ConferanceSample code from the Developping Scalable App in Java
 * https://github.com/udacity/ud859/blob/master/ConferenceCentral_Complete/src/main/java/com/google/devrel/training/conference/domain/AppEngineUser.java
 */
@Entity
public class AppEngineUser {
    @Id
    private String email;
    private User user;
    private AppEngineUser() {}
    public AppEngineUser(User user) {
        this.user = user;
        this.email = user.getEmail();
    }
    public User getUser() {
        return user;
    }
    public Key<AppEngineUser> getKey() {
        return Key.create(AppEngineUser.class, email);
    }
}