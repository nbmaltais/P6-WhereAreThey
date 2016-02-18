package ca.nbsoft.whereareyou.backend.api;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import ca.nbsoft.whereareyou.backend.data.UserProfile;

/**
 * Created by Nicolas on 17/02/2016.
 * Class to handle bots
 */
public class UserBot {

    private static final Logger log = Logger.getLogger(UserBot.class.getName());
    static public final String AUTH_DOMAIN="wherearethey.com";
    static public final String JOHNDOE_EMAIL = "john.doe@wherearethey.com";
    static public final String JANEDOE_EMAIL = "jane.doe@wherearethey.com";



    User mUser;
    WhereAreYouApi mApi;


    public UserBot(User user, WhereAreYouApi api)
    {
        mUser = user;
        mApi = api;
    }

    public UserBot(UserProfile contactProfile, WhereAreYouApi api) {
        mUser = new User(contactProfile.getEmail(),AUTH_DOMAIN);
        mApi = api;
    }

    public void handleContactRequest( String userId )
    {
        try {
            mApi.confirmContactRequest( mUser, userId, true );
        } catch (Exception e) {
            log.info("handleContactRequest Failed, e  = " + e.getMessage());
        }
    }


    public void handleRequestContactLocation(String userId, String message) {
        try {
            Location loc = randomLocation();
            String reply;
            if(message == null)
            {
                reply = "You never write anything... :(";
            }
            else
            {
                reply = "Why did you wrote me " + message + "???";
            }
            mApi.sendLocation(mUser, userId, loc, "Your message was: " + reply);
        } catch (Exception e) {
            log.info("handleRequestContactLocation Failed, e  = " + e.getMessage());
        }
    }

    public void handleSendLocation(String userId, String message) {
        try {
            if( message != null )
            {
                if(message.equals("Ask me"))
                {
                    mApi.requestContactLocation(mUser,userId,"Hey, where are you?");
                }
            }
        } catch (Exception e) {
            log.info("handleSendLocation Failed, e  = " + e.getMessage());
        }
    }

    static final List<Location>  sLocations;
    static
    {
        sLocations = new ArrayList<>();
        sLocations.add(new Location(40.7142700,-74.0059700)); // New york
        sLocations.add(new Location(48.8534100,2.3488000)); // Paris
        sLocations.add(new Location(35.6895000,139.6917100 )); // Tokyo
        sLocations.add(new Location(21.315603, -157.858093)); // Honolulu
    }

    private Location randomLocation() {

        int max = sLocations.size();

        int i = ThreadLocalRandom.current().nextInt(0, max);

        Location loc = sLocations.get(i);

        return loc;
    }


}
