package ca.nbsoft.whereareyou;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;
import java.util.ArrayList;

import ca.nbsoft.whereareyou.backend.whereAreYou.WhereAreYou;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.ContactInfo;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.ContactInfoCollection;
import ca.nbsoft.whereareyou.backend.whereAreYou.model.Location;
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
public class ApiService extends IntentService {
    static final String TAG = ApiService.class.getSimpleName();

    public static final String ACTION_REQUEST_LOCATION = "ca.nbsoft.whereareyou.action.REQUEST_LOCATION";
    public static final String ACTION_SEND_CONTACT_REQUEST = "ca.nbsoft.whereareyou.action.SEND_CONTACT_REQUEST";
    public static final String ACTION_CONFIRM_CONTACT_REQUEST = "ca.nbsoft.whereareyou.action.CONFIRM_CONTACT_REQUEST";
    public static final String ACTION_SEND_LOCATION = "ca.nbsoft.whereareyou.action.SEND_LOCATION";
    public static final String ACTION_UPDATE_CONTACT_LIST = "ca.nbsoft.whereareyou.action.UPDATE_CONTACT_LIST";

    public static final String EXTRA_CONTACT_USER_ID = "ca.nbsoft.whereareyou.extra.USER_ID";
    public static final String EXTRA_MESSAGE = "ca.nbsoft.whereareyou.extra.MESSAGE";
    public static final String EXTRA_CONTACT_EMAIL = "ca.nbsoft.whereareyou.extra.EMAIL";
    public static final String EXTRA_CANCEL_NOTIFICATION = "ca.nbsoft.whereareyou.extra.CANCEL_NOTIFICATION";


    private WhereAreYou mApi;

    public ApiService() {
        super("ApiService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GoogleAccountCredential credential = Endpoints.getCredential(this);
        mApi = Endpoints.getApiEndpoint(credential);
    }


    public static void requestContactLocation(Context context, String contactId, String message ) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_REQUEST_LOCATION);
        intent.putExtra(EXTRA_CONTACT_USER_ID, contactId);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.startService(intent);
    }

    public static void sendContactRequest(Context context, String contactEmail ) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_SEND_CONTACT_REQUEST);
        intent.putExtra(EXTRA_CONTACT_EMAIL, contactEmail);
        context.startService(intent);
    }

    public static Intent sendLocationIntent(Context context, String contactId, String message)
    {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_SEND_LOCATION);
        intent.putExtra(EXTRA_CONTACT_USER_ID, contactId);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    public static void sendLocation(Context context, String contactId, String message)
    {
        context.startActivity(sendLocationIntent(context, contactId, message));
    }

    public static Intent confirmContactRequestIntent(Context context, String contactUserId)
    {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_CONFIRM_CONTACT_REQUEST);
        intent.putExtra(EXTRA_CONTACT_USER_ID, contactUserId);
        return intent;
    }

    public static void confirmContactRequest(Context context, String contactUserId)
    {
        context.startService(confirmContactRequestIntent(context, contactUserId));
    }

    public static void updateContactList(Context context)
    {
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

            final String action = intent.getAction();
            final String userId;
            final String message;
            try {
                switch (action) {
                    case ACTION_REQUEST_LOCATION:
                        userId = intent.getStringExtra(EXTRA_CONTACT_USER_ID);
                        message = intent.getStringExtra(EXTRA_MESSAGE);
                        handleRequestContactLocation(userId, message);
                        break;
                    case ACTION_SEND_CONTACT_REQUEST:
                        final String email = intent.getStringExtra(EXTRA_CONTACT_EMAIL);
                        handleSendContactRequest(email);
                        break;
                    case ACTION_UPDATE_CONTACT_LIST:
                        handleUpdateContactList();
                        break;
                    case ACTION_CONFIRM_CONTACT_REQUEST:
                        userId = intent.getStringExtra(EXTRA_CONTACT_USER_ID);
                        handleConfirmContactRequest(userId);
                        break;
                    case ACTION_SEND_LOCATION:
                        userId = intent.getStringExtra(EXTRA_CONTACT_USER_ID);
                        message = intent.getStringExtra(EXTRA_MESSAGE);
                        handleSendLocation(userId,message);
                        break;
                }

                if(intent.hasExtra(EXTRA_CANCEL_NOTIFICATION)) {
                    int id = intent.getIntExtra(EXTRA_CANCEL_NOTIFICATION,0);
                    NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notifyMgr.cancel(id);
                }

                // TODO: remove action from pending queue upon sucess
            }
            catch (IOException e)
            {
                Log.e(TAG,"Error while permorming action :" + action,e);
                // TODO: report error to UI
            }

        }
    }

    private void handleSendLocation(String userId, String message) throws IOException {
        Log.d(TAG,"Sending location to " + userId);

        Location loc = new Location();

        showToast("Sending location to " + userId);

        mApi.sendLocation(userId,message,loc);
    }

    private void handleConfirmContactRequest(String userId) throws IOException {
        Log.d(TAG,"Confirming contact request with " + userId);
        showToast("Confirming contact request with " + userId);

        mApi.confirmContactRequest(userId).execute();
    }

    private void handleUpdateContactList() throws IOException {
        Log.d(TAG, "handleUpdateContactList");

        ContactInfoCollection contacts = mApi.getContacts().execute();


        // TODO: do a merge instead of nuking all contacts and recreating them

        getContentResolver().delete(ContactColumns.CONTENT_URI,null,null);

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();


        for( ContactInfo contact : contacts.getItems())
        {
            ContactContentValues values = new ContactContentValues();

            values.putUserid(contact.getUserId());
            values.putEmail(contact.getEmail());

            batch.add(ContentProviderOperation.newInsert(ContactColumns.CONTENT_URI).withValues(values.values()).build());
        }

        try {
            getContentResolver().applyBatch(WhereRUProvider.AUTHORITY, batch);
        } catch (RemoteException e) {
            Log.e(TAG,"Contact DB update failed",e);
        } catch (OperationApplicationException e) {
            Log.e(TAG, "Contact DB update failed", e);
        }

    }

    private void handleSendContactRequest(String email)throws IOException {
        Log.d(TAG,"handleSendContactRequest");

        mApi.sendContactRequest(email).execute();

    }

    private void handleRequestContactLocation(String userId, String message)throws IOException {
        Log.d(TAG,"handleRequestContactLocation");

        mApi.requestContactLocation(userId, message).execute();

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
