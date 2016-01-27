package ca.nbsoft.whereareyou.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.logging.Logger;

import WhereAreYou.BuildConfig;
import ca.nbsoft.whereareyou.backend.api.Location;
import ca.nbsoft.whereareyou.backend.data.UserProfile;
import ca.nbsoft.whereareyou.common.GcmMessageKeys;
import ca.nbsoft.whereareyou.common.GcmMessageTypes;

/**
 * Created by Nicolas on 2015-12-10.
 */
public class GcmMessages {
    private static final Logger log = Logger.getLogger(GcmMessages.class.getName());
    private static final String API_KEY = BuildConfig.SERVER_API_KEY;//System.getProperty("gcm.api.key");



    static public void sendLocationRequest( UserProfile from, UserProfile to, String message ) throws IOException {

        if(to.getRegId()==null)
        {
            log.info("sendLocationRequest: user has no registered device");
            return;
        }

        Sender sender = new Sender(API_KEY);
        Message.Builder builder = new Message.Builder()
                .addData(GcmMessageKeys.KEY_TYPE, GcmMessageTypes.LOCATION_REQUEST)
                .addData(GcmMessageKeys.KEY_USER_ID, to.getEmail())
                .addData(GcmMessageKeys.KEY_CONTACT_ID, from.getUserId());

        if(message!=null)
            builder.addData(GcmMessageKeys.KEY_MESSAGE, message);

        Message msg = builder.build();
        Result result = sender.send(msg, to.getRegId(), 5);

        handleResult(result,to.getRegId());

    }

    public static void sendLocation(UserProfile from, UserProfile to, Location location, String message) throws IOException {
        Sender sender = new Sender(API_KEY);

        if(to.getRegId()==null)
        {
            log.info("sendLocation: user has no registered device");
            return;
        }

        Message.Builder builder = new Message.Builder()
                .addData(GcmMessageKeys.KEY_TYPE, GcmMessageTypes.LOCATION)
                .addData(GcmMessageKeys.KEY_USER_ID, to.getEmail())
                .addData(GcmMessageKeys.KEY_CONTACT_ID, from.getUserId());

        if(message!=null)
            builder.addData(GcmMessageKeys.KEY_MESSAGE, message);

        if(location!=null) {
            builder.addData(GcmMessageKeys.KEY_LATITUDE, Double.toString(location.getLatitude()))
                    .addData(GcmMessageKeys.KEY_LONGITUDE, Double.toString(location.getLongitude()));
        }

        Message msg = builder.build();

        Result result = sender.send(msg, to.getRegId(), 5);

        handleResult(result,to.getRegId());
    }


    public static void sendContactRequest(UserProfile from, UserProfile to) throws IOException {

        if(to.getRegId()==null)
        {
            log.info("sendContactRequest: user has no registered device");
            return;
        }


        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData(GcmMessageKeys.KEY_TYPE, GcmMessageTypes.CONTACT_REQUEST)
                .addData(GcmMessageKeys.KEY_USER_ID, to.getEmail())
                .addData(GcmMessageKeys.KEY_CONTACT_ID, from.getUserId())
                .addData(GcmMessageKeys.KEY_CONTACT_EMAIL, from.getEmail())
                .addData(GcmMessageKeys.KEY_CONTACT_NAME, from.getDisplayName())
                .build();

        Result result = sender.send(msg, to.getRegId(), 5);

        handleResult(result,to.getRegId());
    }

    public static void confirmContactRequest(UserProfile confirmedContact, UserProfile to) throws IOException {

        if(to.getRegId()==null)
        {
            log.info("confirmContactRequest: user has no registered device");
            return;
        }

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData(GcmMessageKeys.KEY_TYPE, GcmMessageTypes.CONTACT_CONFIRMATION)
                .addData(GcmMessageKeys.KEY_USER_ID, to.getEmail())
                .addData(GcmMessageKeys.KEY_CONTACT_ID, confirmedContact.getUserId())
                .addData(GcmMessageKeys.KEY_CONTACT_EMAIL, confirmedContact.getEmail())
                .addData(GcmMessageKeys.KEY_CONTACT_NAME, confirmedContact.getDisplayName())
                .build();

        Result result = sender.send(msg, to.getRegId(), 5);

        handleResult(result,to.getRegId());
    }

    public static void notifyContactListModified(  UserProfile to ) throws IOException {
        if(to.getRegId()==null)
        {
            log.info("confirmContactRequest: user has no registered device");
            return;
        }

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData(GcmMessageKeys.KEY_TYPE, GcmMessageTypes.CONTACTS_MODIFIED)
                .addData(GcmMessageKeys.KEY_USER_ID, to.getEmail())
                .build();

        Result result = sender.send(msg, to.getRegId(), 5);

        handleResult(result,to.getRegId());

    }

    private static void handleResult(Result result, String toRegId) {
        if (result.getMessageId() != null)
        {
            log.info("Message sent to " + toRegId);
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // if the regId changed, we have to update the datastore
                /*log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
                record.setRegId(canonicalRegId);
                ofy().save().entity(record).now();*/
            }
        }
        else
        {
            String error = result.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                log.warning("Registration Id " + toRegId + " no longer registered with GCM, removing from datastore");
                // if the device is no longer registered with Gcm, remove it from the datastore
                //ofy().delete().entity(record).now();
            }
            else
            {
                log.warning("Error when sending message : " + error);
            }
        }
    }


}
