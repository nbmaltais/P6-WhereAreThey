package ca.nbsoft.whereareyou.backend.data;

import com.googlecode.objectify.Key;

import static ca.nbsoft.whereareyou.backend.OfyService.ofy;

/**
 * Created by Nicolas on 2016-01-03.
 */
public class UserProfileHelper {

    static public void saveUserProfile( UserProfile profile )
    {
        ofy().save().entity(profile).now();
    }

    static public UserProfile getUserProfile( String userId )
    {
        UserProfile userProfile = ofy().load().key(Key.create(UserProfile.class, userId)).now();
        return userProfile;
    }

    static public UserProfile findUserByRegistrationId(String regId) {
        return ofy().load().type(UserProfile.class).filter("regId", regId).first().now();
    }

    static public  UserProfile getUserProfileByEmail( String email)
    {
        return ofy().load().type(UserProfile.class).filter("email", email).first().now();
    }

    static public void deleteUserProfile(UserProfile userProfile)
    {
        ofy().delete().entity(userProfile).now();
    }

    static public void deleteUserProfile(String userId)
    {
        ofy().delete().key(Key.create(UserProfile.class, userId)).now();
    }

}
