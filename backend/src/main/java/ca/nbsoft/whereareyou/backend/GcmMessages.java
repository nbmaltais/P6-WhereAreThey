package ca.nbsoft.whereareyou.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.logging.Logger;

import WhereAreYou.BuildConfig;
import ca.nbsoft.whereareyou.backend.data.UserProfile;

/**
 * Created by Nicolas on 2015-12-10.
 */
public class GcmMessages {
    private static final Logger log = Logger.getLogger(GcmMessages.class.getName());
    private static final String API_KEY = BuildConfig.SERVER_API_KEY;//System.getProperty("gcm.api.key");


    static public void sendLocationRequest( UserProfile from, UserProfile to, String message ) throws IOException {

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData("type","location-request")
                .addData("from", from.getUserId())
                .addData("message", message)
                .build();

        Result result = sender.send(msg, to.getRegId(), 5);

        handleResult(result,to.getRegId());

    }

    public static void sendLocation(UserProfile from, UserProfile to, String location, String message) throws IOException {
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData("type","location")
                .addData("from", from.getUserId())
                .addData("location", location)
                .addData("message", message)
                .build();

        Result result = sender.send(msg, to.getRegId(), 5);

        handleResult(result,to.getRegId());
    }


    public static void sendContactRequest(UserProfile from, UserProfile to) throws IOException {
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData("type", "contact-request")
                .addData("from", from.getUserId())
                .addData("user-email", from.getEmail())
                .build();

        Result result = sender.send(msg, to.getRegId(), 5);

        handleResult(result,to.getRegId());
    }

    public static void confirmContactRequest(UserProfile from, UserProfile to) throws IOException {
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData("type", "contact-confirmation")
                .addData("from", from.getUserId())
                .addData("user-email", from.getEmail())
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
