/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package ca.nbsoft.whereareyou.backend.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import java.io.IOException;
import java.util.logging.Logger;
import javax.inject.Named;

import ca.nbsoft.whereareyou.backend.ClientsID;
import ca.nbsoft.whereareyou.backend.GcmMessages;
import ca.nbsoft.whereareyou.backend.data.AppEngineUser;
import ca.nbsoft.whereareyou.backend.data.UserProfile;


import static ca.nbsoft.whereareyou.backend.OfyService.factory;
import static ca.nbsoft.whereareyou.backend.OfyService.ofy;

/**
 * A registration endpoint class we are exposing for a device's GCM registration id on the backend
 *
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 *
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(
  name = "registration",
  version = "v1",
  scopes = {ClientsID.EMAIL_SCOPE},
  clientIds = {ClientsID.WEB_CLIENT_ID,
  ClientsID.ANDROID_CLIENT_ID,
  com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID},
  audiences = {ClientsID.ANDROID_AUDIENCE},
  namespace = @ApiNamespace(
    ownerDomain = "backend.whereareyou.nbsoft.ca",
    ownerName = "backend.whereareyou.nbsoft.ca",
    packagePath=""
  )
)
public class RegistrationEndpoint {

    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());


    /**
     * Taken from the udacity ccourse on Scalable app with Java
     * https://github.com/udacity/ud859/blob/master/ConferenceCentral_Complete/src/main/java/com/google/devrel/training/conference/spi/ConferenceApi.java
     *
     * This is an ugly workaround for null userId for Android clients.
     *
     * @param user A User object injected by the cloud endpoints.
     * @return the App Engine userId for the user.
     */

    private static String getUserId(User user) {
        String userId = user.getUserId();
        if (userId == null) {
            log.info("userId is null, so trying to obtain it from the datastore.");
            AppEngineUser appEngineUser = new AppEngineUser(user);
            ofy().save().entity(appEngineUser).now();
            // Begin new session for not using session cache.
            Objectify objectify = factory().begin();
            AppEngineUser savedUser = objectify.load().key(appEngineUser.getKey()).now();
            userId = savedUser.getUser().getUserId();
            log.info("Obtained the userId: " + userId);
        }
        return userId;
    }

    UserProfile createUserRecord(User user)
    {
        String userId = user.getUserId();
        String email = user.getEmail();

        UserProfile record = new UserProfile();
        record.setUserId(userId);
        record.setEmail(email);
        record.setRegId(null);

        return record;
    }

    /**
     * Create a user account.
     * @param user
     * @throws UnauthorizedException
     */
    @ApiMethod(name = "createAccount")
    public void createAccount(User user) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);
        if(userProfile != null)
        {
            log.info("User " + user.getEmail() + " already signed up, skipping");
            return;
        }

        UserProfile record = createUserRecord(user);

        ofy().save().entity(record).now();
    }

    /**
     * Register a device to the backend. Create user account if needed
     *
     * @param registrationId The Google Cloud Messaging registration Id to add
     */
    @ApiMethod(name = "register")
    public void registerDevice(RegistrationId registrationId, User user) throws UnauthorizedException {

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);
        if(userProfile == null)
        {
            userProfile = createUserRecord(user);
        }

        String regId = registrationId.getToken();
        log.info("Registering device " + regId);

        userProfile.setRegId(regId);

        ofy().save().entity(userProfile).now();
    }

    /**
     * Delete a user account
     * @param user
     * @throws UnauthorizedException
     */
    @ApiMethod(name = "deleteAccount")
    public void deleteAccount( User user ) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);
        if(userProfile == null)
        {
            log.info("User account" + user.getEmail() + " doesn't exist, can't delete account");
            return;
        }

        ofy().delete().entity(userProfile).now();
    }

    /**
     * Unregister a device from the backend
     *
     * @param registrationId The Google Cloud Messaging registration Id to remove
     */
    @ApiMethod(name = "unregister")
    public void unregisterDevice(RegistrationId registrationId, User user) throws UnauthorizedException {

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        String regId = registrationId.getToken();
        UserProfile userProfile = getUserProfile(user);
        if (userProfile == null) {
            log.info("Device " + regId + " not registered, skipping unregister");
            return;
        }

        userProfile.setRegId(null);
        ofy().save().entity(userProfile).now();
    }

    @ApiMethod(name = "requestContactLocation")
    public void requestContactLocation( @Named("contactId") String contactUserId, @Named("message") String message, User user ) throws UnauthorizedException, IOException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);
        UserProfile contactUserProfile = getUserProfileById(contactUserId);

        if(userProfile ==null || contactUserProfile ==null)
        {
            // TODO: 2015-12-10
            return;
        }

        GcmMessages.sendLocationRequest( userProfile.getUserId(), contactUserProfile.getRegId(), message );

    }

    private UserProfile getUserProfileById( String userId)
    {
        return ofy().load().key(Key.create(UserProfile.class, userId)).now();
    }

    private UserProfile findUserByRegistrationId(String regId) {
        return ofy().load().type(UserProfile.class).filter("regId", regId).first().now();
    }

    @ApiMethod(name = "getUserProfile")
    public UserProfile getUserProfile(User user) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        return ofy().load().key(Key.create(UserProfile.class, getUserId(user))).now();
    }

}