package ca.nbsoft.whereareyou.gcm;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;

import ca.nbsoft.whereareyou.Endpoints;
import ca.nbsoft.whereareyou.backend.whereAreYou.WhereAreYou;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.RegistrationId;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.StringResult;


/**
 * Created by Nicolas on 2015-11-29.
 */
public class GcmUtils {
    static final String TAG= GcmUtils.class.getSimpleName();
    /**
     * Register device with GCM and backend
     * @param token
     * @throws IOException
     */
    static public StringResult registerDevice( String token, GoogleAccountCredential credential) throws IOException {

        WhereAreYou apiEndpoint = Endpoints.getApiEndpoint(credential);

        RegistrationId r = new RegistrationId();
        r.setToken(token);

        return apiEndpoint.register(r).execute();
    }




}
