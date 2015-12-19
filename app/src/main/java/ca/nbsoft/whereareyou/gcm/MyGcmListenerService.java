package ca.nbsoft.whereareyou.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.contact.ContactSelection;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = GcmListenerService.class.getSimpleName();
    private static final String KEY_TYPE = "type";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_MESSAGE = "message";
    private static final String LOCATION = "location";
    private static final int CONTACT_REQUEST_NOTIF_ID = 1011;
    private static final int LOCATION_REQUEST_NOTIF_ID = 1012;
    private static final int LOCATION_NOTIF_ID = 1013;
    private static final int CONTACT_CONFIRMATION_NOTIF_ID = 1014;

    public MyGcmListenerService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle message) {
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        String type = message.getString(KEY_TYPE);
        switch(type)
        {
            case "location-request":
                onLocationRequest(message);
                break;
            case "location":
                onLocation(message);
                break;
            case "contact-request":
                onContactRequest(message);
                break;
            case "contact-confirmation":
                onContactConfirmation(message);
                break;
        }

        //showToast(message.getString(KEY_MESSAGE));

    }

    private void onContactConfirmation(Bundle message) {

        Log.d(TAG,"onContactConfirmation");

        String fromEmail = message.getString(KEY_USER_EMAIL);
        String fromUserId = message.getString(KEY_USER_ID);

        String title = "Contact Confirmation";
        String contentText = "From " + fromEmail;

        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this);

        builder.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification);

        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.notify(CONTACT_CONFIRMATION_NOTIF_ID, builder.build());
    }

    private void onContactRequest(Bundle message) {

        Log.d(TAG,"onContactRequest");

        String fromEmail = message.getString(KEY_USER_EMAIL);
        String fromUserId = message.getString(KEY_USER_ID);

        String title = getString(R.string.notification_contact_request_title);
        String contentText = "From " + fromEmail;
        String acceptText = getString(R.string.notification_accept_action);

        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this);

        int notifId = CONTACT_REQUEST_NOTIF_ID;

        Intent acceptIntent = ApiService.confirmContactRequestIntent(this, fromUserId);
        acceptIntent.putExtra(ApiService.EXTRA_CANCEL_NOTIFICATION,notifId);
        PendingIntent acceptPendingIntent = PendingIntent.getService(this,0,acceptIntent,PendingIntent.FLAG_ONE_SHOT);

        builder.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .addAction(R.drawable.ic_confirm_contact, acceptText, acceptPendingIntent);

        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.notify(notifId, builder.build());
    }

    private void onLocation(Bundle message) {

        Log.d(TAG,"onLocation");

        String fromUserId = message.getString(KEY_USER_ID);
        String messageText = message.getString(KEY_MESSAGE);

        ContactCursor contactCursor = getContact(fromUserId);
        if(!contactCursor.moveToFirst())
        {
            Log.w(TAG,"onLocation, no contact matching contact id");
            return;
        }


        String title = "Location Received";
        String contentText = "From " + contactCursor.getEmail();

        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this);

        builder.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification);

        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.notify(LOCATION_NOTIF_ID, builder.build());
    }

    private void onLocationRequest(Bundle message) {

        Log.d(TAG,"onLocationRequest");

        String fromUserId = message.getString(KEY_USER_ID);
        String messageText = message.getString(KEY_MESSAGE);

        ContactCursor contactCursor = getContact(fromUserId);
        if(!contactCursor.moveToFirst())
        {
            Log.w(TAG,"onLocationRequest, no contact matching contact id");
            return;
        }

        int notifId = LOCATION_REQUEST_NOTIF_ID;
        String title = getString(R.string.notification_location_request_title);
        String replyText = getString(R.string.notification_reply_action);
        String contentText = "From " + contactCursor.getEmail();

        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this);

        Intent replyIntent = ApiService.sendLocationIntent(this,fromUserId,null);
        replyIntent.putExtra(ApiService.EXTRA_CANCEL_NOTIFICATION,notifId);
        PendingIntent replyPendingIntent = PendingIntent.getService(this,0,replyIntent,PendingIntent.FLAG_ONE_SHOT);

        builder.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .addAction(R.drawable.ic_reply_24dp, replyText, replyPendingIntent);

        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.notify(notifId, builder.build());
    }

    private ContactCursor getContact(String fromUserId) {
        ContactSelection where = new ContactSelection();
        where.userid(fromUserId);

        return where.query(getContentResolver());
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
