package ca.nbsoft.whereareyou;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import ca.nbsoft.whereareyou.Utility.PreferenceUtils;
import ca.nbsoft.whereareyou.backend.whereAreYou.WhereAreYou;


/**
 * Created by Nicolas on 2015-12-09.
 */
public class Endpoints {

    static final String TAG = Endpoints.class.getSimpleName();


    static public GoogleAccountCredential getCredential(Context ctx)
    {
        GoogleAccountCredential cred = GoogleAccountCredential.usingAudience(ctx, "server:client_id:" + BuildConfig.WEB_CLIENT_ID);
        String accountName = PreferenceUtils.getAccountName(ctx);
        if(accountName!=null)
        {
            cred.setSelectedAccountName(accountName);
        }

        return cred;
    }

    static public WhereAreYou getApiEndpoint(GoogleAccountCredential credential)
    {
        WhereAreYou.Builder builder = new WhereAreYou.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), credential)
                // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                // otherwise they can be skipped
                .setApplicationName("GCMTest")
                .setRootUrl(BuildConfig.BACKEND_URL)
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                            throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });

        WhereAreYou api = builder.build();

        return api;
    }
}
