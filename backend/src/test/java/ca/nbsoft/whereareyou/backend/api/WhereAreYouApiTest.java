package ca.nbsoft.whereareyou.backend.api;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import ca.nbsoft.whereareyou.backend.data.UserProfile;
import ca.nbsoft.whereareyou.backend.data.UserProfileHelper;

import static ca.nbsoft.whereareyou.backend.OfyService.ofy;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Nicolas on 2016-01-03.
 */
public class WhereAreYouApiTest {
    private static final String EMAIL = "testuser@example.com";
    private static final String USER_ID = "123456789";
    private static final String REGISTRATION_TOKEN_VALUE = "dummy_reg_token";
    private static final RegistrationId REGISTRATION_TOKEN = new RegistrationId(REGISTRATION_TOKEN_VALUE);
    private WhereAreYouApi whereAreYouApi;
    private User user;
    private User otherUser;
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
                    //.setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

    @Before
    public void setUp() throws Exception {
        helper.setUp();
        user = new User(EMAIL, "gmail.com", USER_ID);
        otherUser = new User("other-user@test.com", "gmail.com","987654321");
        whereAreYouApi = new WhereAreYouApi();
    }

    @After
    public void tearDown() throws Exception {
        ofy().clear();
        helper.tearDown();
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetProfile_WithoutUser() throws Exception {
        whereAreYouApi.getUserProfile(null);
    }

    @Test
    public void testCreateAccount() throws UnauthorizedException, InvalidUserException {
        NewAccountInfo accountInfo = new NewAccountInfo();
        accountInfo.setDisplayName("Me myself and I");
        accountInfo.setPhotoUrl("http://photo.jpg");

        WhereAreYouApi.StatusResult result = whereAreYouApi.createAccount(user,accountInfo);

        assertEquals(result.getResultCode(), WhereAreYouApi.RESULT_OK);

        UserProfile userProfile = whereAreYouApi.getUserProfile(user);
        assertNotNull(userProfile);
        assertEquals(userProfile.getUserId(), user.getUserId());
        assertEquals(userProfile.getEmail(), user.getEmail());
        assertEquals(userProfile.getDisplayName(), accountInfo.getDisplayName());
        assertEquals(userProfile.getPhotoUrl(), accountInfo.getPhotoUrl());
    }



    /**
     * Setup two user profiles that are contact of each other
     */
    private void setupProfilesWithContacts()
    {
        // ensure that both user are registered

        UserProfile profile1 = new UserProfile(user);
        UserProfile profile2 = new UserProfile(otherUser);

        // Ensure that both user know each other
        profile1.addContactUserId(profile2.getUserId());
        profile2.addContactUserId(profile1.getUserId());

        UserProfileHelper.saveUserProfile(profile1);
        UserProfileHelper.saveUserProfile(profile2);
    }


    /**
     * Setup two user profiles with no contacts
     */
    private void setupSingleProfile()
    {
        // ensure that both user are registered

        UserProfile profile1 = new UserProfile(user);
        UserProfileHelper.saveUserProfile(profile1);
    }

    /**
     * Setup two user profiles with no contacts
     */
    private void setupProfiles()
    {
        // ensure that both user are registered

        UserProfile profile1 = new UserProfile(user);
        UserProfile profile2 = new UserProfile(otherUser);

        UserProfileHelper.saveUserProfile(profile1);
        UserProfileHelper.saveUserProfile(profile2);
    }

    private void setupProfilesWithPendingRequest()
    {
        UserProfile profile1 = new UserProfile(user);
        UserProfile profile2 = new UserProfile(otherUser);

        profile1.addPendingContactRequestUserId( profile2.getUserId() );
        profile2.addWaitingForConfirmationUserId(profile1.getUserId() );

        UserProfileHelper.saveUserProfile(profile1);
        UserProfileHelper.saveUserProfile(profile2);
    }


    @Test(expected = UnauthorizedException.class)
    public void testCreateAccount_WithoutUser() throws UnauthorizedException, InvalidUserException {
        NewAccountInfo accountInfo = new NewAccountInfo();
        accountInfo.setDisplayName("Me myself and I");
        accountInfo.setPhotoUrl("http://photo.jpg");

        WhereAreYouApi.StatusResult result = whereAreYouApi.createAccount(null,accountInfo);
    }

    @Test
    public void testRegisterDevice() throws UnauthorizedException, InvalidUserException {

        setupSingleProfile();

        WhereAreYouApi.StatusResult result = whereAreYouApi.registerDevice(user, REGISTRATION_TOKEN );

        assertEquals(result.getResultCode(), whereAreYouApi.RESULT_OK);

        UserProfile userProfile = whereAreYouApi.getUserProfile(user);

        assertEquals(userProfile.getRegId(), REGISTRATION_TOKEN.getToken());
    }

    @Test
    public void testSendLocation() throws Exception {

        setupProfilesWithContacts();

        Location loc = new Location();
        loc.setLatitude(0);
        loc.setLongitude(0);
        String message = "Hello!";
        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.sendLocation(user, otherUser.getUserId(), loc, message);

        assertEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_OK);
    }

