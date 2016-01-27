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
import com.google.appengine.api.users.User;
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
import ca.nbsoft.whereareyou.backend.data.UserProfileHelper;
import ca.nbsoft.whereareyou.common.ContactStatus;
import ca.nbsoft.whereareyou.common.StatusCode;


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
  ClientsID.ANDROID_DEBUG_CLIENT_ID,
          ClientsID.ANDROID_RELEASE_CLIENT_ID,
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

    public static class IntegerResult {

        private final Integer result;

        public IntegerResult(Integer result) {
            this.result = result;
        }

        public Integer getResult() {
            return result;
        }
    }

    public static class StatusResult
    {

        public int getResultCode() {
            return resultCode;
        }

        public String getDescription() {
            return description;
        }

        private final Integer resultCode;
        private final String description;

        public StatusResult(Integer resultCode, String description) {
            this.resultCode = resultCode;
            this.description = description;
        }

    }

    public static StatusResult noErrorStatusResult()
    {
        return new StatusResult(StatusCode.RESULT_OK,"Success");
    }

    public static StatusResult userUnsubscribedStatusResult()
    {
        return new StatusResult(StatusCode.RESULT_USER_UNSUBSCRIBED,"This user unsubscribed.");
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

    private UserProfile createUserProfile(User user, NewAccountInfo accountInfo)
    {
        String userId = getUserId(user);
        String email = user.getEmail();

        UserProfile record = new UserProfile();
        record.setUserId(userId);
        record.setEmail(email);
        record.setRegId(null);

        record.setDisplayName(accountInfo.getDisplayName());
        record.setPhotoUrl(accountInfo.getPhotoUrl());

        return record;
    }

    /**
     * Create a user account.
     * @param user
     * @throws UnauthorizedException
     */
    @ApiMethod(name = "createAccount")
    public StatusResult createAccount(User user, NewAccountInfo accountInfo) throws UnauthorizedException {

        log.info("API call: createAccount, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = UserProfileHelper.getUserProfile(getUserId(user));
        if(userProfile == null)
        {
            userProfile = createUserProfile(user,accountInfo);
            UserProfileHelper.saveUserProfile(userProfile);

            return noErrorStatusResult();
        }
        else
        {
            log.info("User " + user.getEmail() + " already signed up");

            userProfile.setDisplayName(accountInfo.getDisplayName());
            userProfile.setPhotoUrl(accountInfo.getPhotoUrl());
            UserProfileHelper.saveUserProfile(userProfile);

            return new StatusResult(StatusCode.RESULT_ACCOUNT_ALREADY_EXISTS,"Account already exists");

        }


    }

    /**
     * Register a device to the backend. Create user account if needed
     *
     * @param registrationId The Google Cloud Messaging registration Id to add
     */
    @ApiMethod(name = "register")
    public StatusResult registerDevice(User user, RegistrationId registrationId) throws UnauthorizedException, InvalidUserException {

        log.info("API call: registerDevice, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = UserProfileHelper.getUserProfile(getUserId(user));
        if(userProfile == null)
        {
            throw new InvalidUserException();
        }

        String regId = registrationId.getToken();
        log.info("Registering device " + regId);

        userProfile.setRegId(regId);
        UserProfileHelper.saveUserProfile(userProfile);


        return noErrorStatusResult();
    }

    /**
     * Delete a user account
     * @param user
     * @throws UnauthorizedException
     */
    @ApiMethod(name = "deleteAccount")
    public StatusResult deleteAccount( User user ) throws UnauthorizedException, InvalidUserException {

        log.info("API call: deleteAccount, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);

        UserProfileHelper.deleteUserProfile(userProfile);
        return noErrorStatusResult();
    }

    /**
     * Unregister a device from the backend
     *
     * @param registrationId The Google Cloud Messaging registration Id to remove
     */
    @ApiMethod(name = "unregister")
    public StatusResult unregisterDevice(User user, RegistrationId registrationId) throws UnauthorizedException, InvalidUserException {

        log.info("API call: unregisterDevice, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        String regId = registrationId.getToken();
        UserProfile userProfile = getUserProfile(user);

        userProfile.setRegId(null);
        UserProfileHelper.saveUserProfile(userProfile);
        return noErrorStatusResult();
    }

    @ApiMethod(name = "requestContactLocation")
    public StatusResult requestContactLocation( User user, @Named("contactId") String contactUserId,
                                        @Nullable @Named("message") String message ) throws UnauthorizedException, InvalidUserException, IOException {

        log.info("API call: requestContactLocation, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }


        UserProfile userProfile = getUserProfile(user);


        if(!userProfile.containsContactUserId(contactUserId))
            throw new InvalidUserException("Specified user is not in contact list.");

        UserProfile contactUserProfile = UserProfileHelper.getUserProfile(contactUserId);
        if(contactUserProfile==null)
        {
            return handleDeletedUser(userProfile,contactUserId);
        }

        GcmMessages.sendLocationRequest(userProfile, contactUserProfile, message);

        return noErrorStatusResult();
    }



    @ApiMethod(name = "removeContact")
    public StatusResult removeContact(User user, @Named("contactId") String contactUserId) throws UnauthorizedException, InvalidUserException, IOException {

        log.info("API call: requestContactLocation, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);

        if(!userProfile.containsContactUserId(contactUserId) && !userProfile.containsPendingContactRequestUserId(contactUserId))
        {
            return new StatusResult(StatusCode.RESULT_NOT_IN_CONTACT,"User is not in contact list.");
        }

        userProfile.removeContactUserId(contactUserId);
        userProfile.removePendingContactRequestUserId(contactUserId);

        UserProfileHelper.saveUserProfile(userProfile);

        UserProfile contactProfile = UserProfileHelper.getUserProfile(contactUserId);
        if(contactProfile==null)
        {
            return handleDeletedUser(userProfile,contactUserId);
        }

        contactProfile.removeContactUserId(userProfile.getUserId());
        contactProfile.removeWaitingForConfirmationUserId(userProfile.getUserId());
        UserProfileHelper.saveUserProfile(contactProfile);

        GcmMessages.notifyContactListModified(userProfile);

        return noErrorStatusResult();
    }


    @ApiMethod(name = "sendLocation")
    public StatusResult sendLocation( User user,
                              @Named("contactId") String contactUserId,
                              Location location,
                              @Nullable @Named("message") String message ) throws Exception {

        log.info("API call: sendLocation, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);
        if(!userProfile.containsContactUserId(contactUserId))
            throw new InvalidUserException();

        UserProfile contactProfile = UserProfileHelper.getUserProfile(contactUserId);
        if(contactProfile==null)
        {
            return handleDeletedUser(userProfile,contactUserId);
        }

        GcmMessages.sendLocation(userProfile, contactProfile, location, message);

        return noErrorStatusResult();
    }

    @ApiMethod(name = "sendContactRequest")
    public StatusResult sendContactRequest(User user, @Named("contactEmail") String contactEmail) throws Exception {

        log.info("API call: sendContactRequest, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        if(user.getEmail().equals(contactEmail))
        {
            throw new InvalidUserException("Sending contact request to self is not permitted.");
        }

        UserProfile userProfile = getUserProfile(user);

        UserProfile contactProfile = UserProfileHelper.getUserProfileByEmail(contactEmail);
        if(contactProfile==null)
        {
            // TODO: send invitation??
            return new StatusResult(StatusCode.RESULT_NO_USER_WITH_EMAIL,"No user with the specified email has signed in.");
        }

        boolean contactContainUser = contactProfile.containsContactUserId(userProfile.getUserId());
        boolean userContainContact = userProfile.containsContactUserId(contactProfile.getUserId());;

        if( contactContainUser && userContainContact)
        {
            log.info("User is already in contact, aborting sendContactRequest");
            return new StatusResult(StatusCode.RESULT_CONTACT_ALREADY_ADDED,"Already in contact list");
        }
        else if( contactContainUser || userContainContact )
        {
            // This is weird, but it can happen if one of the user
            // delete and recreate it's account. Remove contact from each other
            // and reconfirm
            contactProfile.removeContactUserId(userProfile.getUserId());
            userProfile.removeContactUserId(contactProfile.getUserId());
        }

        if( userProfile.containsPendingContactRequestUserId(contactProfile.getUserId()) )
        {
            log.info("User is already in pending contact request, aborting sendContactRequest");
            return new StatusResult(StatusCode.RESULT_CONTACT_REQUEST_PENDING,"A request is already pending");
        }

        if( contactProfile.containsContactUserId(userProfile.getUserId()) )
        {
            // The contact already accepted the user, but the user does not have contact in his contact
            // list. This could happen if user deleted his accout and recreated it.
            // We will ask confirmation just in case
            contactProfile.removeContactUserId(userProfile.getUserId());
        }


        userProfile.addPendingContactRequestUserId(contactProfile.getUserId());
        contactProfile.addWaitingForConfirmationUserId(userProfile.getUserId());

        UserProfileHelper.saveUserProfile(userProfile);
        UserProfileHelper.saveUserProfile(contactProfile);

        GcmMessages.sendContactRequest(userProfile, contactProfile);
        GcmMessages.notifyContactListModified(userProfile);

        return noErrorStatusResult();
    }

    @ApiMethod(name = "confirmContactRequest")
    public StatusResult confirmContactRequest(User user,
                                      @Named("contactUserId") String contactUserId,
                                        @Named("accept") boolean accept ) throws UnauthorizedException, IOException, InvalidUserException {

        log.info("API call: confirmContactRequest, user = " + user);
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);
        UserProfile contactProfile = UserProfileHelper.getUserProfile(contactUserId);

        if(contactProfile==null)
        {
            return handleDeletedUser(userProfile,contactUserId);
        }

        String userId = userProfile.getUserId();

        if( !contactProfile.containsPendingContactRequestUserId(userId) )
        {
            log.info("No pending request found for userid = " + userId + ", aborting confirmContactRequest");
            return new StatusResult(StatusCode.RESULT_NO_PENDING_REQUEST,"No pending contact request to confirm.");
        }

        contactProfile.removePendingContactRequestUserId(userId);
        userProfile.removeWaitingForConfirmationUserId(contactUserId);

        if(accept) {
            contactProfile.addContactUserId(userId);
            userProfile.addContactUserId(contactUserId);
        }

        UserProfileHelper.saveUserProfile(contactProfile);
        UserProfileHelper.saveUserProfile(userProfile);

        if(accept) {
            //  send notification that contact request is confirmed
            GcmMessages.confirmContactRequest(userProfile, contactProfile);
            GcmMessages.notifyContactListModified(userProfile);
        }
        else
        {
            GcmMessages.notifyContactListModified(userProfile);
            GcmMessages.notifyContactListModified(contactProfile);
        }


        return noErrorStatusResult();
    }



    @ApiMethod(name = "getUserProfile")
    public UserProfile getUserProfile(User user) throws UnauthorizedException, InvalidUserException {
        log.info("API call: getUserProfile, user = " + user);
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = UserProfileHelper.getUserProfile(getUserId(user));
        if(userProfile==null)
            throw new InvalidUserException();

        return userProfile;
    }

    @ApiMethod(name = "getContactInfo")
    public ContactInfo getContactInfo( @Named("contactUserId") String contactUserId, User user ) throws Exception {
        log.info("API call: getContactInfo, user = " + user);

        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        UserProfile userProfile = getUserProfile(user);
        if( userProfile.containsContactUserId(contactUserId) ) {

            UserProfile contactProfile = UserProfileHelper.getUserProfile(contactUserId);
            return createContactInfo(contactProfile, ContactStatus.NONE);
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

        for( String contactId : userProfile.getContactsUserId())
        {
            UserProfile contactProfile = UserProfileHelper.getUserProfile(contactId);

            if(contactProfile!=null) {
                contacts.add(createContactInfo(contactProfile, ContactStatus.NONE));
            }
            else {
                userProfile.removeContactUserId(contactId);
                modified=true;
            }
        }

        for( String contactId : userProfile.getPendingContactRequestsUserId())
        {
            UserProfile contactProfile = UserProfileHelper.getUserProfile(contactId);

            if(contactProfile!=null) {
                contacts.add(createContactInfo(contactProfile, ContactStatus.PENDING));
            }
            else {
                userProfile.removePendingContactRequestUserId(contactId);
                modified=true;
            }
        }

        for( String contactId : userProfile.getWaitingForConfirmationUserId())
        {
            UserProfile contactProfile = UserProfileHelper.getUserProfile(contactId);

            if(contactProfile!=null) {
                contacts.add(createContactInfo(contactProfile, ContactStatus.WAITING_FOR_CONFIRMATION));
            }
            else {
                userProfile.removeWaitingForConfirmationUserId(contactId);
                modified=true;
            }
        }

        if(modified)
        {
            UserProfileHelper.saveUserProfile(userProfile);
        }

        return contacts;
    }

    private StatusResult handleDeletedUser(UserProfile userProfile, String contactUserId) throws IOException {

        // Remove user from all user lists
        userProfile.removeContactUserId(contactUserId);
        userProfile.removePendingContactRequestUserId(contactUserId);
        userProfile.removeWaitingForConfirmationUserId(contactUserId);

        UserProfileHelper.saveUserProfile(userProfile);

        GcmMessages.notifyContactListModified(userProfile);

        return userUnsubscribedStatusResult();
    }

    private ContactInfo createContactInfo( UserProfile contactProfile, int status )
    {
        // TODO: return all info if status is pending???

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(contactProfile.getEmail());
        contactInfo.setUserId(contactProfile.getUserId());
        contactInfo.setDisplayName(contactProfile.getDisplayName());
        contactInfo.setPhotoUrl(contactProfile.getPhotoUrl());
        contactInfo.setStatus(status);

        return contactInfo;
    }

}
