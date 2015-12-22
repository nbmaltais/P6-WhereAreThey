package ca.nbsoft.whereareyou;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by Nicolas on 2015-11-29.
 */
public class PlayServicesUtils {

    private static final String TAG = PlayServicesUtils.class.getSimpleName();
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static class PlayServicesNotSupported extends Exception
    {

    }

    static public boolean checkPlayServices(Activity ctx) throws PlayServicesNotSupported {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(ctx);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(ctx, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                throw new PlayServicesNotSupported();
            }
            return false;
        }
        return true;
    }


}
