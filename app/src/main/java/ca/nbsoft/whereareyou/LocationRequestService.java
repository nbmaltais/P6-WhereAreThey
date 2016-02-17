package ca.nbsoft.whereareyou;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service to request the user location.
 * Using getLastLocation is not precise enough so we subsribe to location update
 */
public class LocationRequestService extends Service {
    public LocationRequestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
