package ca.nbsoft.whereareyou;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import ca.nbsoft.whereareyou.backend.whereAreYou.WhereAreYou;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.ContactInfo;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.ContactInfoCollection;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.Location;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.StatusResult;
import ca.nbsoft.whereareyou.provider.WhereRUProvider;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.contact.ContactContentValues;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ApiService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final String TAG = ApiService.class.getSimpleName();

    public static final String ACTION_REQUEST_LOCATION = "ca.nbsoft.whereareyou.action.REQUEST_LOCATION";
    public static final String ACTION_SEND_CONTACT_REQUEST = "ca.nbsoft.whereareyou.action.SEND_CONTACT_REQUEST";
    public static final String ACTION_CONFIRM_CONTACT_REQUEST = "ca.nbsoft.whereareyou.action.CONFIRM_CONTACT_REQUEST";
    public static final String ACTION_SEND_LOCATION = "ca.nbsoft.whereareyou.action.SEND_LOCATION";
    public static final String ACTION_UPDATE_CONTACT_LIST = "ca.nbsoft.whereareyou.action.UPDATE_CONTACT_LIST";


    public static final class StatusCode
    {
        public static final int RESULT_OK = 0;
        public static final int RESULT_NOT_REGISTERED = -1;
        public static final int RESULT_CONTACT_ALREADY_ADDED = -2;
        public static final int RESULT_CONTACT_REQUEST_PENDING = -3;
        public static final int RESULT_NO_PENDING_REQUEST = -4;
        public static final int RESULT_NOT_IN_CONTACT = -5;
        public static final int RESULT_USER_UNSUBSCRIBED = -6;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef ({ACTION_REQUEST_LOCATION,
            ACTION_SEND_CONTACT_REQUEST,
            ACTION_CONFIRM_CONTACT_REQUEST,
            ACTION_SEND_LOCATION,
            ACTION_UPDATE_CONTACT_LIST})
    public @interface ActionName {}

    public static class Result implements Parcelable {


        public static final int RESULT_SUCCESS =0;
        public static final int RESULT_FAILURE =-1;
        public static final int RESULT_DB_ERROR = -2;
        public static final int RESULT_INVALID_ACTION =-3;
        public static final int RESULT_BACKEND_ERROR_PERMISSION =-4;
        public static final int RESULT_BACKEND_ERROR_INVALID_USER =-5;
        public static final int RESULT_BACKEND_ERROR_STATUSCODE = -6;
        @Retention(RetentionPolicy.SOURCE)
        @IntDef({RESULT_SUCCESS,
                RESULT_FAILURE,
                RESULT_DB_ERROR,
                RESULT_INVALID_ACTION,
                RESULT_BACKEND_ERROR_PERMISSION,
                RESULT_BACKEND_ERROR_INVALID_USER,
                RESULT_BACKEND_ERROR_STATUSCODE})
        public @interface ResultCode {}


        int resultCode;
        int specificResultCode;

        public static Result from(@ResultCode int rc)
        {
            return new Result(rc);
        }

        public static Result from(StatusResult result)
        {
            if(result.getResultCode()==StatusCode.RESULT_OK)
                return new Result(RESULT_SUCCESS);
            else
                return new Result(RESULT_BACKEND_ERROR_STATUSCODE,result.getResultCode());
        }

        public Result(@ResultCode int rc){
            resultCode=rc;
            specificResultCode=0;
        }

        public Result(@ResultCode int rc, int specifirRc){
            resultCode=rc;
            specificResultCode=specifirRc;
        }

        public int getResultCode() {
            return resultCode;
        }

        public int getSpecificResultCode() {
            return specificResultCode;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.resultCode);
            dest.writeInt(this.specificResultCode);
        }

        protected Result(Parcel in) {
            this.resultCode = in.readInt();
            this.specificResultCode = in.readInt();
        }

        public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
            public Result createFromParcel(Parcel source) {
                return new Result(source);
            }

            public Result[] newArray(int size) {
                return new Result[size];
            }
        };
    }


    public static final String EXTRA_CONTACT_EMAIL = "ca.nbsoft.whereareyou.extra.EMAIL";
    public static final String EXTRA_CANCEL_NOTIFICATION = "ca.nbsoft.whereareyou.extra.CANCEL_NOTIFICATION";


    private WhereAreYou mApi;
    private GoogleApiClient mGoogleApiClient;
    private boolean mGoogleApiConnected = false;
    private boolean mGoogleApiInitDone = false;
    private final Object mGoogleApiLock = new Object();

    /**
     * BroadcastReceiver that should be extended to receive completion results
     */
    public static class ResultBroadcastReceiver  extends BroadcastReceiver
    {

        public static final String EXTRA_RESULT_CODE = "RESULT_CODE";

        public static void sendResult(Context ctx,@ActionName String action, Result result)
        {
            Intent intent = new Intent(action);
            intent.putExtra(EXTRA_RESULT_CODE, result);
            LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Result result = intent.getParcelableExtra(EXTRA_RESULT_CODE);
            switch( intent.getAction()) {
                case ACTION_REQUEST_LOCATION:
                    onRequestLocationResult(result);
                    break;
                case ACTION_SEND_LOCATION:
                    onSendLocationResult(result);
                    break;
                case ACTION_UPDATE_CONTACT_LIST:
                    onUpdateContactListResult(result);
                    break;
                case ACTION_SEND_CONTACT_REQUEST:
                    onSendContactRequestResult(result);
                    break;
                case ACTION_CONFIRM_CONTACT_REQUEST:
                    onConfirmContactRequestResult(result);
                    break;
            }
        }

        public void onConfirmContactRequestResult(Result resultCode ) {

        }

        public void onSendContactRequestResult(Result resultCode) {
            
        }

        public void onUpdateContactListResult(Result resultCode) {
            
        }

        public void onSendLocationResult(Result resultCode) {
            
        }

        public void onRequestLocationResult(Result resultCode) {
            
        }
    }


    public ApiService() {
        super("ApiService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GoogleAccountCredential credential = Endpoints.getCredential(this);
        mApi = Endpoints.getApiEndpoint(credential);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    public static void subscribeToResult(Context ctx, @ActionName String action, BroadcastReceiver receiver)
    {
        LocalBroadcastManager.getInstance(ctx).registerReceiver(receiver, new IntentFilter(action));
    }

    public static void subscribeToResult(Context ctx, List<String> actions, BroadcastReceiver receiver)
    {
        IntentFilter filter = new IntentFilter();
        for( String action : actions) {
            filter.addAction(action);
        }
        LocalBroadcastManager.getInstance(ctx).registerReceiver(receiver, filter );
    }

    public static void subscribeToResult(Context ctx,  BroadcastReceiver receiver)
    {
        IntentFilter filter = new IntentFilter();


        filter.addAction(ACTION_REQUEST_LOCATION);
        filter.addAction(ACTION_SEND_CONTACT_REQUEST);
        filter.addAction(ACTION_CONFIRM_CONTACT_REQUEST);
        filter.addAction(ACTION_SEND_LOCATION);
        filter.addAction(ACTION_UPDATE_CONTACT_LIST);

        LocalBroadcastManager.getInstance(ctx).registerReceiver(receiver, filter);
    }

    public static void unSubscribeFromResult(Context ctx, BroadcastReceiver receiver)
    {
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(receiver);
    }

    public static void requestContactLocation(Context context, @NonNull String contactId, @NonNull String message) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_REQUEST_LOCATION);
        intent.putExtra(Constants.EXTRA_CONTACT_USER_ID, contactId);
        intent.putExtra(Constants.EXTRA_MESSAGE, message);
        context.startService(intent);
    }

    public static void sendContactRequest(Context context, @NonNull String contactEmail) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_SEND_CONTACT_REQUEST);
        intent.putExtra(EXTRA_CONTACT_EMAIL, contactEmail);
        context.startService(intent);
    }

    public static Intent sendLocationIntent(Context context, @NonNull String contactId,  String message) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_SEND_LOCATION);
        intent.putExtra(Constants.EXTRA_CONTACT_USER_ID, contactId);
        intent.putExtra(Constants.EXTRA_MESSAGE, message);
        return intent;
    }

    public static void sendLocation(Context context, @NonNull String contactId, String message) {
        context.startActivity(sendLocationIntent(context, contactId, message));
    }

    public static Intent confirmContactRequestIntent(Context context, @NonNull String contactUserId) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_CONFIRM_CONTACT_REQUEST);
        intent.putExtra(Constants.EXTRA_CONTACT_USER_ID, contactUserId);
        return intent;
    }

    public static void confirmContactRequest(Context context, @NonNull String contactUserId) {
        context.startService(confirmContactRequestIntent(context, contactUserId));
    }

    public static void updateContactList(Context context) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_UPDATE_CONTACT_LIST);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            // TODO: add definitice action to a pending queue in case the operation fails.
            // The pending queue should be processed later
            //

            @ActionName final String action = intent.getAction();
            final String userId;
            final String message;
            Result resultCode = new Result(Result.RESULT_INVALID_ACTION);
            try {
                // Cancel the associated notification
                if (intent.hasExtra(EXTRA_CANCEL_NOTIFICATION)) {
                    int id = intent.getIntExtra(EXTRA_CANCEL_NOTIFICATION, 0);
                    NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notifyMgr.cancel(id);
                }

                switch (action) {
                    case ACTION_REQUEST_LOCATION:
                        userId = intent.getStringExtra(Constants.EXTRA_CONTACT_USER_ID);
                        message = intent.getStringExtra(Constants.EXTRA_MESSAGE);
                        resultCode = handleRequestContactLocation(userId, message);
                        break;
                    case ACTION_SEND_CONTACT_REQUEST:
                        final String email = intent.getStringExtra(EXTRA_CONTACT_EMAIL);
                        resultCode = handleSendContactRequest(email);
                        break;
                    case ACTION_UPDATE_CONTACT_LIST:
                        resultCode = handleUpdateContactList();
                        break;
                    case ACTION_CONFIRM_CONTACT_REQUEST:
                        userId = intent.getStringExtra(Constants.EXTRA_CONTACT_USER_ID);
                        resultCode = handleConfirmContactRequest(userId);
                        break;
                    case ACTION_SEND_LOCATION:
                        userId = intent.getStringExtra(Constants.EXTRA_CONTACT_USER_ID);
                        message = intent.getStringExtra(Constants.EXTRA_MESSAGE);
                        resultCode = handleSendLocation(userId, message);
                        break;
                }

                ResultBroadcastReceiver.sendResult(this,action,resultCode);

                // TODO: remove action from pending queue upon sucess

            } catch (IOException e) {
                Log.e(TAG, "Error while performing action :" + action, e);
                ResultBroadcastReceiver.sendResult(this, action,  Result.from(Result.RESULT_FAILURE));
                // TODO: report error to UI
            } catch (InterruptedException e) {
                Log.e(TAG, "Error while performing action :" + action + ". Google API was not connected", e);
                ResultBroadcastReceiver.sendResult(this, action,  Result.from(Result.RESULT_FAILURE));
            }

        }
    }

    private Result handleSendLocation(@NonNull String userId, @NonNull String message) throws IOException, InterruptedException {
        Log.d(TAG, "Sending location to " + userId);

        Location loc = null;

        // TODO ask permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.w(TAG,"User has not grated permission for location");
            return  Result.from(Result.RESULT_BACKEND_ERROR_PERMISSION);
        }
        else {

            waitForGoogleApi();

            android.location.Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            loc = new Location();
            loc.setLongitude(lastLocation.getLongitude());
            loc.setLatitude(lastLocation.getLatitude());
        }

        showToast("Sending location to " + userId);

        WhereAreYou.SendLocation sendLocation = mApi.sendLocation(userId, loc);
        if(message!=null)
            sendLocation.setMessage(message);

        StatusResult result = sendLocation.execute();

        return Result.from(result);
    }

    private Result handleConfirmContactRequest(@NonNull String userId) throws IOException {
        Log.d(TAG, "Confirming contact request with " + userId);
        showToast("Confirming contact request with " + userId);

        StatusResult result = mApi.confirmContactRequest(userId).execute();

        return Result.from(result);
    }

    private Result handleUpdateContactList() throws IOException {
        Log.d(TAG, "handleUpdateContactList");

        ContactInfoCollection contacts = mApi.getContacts().execute();


        // TODO: do a merge instead of nuking all contacts and recreating them

        getContentResolver().delete(ContactColumns.CONTENT_URI, null, null);

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        List<ContactInfo> items = contacts.getItems();
        if(items!=null) {
            for (ContactInfo contact : items) {
                ContactContentValues values = new ContactContentValues();

                values.putUserid(contact.getUserId());
                values.putEmail(contact.getEmail());

                batch.add(ContentProviderOperation.newInsert(ContactColumns.CONTENT_URI).withValues(values.values()).build());
            }
        }

        try {
            getContentResolver().applyBatch(WhereRUProvider.AUTHORITY, batch);
            return Result.from(Result.RESULT_SUCCESS);
        } catch (RemoteException e) {
            Log.e(TAG,"Contact DB update failed",e);
            return Result.from(Result.RESULT_DB_ERROR);
        } catch (OperationApplicationException e) {
            Log.e(TAG, "Contact DB update failed", e);
            return Result.from(Result.RESULT_DB_ERROR);
        }

    }

    private Result  handleSendContactRequest(@NonNull String email)throws IOException {
        Log.d(TAG, "handleSendContactRequest");

        StatusResult result = mApi.sendContactRequest(email).execute();

        return Result.from(result);
    }

    private Result handleRequestContactLocation(@NonNull String userId, String message)throws IOException {
        Log.d(TAG, "handleRequestContactLocation, userId = " + userId);

        WhereAreYou.RequestContactLocation requestContactLocation = mApi.requestContactLocation(userId);
        if(message!=null)
            requestContactLocation.setMessage(message);
        StatusResult result = requestContactLocation.execute();


        return Result.from(result);
    }



    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Google API onConnected");
        synchronized (mGoogleApiLock) {
            mGoogleApiConnected = true;
            mGoogleApiInitDone = true;
            mGoogleApiLock.notify();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Google API onConnectionFailed");
        synchronized (mGoogleApiLock) {
            mGoogleApiConnected = false;
            mGoogleApiInitDone = true;
            mGoogleApiLock.notify();
        }
    }

    private void waitForGoogleApi() throws InterruptedException {
        synchronized (mGoogleApiLock) {
            if(mGoogleApiInitDone==false) {
                Log.i(TAG,"Waiting for google api to connect");
                mGoogleApiLock.wait();
                Log.i(TAG, "Google api connected");
            }
        }
    }
}
