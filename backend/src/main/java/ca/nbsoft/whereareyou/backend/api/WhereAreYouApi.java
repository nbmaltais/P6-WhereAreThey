/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package ca.nbsoft.whereareyou.backend.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
  name = "whereAreYou",
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
public class WhereAreYouApi {

    private static final Logger log = Logger.getLogger(WhereAreYouApi.class.getName());


    public static class BooleanResult {

        private final Boolean result;

        public BooleanResult(Boolean result) {
            this.result = result;
        }

        public Boolean getResult() {
            return result;
        }
    }

    public static class StringResult {

        private final String result;

        public StringResult(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }
    }

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

    private UserProfile createUserRecord(User user)
    {
        String userId = getUserId(user);
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
    public StringResult createAccount(User user) throws UnauthorizedException {

        log.info("API call: createAccount, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);
        if(userProfile == null)
        {
            UserProfile record = createUserRecord(user);

            ofy().save().entity(record).now();
        }
        else
        {
            log.info("User " + user.getEmail() + " already signed up, skipping");
        }


        return new StringResult(userProfile.getUserId());
    }

    /**
     * Register a device to the backend. Create user account if needed
     *
     * @param registrationId The Google Cloud Messaging registration Id to add
     */
    @ApiMethod(name = "register")
    public StringResult registerDevice(RegistrationId registrationId, User user) throws UnauthorizedException {

        log.info("API call: registerDevice, user = " + user);

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

        return new StringResult(userProfile.getUserId());
    }

    /**
     * Delete a user account
     * @param user
     * @throws UnauthorizedException
     */
    @ApiMethod(name = "deleteAccount")
    public void deleteAccount( User user ) throws UnauthorizedException {

        log.info("API call: deleteAccount, user = " + user);

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
    public void unregisterDevice(User user, RegistrationId registrationId) throws UnauthorizedException {

        log.info("API call: unregisterDevice, user = " + user);

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
    public void requestContactLocation( User user, @Named("contactId") String contactUserId,
                                        @Nullable @Named("message") String message ) throws Exception {

        log.info("API call: requestContactLocation, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);

        if(!userProfile.containsContactUserId(contactUserId))
            throw new Exception("Invalid contact");

        UserProfile contactUserProfile = getUserProfileById(contactUserId);


        GcmMessages.sendLocationRequest(userProfile, contactUserProfile, message);

    }

    @ApiMethod(name = "sendLocation")
    public void sendLocation( User user,
                              @Named("contactId") String contactUserId,
                              Location location,
                              @Nullable @Named("message") String message ) throws Exception {

        log.info("API call: sendLocation, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);
        if(!userProfile.containsContactUserId(contactUserId))
            throw new InvalidContactException();

        UserProfile contactProfile = getUserProfileById(contactUserId);

        GcmMessages.sendLocation(userProfile, contactProfile, location, message);
    }

    @ApiMethod(name = "sendContactRequest")
    public void sendContactRequest(User user, @Named("contactEmail") String contactEmail) throws Exception {

        log.info("API call: sendContactRequest, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);

        UserProfile contactProfile=getUserProfileByEmail(contactEmail);
        if(contactProfile==null)
        {
            // TODO: send invitation??
            throw new InvalidContactException();
        }

        if( userProfile.containsContactUserId(contactProfile.getUserId()) )
        {
            log.info("User is already in contact, aborting sendContactRequest");
            return;
        }

        if( userProfile.containsPendingContactRequestUserId(contactProfile.getUserId()) )
        {
            log.info("User is already in pending contact request, aborting sendContactRequest");
            return;
        }



        GcmMessages.sendContactRequest(userProfile, contactProfile);

        userProfile.addPendingContactRequestUserId(contactProfile.getUserId());
        ofy().save().entity(userProfile).now();
    }

    @ApiMethod(name = "confirmContactRequest")
    public void confirmContactRequest(User user,
                                      @Named("contactUserId") String contactUserId) throws UnauthorizedException, IOException {

        log.info("API call: confirmContactRequest, user = " + user);
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);
        UserProfile contactProfile = getUserProfileById(contactUserId);

        String userId = userProfile.getUserId();

        if( !contactProfile.containsPendingContactRequestUserId(userId) )
        {
            log.info("No pending request found for userid = " + userId + ", aborting confirmContactRequest");
            return;
        }

        contactProfile.removePendingContactRequestUserId(userId);
        contactProfile.addContactUserId(userId);
        ofy().save().entity(contactProfile).now();

        userProfile.addContactUserId(contactUserId);
        ofy().save().entity(userProfile).now();

        // TODO send notification that contact request is confirmed
        GcmMessages.confirmContactRequest(userProfile, contactProfile);
    }

    private UserProfile getUserProfileById( String userId)
    {
        return ofy().load().key(Key.create(UserProfile.class, userId)).now();
    }

    private UserProfile getUserProfileByEmail( String email)
    {
        return ofy().load().type(UserProfile.class).filter("email", email).first().now();
    }

    private UserProfile findUserByRegistrationId(String regId) {
        return ofy().load().type(UserProfile.class).filter("regId", regId).first().now();
    }

    @ApiMethod(name = "getUserProfile")
    public UserProfile getUserProfile(User user) throws UnauthorizedException {
        log.info("API call: getUserProfile, user = " + user);
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        return ofy().load().key(Key.create(UserProfile.class, getUserId(user))).now();
    }

    @ApiMethod(name = "getContactInfo")
    public ContactInfo getContactInfo( @Named("contactUserId") String contactUserId, User user ) throws Exception {
        log.info("API call: getContactInfo, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);
        if( userProfile.containsContactUserId(contactUserId) ) {

            UserProfile contactProfile = ofy().load().key(Key.create(UserProfile.class, contactUserId)).now();
            return createContactInfo(contactProfile);
        }
        else
        {
            throw new Exception("Invalid contact.");
        }
    }

    @ApiMethod(name = "getContacts")
    public Collection<ContactInfo> getContacts(  User user ) throws Exception {
        log.info("API call: getContacts, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        boolean modified=false;
        UserProfile userProfile = getUserProfile(user);
        Collection<ContactInfo> contacts = new ArrayList<>();

        for( String contactId : userProfile.getContactsKeys())
        {
            UserProfile contactProfile = ofy().load().key(Key.create(UserProfile.class, contactId)).now();
            if(contactProfile!=null) {
                contacts.add(createContactInfo(contactProfile));
            }
            else {
                userProfile.removeContactUserId(contactId);
                modified=true;
            }
        }

        if(modified)
        {
            ofy().save().entity(userProfile).now();
        }

        return contacts;
    }

    private ContactInfo createContactInfo( UserProfile contactProfile )
    {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(contactProfile.getEmail());
        contactInfo.setUserId(contactProfile.getUserId());

        return contactInfo;
    }

}