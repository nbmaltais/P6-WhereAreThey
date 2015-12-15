package ca.nbsoft.whereareyou.gcm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = GcmListenerService.class.getSimpleName();

    public MyGcmListenerService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle message) {
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        showToast(message.getString("message"));

    }

    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
