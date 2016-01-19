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
import android.net.Uri;
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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import ca.nbsoft.whereareyou.Utility.MessagesUtils;
import ca.nbsoft.whereareyou.Utility.PreferenceUtils;
import ca.nbsoft.whereareyou.backend.whereAreYou.WhereAreYou;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.ContactInfo;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.ContactInfoCollection;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.Location;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.NewAccountInfo;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.RegistrationId;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.StatusResult;
import ca.nbsoft.whereareyou.common.StatusCode;
import ca.nbsoft.whereareyou.provider.WhereRUProvider;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.contact.ContactContentValues;
import ca.nbsoft.whereareyou.provider.contact.ContactSelection;

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
    public static final String ACTION_DELETE_CONTACT ="ca.nbsoft.whereareyou.action.DELETE_CONTACT";
    public static final String ACTION_DELETE_ACCOUNT ="ca.nbsoft.whereareyou.action.DELETE_ACCOUNT";
    public static final String ACTION_REGISTER_DEVICE ="ca.nbsoft.whereareyou.action._REGISTER_DEVICE";
    public static final String ACTION_CREATE_ACCOUNT ="ca.nbsoft.whereareyou.action.CREATE_ACCOUNT";


    @Retention(RetentionPolicy.SOURCE)
    @StringDef ({ACTION_REQUEST_LOCATION,
            ACTION_SEND_CONTACT_REQUEST,
            ACTION_CONFIRM_CONTACT_REQUEST,
            ACTION_SEND_LOCATION,
            ACTION_UPDATE_CONTACT_LIST,
            ACTION_DELETE_CONTACT,
            ACTION_DELETE_ACCOUNT,
            ACTION_REGISTER_DEVICE,
            ACTION_CREATE_ACCOUNT})
    public @interface ActionName {}

    public static class Result implements Parcelable {


        public static final int RESULT_SUCCESS =0;
        public static final int RESULT_FAILURE =-1;
        public static final int RESULT_DB_ERROR = -2;
        public static final int RESULT_INVALID_ACTION =-3;
        public static final int RESULT_BACKEND_ERROR_PERMISSION =-4;
        public static final int RESULT_BACKEND_ERROR_INVALID_USER =-5;
        public static final int RESULT_BACKEND_ERROR_STATUSCODE = -6;
        public static final int RESULT_ERROR_NO_NETWORK = -7;
        private static final int RESULT_NO_ACCOUNT = -8;
        @Retention(RetentionPolicy.SOURCE)
        @IntDef({RESULT_SUCCESS,
                RESULT_FAILURE,
                RESULT_DB_ERROR,
                RESULT_INVALID_ACTION,
                RESULT_BACKEND_ERROR_PERMISSION,
                RESULT_BACKEND_ERROR_INVALID_USER,
                RESULT_BACKEND_ERROR_STATUSCODE,
                RESULT_ERROR_NO_NETWORK,
                RESULT_NO_ACCOUNT})
        public @interface ResultCode {}


        int resultCode;
        int specificResultCode;

        public static Result from(@ResultCode int rc)
        {
            return new Result(rc);
        }

        public static Result from(StatusResult result)
        {
            if(result.getResultCode() >= StatusCode.RESULT_OK)
                return new Result(RESULT_SUCCESS, result.getResultCode());
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

        public boolean isOk()
        {
            return resultCode == RESULT_SUCCESS;
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
            Bundle args = intent.getExtras();
            switch( intent.getAction()) {
                case ACTION_REQUEST_LOCATION:
                    onRequestLocationResult(result,args);
                    break;
                case ACTION_SEND_LOCATION:
                    onSendLocationResult(result,args);
                    break;
                case ACTION_UPDATE_CONTACT_LIST:
                    onUpdateContactListResult(result,args);
                    break;
                case ACTION_SEND_CONTACT_REQUEST:
                    onSendContactRequestResult(result,args);
                    break;
                case ACTION_CONFIRM_CONTACT_REQUEST:
                    onConfirmContactRequestResult(result,args);
                    break;
                case ACTION_DELETE_CONTACT:
                    onDeleteContactResult(result,args);
                    break;
                case ACTION_DELETE_ACCOUNT:
                    onDeleteAccountResult(result,args);
                    break;
                case ACTION_REGISTER_DEVICE:
                    onRegisterDeviceResult(result,args);
                    break;
                case ACTION_CREATE_ACCOUNT:
                    onCreateAccountResult(result,args);
                    break;
            }
        }

        public void onCreateAccountResult(Result result, Bundle args) {

        }

        public void onRegisterDeviceResult(Result result, Bundle args) {

        }

        public void onDeleteAccountResult(Result result, Bundle args) {

        }

        public void onConfirmContactRequestResult(Result resultCode, Bundle args)  {

        }

        public void onSendContactRequestResult(Result resultCode, Bundle args) {

        }

        public void onUpdateContactListResult(Result resultCode, Bundle args) {

        }

        public void onSendLocationResult(Result resultCode, Bundle args) {

        }

        public void onRequestLocationResult(Result resultCode, Bundle args) {

        }

        public void onDeleteContactResult(Result resultCode, Bundle args) {

        }
    }

    public static final String EXTRA_CONTACT_EMAIL = "ca.nbsoft.whereareyou.extra.EMAIL";
    public static final String EXTRA_CANCEL_NOTIFICATION = "ca.nbsoft.whereareyou.extra.CANCEL_NOTIFICATION";
    public static final String EXTRA_DISPLAY_NAME="ca.nbsoft.whereareyou.extra.DISPLAY_NAME";
    public static final String EXTRA_PHOTO_URL="ca.nbsoft.whereareyou.extra.PHOTO_URL";

    private WhereAreYou mApi;
    private GoogleApiClient mGoogleApiClient;
    private boolean mGoogleApiConnected = false;
    private boolean mGoogleApiInitDone = false;
    private String mAccountName;
    private final Object mGoogleApiLock = new Object();




    public ApiService() {
        super("ApiService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GoogleAccountCredential credential = Endpoints.getCredential(this);
        mAccountName = credential.getSelectedAccountName();

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

    public static void subscribeToResult(Context ctx, String[] actions, BroadcastReceiver receiver)
    {
        IntentFilter filter = new IntentFilter();
        for( String action : actions) {
            filter.addAction(action);
        }
        LocalBroadcastManager.getInstance(ctx).registerReceiver(receiver, filter);
    }

    public static void subscribeToResult(Context ctx,  BroadcastReceiver receiver)
    {
        IntentFilter filter = new IntentFilter();


        filter.addAction(ACTION_REQUEST_LOCATION);
        filter.addAction(ACTION_SEND_CONTACT_REQUEST);
        filter.addAction(ACTION_CONFIRM_CONTACT_REQUEST);
        filter.addAction(ACTION_SEND_LOCATION);
        filter.addAction(ACTION_UPDATE_CONTACT_LIST);
        filter.addAction(ACTION_DELETE_CONTACT);
        filter.addAction(ACTION_DELETE_ACCOUNT);
        filter.addAction(ACTION_REGISTER_DEVICE);
        filter.addAction(ACTION_CREATE_ACCOUNT);

        LocalBroadcastManager.getInstance(ctx).registerReceiver(receiver, filter);
    }

    public static void unSubscribeFromResult(Context ctx, BroadcastReceiver receiver)
    {
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(receiver);
    }

    public static void createAccount(Context ctx, String displayName, Uri photoUrl) {
        Intent intent = new Intent(ctx, ApiService.class);
        intent.setAction(ACTION_CREATE_ACCOUNT);
        intent.putExtra(EXTRA_DISPLAY_NAME, displayName);
        intent.putExtra(EXTRA_PHOTO_URL, photoUrl != null ? photoUrl.toString() : "");
        ctx.startService(intent);
    }

    public static void registerDevice(Context ctx) {
        Log.d(TAG, "Starting registration servive.");
        Intent intent = new Intent(ctx, ApiService.class);
        intent.setAction(ACTION_REGISTER_DEVICE);
        ctx.startService(intent);
    }

    public static boolean registerDeviceIfNeeded(Context ctx) {
        boolean regIdSent = PreferenceUtils.getSentRegistrationToBackend(ctx);
        if (regIdSent == false ) {
            registerDevice(ctx);
            return true;
        } else {
            Log.d(TAG, "Device is already registered.");
            return false;
        }
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
        context.startService(sendLocationIntent(context, contactId, message));
    }

    public static Intent confirmContactRequestIntent(Context context, @NonNull String contactUserId, boolean acceptRequest) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_CONFIRM_CONTACT_REQUEST);
        intent.putExtra(Constants.EXTRA_CONTACT_USER_ID, contactUserId);
        intent.putExtra(Constants.EXTRA_CONTACT_CONFIRMATION,acceptRequest);
        return intent;
    }

    public static void confirmContactRequest(Context context, @NonNull String contactUserId, boolean acceptRequest) {
        context.startService(confirmContactRequestIntent(context, contactUserId,acceptRequest));
    }

    public static void deleteContact(Context context, @NonNull String contactUserId) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_DELETE_CONTACT);
        intent.putExtra(Constants.EXTRA_CONTACT_USER_ID, contactUserId);
        context.startService(intent);
    }

    public static void updateContactList(Context context) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_UPDATE_CONTACT_LIST);
        context.startService(intent);
    }

    public static void deleteAccount(Context context)
    {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_DELETE_ACCOUNT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {


            // TODO: add  action to a pending queue in case the operation fails.
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

                if(mAccountName==null)
                {
                    ResultBroadcastReceiver.sendResult(this, action, Result.from(Result.RESULT_NO_ACCOUNT));
                    return;
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
                        boolean accept=intent.getBooleanExtra(Constants.EXTRA_CONTACT_CONFIRMATION,false);
                        resultCode = handleConfirmContactRequest(userId,accept);
                        break;
                    case ACTION_SEND_LOCATION:
                        userId = intent.getStringExtra(Constants.EXTRA_CONTACT_USER_ID);
                        message = intent.getStringExtra(Constants.EXTRA_MESSAGE);
                        resultCode = handleSendLocation(userId, message);
                        break;
                    case ACTION_DELETE_CONTACT:
                        userId = intent.getStringExtra(Constants.EXTRA_CONTACT_USER_ID);
                        resultCode = handleDeleteContact(userId);
                        break;
                    case ACTION_DELETE_ACCOUNT:
                        resultCode = handleDeleteAccount();
                        break;
                    case ACTION_REGISTER_DEVICE:
                        resultCode = handleRegisterDevice();
                        break;
                    case ACTION_CREATE_ACCOUNT:
                        String displayName = intent.getStringExtra(EXTRA_DISPLAY_NAME);
                        String photoUrl = intent.getStringExtra(EXTRA_PHOTO_URL);
                        resultCode = handleCreateAccount(displayName, photoUrl);
                        break;
                }

                // Perform specific action post
                handleResultCode(resultCode);

                ResultBroadcastReceiver.sendResult(this,action,resultCode);

                // TODO: remove action from pending queue upon success

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



    private void handleResultCode(Result resultCode) {
        if( resultCode.getResultCode() == Result.RESULT_BACKEND_ERROR_STATUSCODE )
        {
            switch( resultCode.getSpecificResultCode() )
            {
                case StatusCode.RESULT_USER_UNSUBSCRIBED:
                    // Force a contact update
                    ApiService.updateContactList(this);
                    break;
            }
        }
    }


    private Result handleCreateAccount(String displayName, String photoUrl) throws IOException {

        Log.d(TAG, "Creating account, name=" + displayName + ", phtotUrl="+photoUrl);

        NewAccountInfo accountInfo = new NewAccountInfo();
        accountInfo.setDisplayName(displayName);
        accountInfo.setPhotoUrl(photoUrl);

        StatusResult result = mApi.createAccount(accountInfo).execute();

        return Result.from(result);
    }

    private Result handleRegisterDevice() {
        try {
            // get gcm token for this app

            Log.d(TAG, "Getting registration token");
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(BuildConfig.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);

            // Send  registration to backend.
            Log.d(TAG, "Registering device with backend");


            RegistrationId r = new RegistrationId();
            r.setToken(token);

            StatusResult result = mApi.register(r).execute();

            Result resultCode = Result.from(Result.RESULT_SUCCESS);
            if(resultCode.isOk()) {
                Log.d(TAG, "Writing preference");
                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                PreferenceUtils.setSentRegistrationToBackend(this, true);
            }

            return resultCode;

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            PreferenceUtils.setSentRegistrationToBackend(this, false);

            return Result.from(Result.RESULT_FAILURE);
        }
    }


    private Result handleDeleteAccount() throws IOException {
        Log.d(TAG, "Deleting account");


        StatusResult result = mApi.deleteAccount().execute();

        PreferenceUtils.setAccountName(this,null);
        PreferenceUtils.setSentRegistrationToBackend(this, false);
        PreferenceUtils.setUserId(this, null);
        deleteAccountFromDb();

        return Result.from(result);
    }




    private Result handleDeleteContact(String userId) throws IOException {
        Log.d(TAG, "Deleting contact " + userId);

        StatusResult result = mApi.removeContact(userId).execute();

        removeContactFromDb(userId);

        return Result.from(result);
    }

    private void removeContactFromDb(String userId) {
        ContactSelection sel = new ContactSelection();

        sel.userid(userId);

        sel.delete(getContentResolver());
    }

    private void deleteAccountFromDb() {
        getContentResolver().delete(ContactColumns.CONTENT_URI, null, null);
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
            Log.w(TAG,"User has not granted permission for location");
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
        if(message!=null) {
            sendLocation.setMessage(message);
        }

        StatusResult statusResult = sendLocation.execute();

        Result result = Result.from(statusResult);

        if(result.isOk() && message!=null && !message.isEmpty())
        {
            MessagesUtils.addSentMessage(this,userId,message);
        }

        return result;
    }

    private Result handleConfirmContactRequest(@NonNull String userId, boolean accept) throws IOException {
        Log.d(TAG, "Confirming contact request with " + userId);
        showToast("Confirming contact request with " + userId);

        StatusResult result = mApi.confirmContactRequest(userId,accept).execute();

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

                values.putAccount(mAccountName);
                values.putUserid(contact.getUserId());
                values.putEmail(contact.getEmail());
                values.putName(contact.getDisplayName());
                values.putPhotoUrl(contact.getPhotoUrl());

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
        StatusResult statusResult = requestContactLocation.execute();

        Result result = Result.from(statusResult);

        if(result.isOk() && message!=null && !message.isEmpty())
        {
            MessagesUtils.addSentMessage(this,userId,message);
        }

        return result;
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
