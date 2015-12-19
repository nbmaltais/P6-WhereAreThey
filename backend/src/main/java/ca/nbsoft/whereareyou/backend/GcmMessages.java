package ca.nbsoft.whereareyou.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.datastore.GeoPt;

import java.io.IOException;
import java.util.logging.Logger;

import WhereAreYou.BuildConfig;
import ca.nbsoft.whereareyou.backend.api.Location;
import ca.nbsoft.whereareyou.backend.data.UserProfile;

/**
 * Created by Nicolas on 2015-12-10.
 */
public class GcmMessages {
    private static final Logger log = Logger.getLogger(GcmMessages.class.getName());
    private static final String API_KEY = BuildConfig.SERVER_API_KEY;//System.getProperty("gcm.api.key");

    private static final String KEY_TYPE = "type";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_MESSAGE = "message";

    static public void sendLocationRequest( UserProfile from, UserProfile to, String message ) throws IOException {

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData(KEY_TYPE,"location-request")
                .addData(KEY_USER_ID, from.getUserId())
                .addData(KEY_MESSAGE, message)
                .build();

        Result result = sender.send(msg, to.getRegId(), 5);

        handleResult(result,to.getRegId());

    }

    public static void sendLocation(UserProfile from, UserProfile to, Location location, String message) throws IOException {
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData(KEY_TYPE,"location")
                .addData(KEY_USER_ID, from.getUserId())
                .addData("location_lat", Double.toString(location.getLatitude()))
                .addData("location_long", Double.toString(location.getLongitude()))
                .addData(KEY_MESSAGE, message)
                .build();

        Result result = sender.send(msg, to.getRegId(), 5);

        handleResult(result,to.getRegId());
    }


    public static void sendContactRequest(UserProfile from, UserProfile to) throws IOException {
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData(KEY_TYPE, "contact-request")
                .addData(KEY_USER_ID, from.getUserId())
                .addData(KEY_USER_EMAIL, from.getEmail())
                .build();

        Result result = sender.send(msg, to.getRegId(), 5);

        handleResult(result,to.getRegId());
    }

    public static void confirmContactRequest(UserProfile from, UserProfile to) throws IOException {
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData(KEY_TYPE, "contact-confirmation")
                .addData(KEY_USER_ID, from.getUserId())
                .addData(KEY_USER_EMAIL, from.getEmail())
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
