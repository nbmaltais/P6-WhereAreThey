package ca.nbsoft.whereareyou.gcm;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.BuildConfig;
import ca.nbsoft.whereareyou.Endpoints;
import ca.nbsoft.whereareyou.Utility.PreferenceUtils;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.StringResult;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class RegistrationIntentService extends IntentService {

    static final String TAG = RegistrationIntentService.class.getSimpleName();
    static final public String EXTRA_REG_ID = "REG_ID";
    static final public String EXTRA_RESULT_RECEIVER = "RESULT_RECEIVER";
    public static final String RESULT_EXTRA = "RESULT";
    public static final int RESULT_FAILED = 0;
    public static final int RESULT_SUCCEEDED = 1;
    public static final String RESULT_EVENT = "ca.nbsoft.whereareyou.gcm.RegistrationIntentService.RESULT";


    public static void registerDevice(Context ctx) {
        registerDevice(ctx, null, null);
    }

    public static void registerDevice(Context ctx, ResultReceiver callback) {
        registerDevice(ctx, callback, null);
    }

    public static void registerDevice(Context ctx, ResultReceiver callback, String regId) {
        Log.d(TAG, "Starting registration servive.");
        Intent intent = new Intent(ctx, RegistrationIntentService.class);
        intent.putExtra(EXTRA_REG_ID, regId);
        if (callback != null)
            intent.putExtra(EXTRA_RESULT_RECEIVER, callback);
        ctx.startService(intent);
    }

    public static boolean registerDeviceIfNeeded(Context ctx, ResultReceiver callback, String regId) {
        boolean regIdSent = PreferenceUtils.getSentRegistrationToBackend(ctx);
        String userId = PreferenceUtils.getUserId(ctx);
        if (regIdSent == false || userId == null) {
            registerDevice(ctx, callback, regId);
            return true;
        } else {
            Log.d(TAG, "Device is already registered.");
            return false;
        }
    }

    public static boolean registerDeviceIfNeeded(Context ctx, ResultReceiver callback) {
        return registerDeviceIfNeeded(ctx, callback, null);
    }

    public static boolean registerDeviceIfNeeded(Context ctx) {
        return registerDeviceIfNeeded(ctx, null, null);
    }

    public static void subscribeToResult(Context ctx, BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(ctx).registerReceiver(receiver,
                new IntentFilter(RESULT_EVENT));
    }

    public static void unSubscribeFromResult(Context ctx, BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(receiver);
    }


    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String token = intent.getStringExtra(EXTRA_REG_ID);
        ResultReceiver callback = null;

        if (intent.hasExtra(EXTRA_RESULT_RECEIVER)) {
            callback = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
        }

        try {
            // get gcm token for this app
            if (token == null) {
                Log.d(TAG, "Getting registration token");
                InstanceID instanceID = InstanceID.getInstance(this);
                token = instanceID.getToken(BuildConfig.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                Log.i(TAG, "GCM Registration Token: " + token);
            }

            // Send  registration to backend.
            Log.d(TAG, "Registering device with backend");

            GoogleAccountCredential credential = Endpoints.getCredential(this);
            if (credential.getSelectedAccountName() == null) {
                Log.w(TAG, "No account has been selected");
            }
            StringResult result = GcmUtils.registerDevice(token, credential);

            Log.d(TAG, "Writing preference");
            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            PreferenceUtils.setSentRegistrationToBackend(this, true);
            PreferenceUtils.setUserId(this, result.getResult());

            sendRegistrationConfirmation(RESULT_SUCCEEDED);
            if (callback != null) {
                callback.send(RESULT_SUCCEEDED, null);
            }

            // Update contact list after registration
            ApiService.updateContactList(this);

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            PreferenceUtils.setSentRegistrationToBackend(this, false);

            if (callback != null) {
                callback.send(RESULT_FAILED, null);
            }
            sendRegistrationConfirmation(RESULT_FAILED);
        }
    }

    private void sendRegistrationConfirmation(int result) {
        Intent intent = new Intent(RESULT_EVENT);
        intent.putExtra(RESULT_EXTRA, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    /**
     * Created by Nicolas on 2015-12-21.
     * Broadcast receiver to receive result from RegistrationIntentService
     */
    abstract public static class RegistrationResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra(RESULT_EXTRA, -1);
            if (result == RESULT_SUCCEEDED) {
                onLoginSucceeded();
            } else if (result == RESULT_FAILED) {
                onLoginFailed();
            }
        }

        protected abstract void onLoginFailed();

        protected abstract void onLoginSucceeded();
    }
}
