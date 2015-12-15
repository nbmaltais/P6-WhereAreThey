package ca.nbsoft.whereareyou.gcm;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;

import ca.nbsoft.whereareyou.Endpoints;
import ca.nbsoft.whereareyou.backend.registration.Registration;
import ca.nbsoft.whereareyou.backend.registration.model.RegistrationId;


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
    static public void registerDevice( String token, GoogleAccountCredential credential) throws IOException {

        Registration regService = Endpoints.getRegistrationEndpoint(credential);

        RegistrationId r = new RegistrationId();
        r.setToken(token);

        regService.register(r).execute();
    }




}
