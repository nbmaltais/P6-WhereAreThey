package ca.nbsoft.whereareyou.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.logging.Logger;

import WhereAreYou.BuildConfig;

/**
 * Created by Nicolas on 2015-12-10.
 */
public class GcmMessages {
    private static final Logger log = Logger.getLogger(GcmMessages.class.getName());
    private static final String API_KEY = BuildConfig.SERVER_API_KEY;//System.getProperty("gcm.api.key");

    static public void sendLocationReply()
    {

    }


    static public void sendLocationRequest( String from, String toRegId, String message ) throws IOException {

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData("type","location-request")
                .addData("from", from)
                .addData("message", message)
                .build();

        Result result = sender.send(msg, toRegId, 5);

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