    @Test
    public void testSendLocation_nullMessage() throws Exception {

        setupProfilesWithContacts();

        Location loc = new Location();
        loc.setLatitude(0);
        loc.setLongitude(0);

        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.sendLocation(user, otherUser.getUserId(), loc, null);

        assertEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_OK);
    }

    @Test(expected = InvalidUserException.class)
    public void testSendLocation_invalidContact() throws Exception {

        setupProfiles();

        Location loc = new Location();
        loc.setLatitude(0);
        loc.setLongitude(0);
        String message = "Hello!";
        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.sendLocation(user, otherUser.getUserId(), loc, message);

    }

    @Test
    public void testSendLocation_contactUnsubscribed() throws Exception {

        setupProfilesWithContacts();
        UserProfileHelper.deleteUserProfile(otherUser.getUserId());

        Location loc = new Location();
        loc.setLatitude(0);
        loc.setLongitude(0);
        String message = "Hello!";
        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.sendLocation(user, otherUser.getUserId(), loc, message);

        assertEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_USER_UNSUBSCRIBED);

        UserProfile profile1 = whereAreYouApi.getUserProfile(user);

        assertFalse(profile1.containsContactUserId(otherUser.getUserId()));
        assertFalse(profile1.containsPendingContactRequestUserId(otherUser.getUserId()));
        assertFalse(  profile1.containsWaitingForConfirmationUserId(otherUser.getUserId()));
    }

    @Test
    public void testSendContactRequest() throws Exception {
        setupProfiles();

        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.sendContactRequest(user, otherUser.getEmail());

        assertEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_OK);

        // Get the updated  profiles
        UserProfile profile1 = whereAreYouApi.getUserProfile(user);
        UserProfile profile2 = whereAreYouApi.getUserProfile(otherUser);

        assertTrue(profile1.containsPendingContactRequestUserId(profile2.getUserId()));
        assertFalse(profile2.containsPendingContactRequestUserId(profile1.getUserId()));

        assertFalse(profile1.containsWaitingForConfirmationUserId(profile2.getUserId()));
        assertTrue(profile2.containsWaitingForConfirmationUserId(profile1.getUserId()));

        assertFalse(profile1.containsContactUserId(profile2.getUserId()));
        assertFalse(profile2.containsContactUserId(profile1.getUserId()));
    }

    @Test
    public void testSendContactRequest_UnknownUser() throws Exception {
        UserProfile profile1 = new UserProfile(user);
        UserProfileHelper.saveUserProfile(profile1);

        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.sendContactRequest(user, "unknown@gmail.com");
        assertEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_NO_USER_WITH_EMAIL);
    }

    @Test(expected = InvalidUserException.class)
    public void testSendContactRequest_sameUser() throws Exception {
        UserProfile profile1 = new UserProfile(user);
        UserProfileHelper.saveUserProfile(profile1);

        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.sendContactRequest(user, user.getEmail());
    }

    @Test
    public void testSendContactRequest_pending() throws Exception {
        setupProfilesWithPendingRequest();

        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.sendContactRequest(user, otherUser.getEmail());

        assertEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_CONTACT_REQUEST_PENDING);

        // Get the updated  profiles
        UserProfile profile1 = whereAreYouApi.getUserProfile(user);
        UserProfile profile2 = whereAreYouApi.getUserProfile(otherUser);

        assertTrue(profile1.containsPendingContactRequestUserId(profile2.getUserId()));
        assertFalse(profile2.containsPendingContactRequestUserId(profile1.getUserId()));

        assertFalse(profile1.containsWaitingForConfirmationUserId(profile2.getUserId()));
        assertTrue(profile2.containsWaitingForConfirmationUserId(profile1.getUserId()));

        assertFalse(profile1.containsContactUserId(profile2.getUserId()));
        assertFalse(profile2.containsContactUserId(profile1.getUserId()));
    }

    @Test
    public void testSendContactRequest_alreadyAdded() throws Exception {
        setupProfilesWithContacts();

        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.sendContactRequest(user, otherUser.getEmail());

        assertEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_CONTACT_ALREADY_ADDED);

        // Get the updated  profiles

        UserProfile profile1 = whereAreYouApi.getUserProfile(user);
        UserProfile profile2 = whereAreYouApi.getUserProfile(otherUser);

        assertFalse(profile1.containsPendingContactRequestUserId(profile2.getUserId()));
        assertFalse(profile2.containsPendingContactRequestUserId(profile1.getUserId()));
        assertFalse(profile1.containsWaitingForConfirmationUserId(profile2.getUserId()));
        assertFalse(profile2.containsWaitingForConfirmationUserId(profile1.getUserId()));

        assertTrue(profile1.containsContactUserId(profile2.getUserId()));
        assertTrue(profile2.containsContactUserId(profile1.getUserId()));
    }



    @Test
    public void testConfirmContactRequest() throws UnauthorizedException, IOException, InvalidUserException {
        setupProfilesWithPendingRequest();

        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.confirmContactRequest( otherUser, user.getUserId() );

        assertEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_OK);

        // Get the updated  profiles
        UserProfile profile1 = whereAreYouApi.getUserProfile(user);
        UserProfile profile2 = whereAreYouApi.getUserProfile(otherUser);

        assertTrue(profile1.containsContactUserId(profile2.getUserId()));
        assertTrue(profile2.containsContactUserId(profile1.getUserId()));

        assertFalse(profile1.containsWaitingForConfirmationUserId(profile2.getUserId()));
        assertFalse(profile2.containsWaitingForConfirmationUserId(profile1.getUserId()));
        assertFalse(profile1.containsPendingContactRequestUserId(profile2.getUserId()));
        assertFalse(profile2.containsPendingContactRequestUserId(profile1.getUserId()));
    }

    @Test
    public void testConfirmContactRequest_noPending() throws UnauthorizedException, IOException, InvalidUserException {
        setupProfiles();

        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.confirmContactRequest( otherUser, user.getUserId() );

        assertEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_NO_PENDING_REQUEST);

        // Get the updated  profiles
        UserProfile profile1 = whereAreYouApi.getUserProfile(user);
        UserProfile profile2 = whereAreYouApi.getUserProfile(otherUser);

        assertFalse(profile1.containsContactUserId(profile2.getUserId()));
        assertFalse(profile2.containsContactUserId(profile1.getUserId()));
        assertFalse(profile1.containsWaitingForConfirmationUserId(profile2.getUserId()));
        assertFalse(profile2.containsWaitingForConfirmationUserId(profile1.getUserId()));
        assertFalse(profile1.containsPendingContactRequestUserId(profile2.getUserId()));
        assertFalse(profile2.containsPendingContactRequestUserId(profile1.getUserId()));

    }

    @Test
    public void testRemoveContact() throws UnauthorizedException, InvalidUserException {
        setupProfilesWithContacts();

        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.removeContact(user, otherUser.getUserId());
        assertEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_OK);

        // Get the updated  profiles
        UserProfile profile1 = whereAreYouApi.getUserProfile(user);
        UserProfile profile2 = whereAreYouApi.getUserProfile(otherUser);

        assertFalse(profile1.containsContactUserId(profile2.getUserId()));
        assertFalse(profile2.containsContactUserId(profile1.getUserId()));
        assertFalse(profile1.containsWaitingForConfirmationUserId(profile2.getUserId()));
        assertFalse(profile2.containsWaitingForConfirmationUserId(profile1.getUserId()));
        assertFalse(profile1.containsPendingContactRequestUserId(profile2.getUserId()));
        assertFalse(profile2.containsPendingContactRequestUserId(profile1.getUserId()));
    }

    @Test
    public void testRemoveContact_pending() throws UnauthorizedException, InvalidUserException {
        setupProfilesWithPendingRequest();

        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.removeContact(user, otherUser.getUserId());
        assertEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_OK);

        // Get the updated  profiles
        UserProfile profile1 = whereAreYouApi.getUserProfile(user);
        UserProfile profile2 = whereAreYouApi.getUserProfile(otherUser);

        assertFalse(profile1.containsContactUserId(profile2.getUserId()));
        assertFalse(profile2.containsContactUserId(profile1.getUserId()));
        assertFalse(profile1.containsWaitingForConfirmationUserId(profile2.getUserId()));
        assertFalse(profile2.containsWaitingForConfirmationUserId(profile1.getUserId()));
        assertFalse(profile1.containsPendingContactRequestUserId(profile2.getUserId()));
        assertFalse(profile2.containsPendingContactRequestUserId(profile1.getUserId()));
    }
    @Test
    public void testRemoveContact_notInList() throws UnauthorizedException, InvalidUserException {
        setupProfiles();

        WhereAreYouApi.StatusResult statusResult = whereAreYouApi.removeContact( user, otherUser.getUserId() );
        assertNotEquals(statusResult.getResultCode(), WhereAreYouApi.RESULT_OK);
        assertEquals(statusResult.getResultCode(),WhereAreYouApi.RESULT_NOT_IN_CONTACT);

        // Get the updated  profiles
        UserProfile profile1 = whereAreYouApi.getUserProfile(user);
        UserProfile profile2 = whereAreYouApi.getUserProfile(otherUser);

        assertFalse(profile1.containsContactUserId(profile2.getUserId()));
        assertFalse(profile2.containsContactUserId(profile1.getUserId()));
        assertFalse(profile1.containsWaitingForConfirmationUserId(profile2.getUserId()));
        assertFalse(profile2.containsWaitingForConfirmationUserId(profile1.getUserId()));
        assertFalse(profile1.containsPendingContactRequestUserId(profile2.getUserId()));
        assertFalse(profile2.containsPendingContactRequestUserId(profile1.getUserId()));
    }


}
