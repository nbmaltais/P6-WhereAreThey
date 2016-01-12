package ca.nbsoft.whereareyou.gcm;

import com.google.android.gms.iid.InstanceIDListenerService;

import ca.nbsoft.whereareyou.ApiService;

public class MyInstanceIDListenerService extends InstanceIDListenerService {
    public MyInstanceIDListenerService() {
    }

    @Override
    public void onTokenRefresh() {
        ApiService.registerDevice(this);
    }
}
