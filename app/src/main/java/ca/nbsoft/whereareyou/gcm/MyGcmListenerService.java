package ca.nbsoft.whereareyou.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.Constants;
import ca.nbsoft.whereareyou.Contact;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.MessagesUtils;
import ca.nbsoft.whereareyou.provider.contact.ContactContentValues;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.contact.ContactSelection;
import ca.nbsoft.whereareyou.ui.contact.ContactDetailActivity;
import ca.nbsoft.whereareyou.ui.main.MainActivity;
import ca.nbsoft.whereareyou.ui.map.MapsActivity;

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
    private static final String KEY_LATITUDE = "location_lat";
    private static final String KEY_LONGITUDE = "location_long";


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

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);

        // Notification
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this);

        builder.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification);

        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.notify(CONTACT_CONFIRMATION_NOTIF_ID, builder.build());

        // Update contact list
        ApiService.updateContactList(this);
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

        Intent acceptIntent = ApiService.confirmContactRequestIntent(this, fromUserId,true);
        acceptIntent.putExtra(ApiService.EXTRA_CANCEL_NOTIFICATION, notifId);
        PendingIntent acceptPendingIntent = PendingIntent.getService(this,0,acceptIntent,PendingIntent.FLAG_ONE_SHOT);

        Intent rejectIntent = ApiService.confirmContactRequestIntent(this, fromUserId,false);
        acceptIntent.putExtra(ApiService.EXTRA_CANCEL_NOTIFICATION, notifId);
        PendingIntent rejectPendingIntent = PendingIntent.getService(this,0,rejectIntent,PendingIntent.FLAG_ONE_SHOT);


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
        String messageText= message.getString(KEY_MESSAGE,null);

        android.location.Location loc = new android.location.Location("WhereAreYouBackend");
        if(message.containsKey(KEY_LATITUDE) && message.containsKey(KEY_LONGITUDE))
        {
            try {
                String latString = message.getString(KEY_LATITUDE);
                String longString = message.getString(KEY_LONGITUDE);

                double latDouble = Double.parseDouble(latString);
                double longDouble = Double.parseDouble(longString);

                loc.setLatitude(latDouble);
                loc.setLongitude(longDouble);
                Log.d(TAG, "location = " + loc);
            }
            catch(NumberFormatException e)
            {
                Log.w(TAG,"Invalid location format");
            }
        }

        Contact contact = getContact(fromUserId);
        if(contact==null)
        {
            Log.w(TAG,"onLocation, no contact matching contact id");
            return;
        }

        // update the contact object
        contact.setLatLng(loc);

        // Update the contact position in the DB
        updateContact(fromUserId, loc, messageText);

        // Add the message to the DB
        if(messageText!=null && !messageText.isEmpty())
        {
            MessagesUtils.addReceivedMessage(this,contact.getId(),messageText);
        }

        // TODO: used ordered broadcast to show notification only if no activity can
        // use it
        // https://commonsware.com/blog/2010/08/11/activity-notification-ordered-broadcast.html
        sendLocationNotification(contact,messageText);

    }

    private void sendLocationNotification(Contact contact, String messageText) {
        // Create a notification

        String title = "Location Received";
        String contentText = "From " + contact.getDisplayName();
        int notifId = LOCATION_NOTIF_ID;

        // TODO: create back stack for map activity
        Intent locationIntent = MapsActivity.getShowContactIntent(this,contact);
        locationIntent.putExtra(Constants.EXTRA_MESSAGE,messageText);
        locationIntent.putExtra(ApiService.EXTRA_CANCEL_NOTIFICATION, notifId);

        PendingIntent locationPendingIntent = TaskStackBuilder.create(this)
                // add all of DetailsActivity's parents to the stack,
                // followed by DetailsActivity itself
                .addNextIntentWithParentStack(locationIntent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this);

        builder.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(locationPendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText));

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);


        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.notify(notifId, builder.build());
    }

    private void onLocationRequest(Bundle message) {

        Log.d(TAG,"onLocationRequest");

        String fromUserId = message.getString(KEY_USER_ID);
        String messageText = message.getString(KEY_MESSAGE, null);

        Contact contact = getContact(fromUserId);
        if(contact==null)
        {
            Log.w(TAG,"onLocationRequest, no contact matching contact id");
            return;
        }

        // Add the message to the DB
        if(messageText!=null && !messageText.isEmpty())
        {
            MessagesUtils.addReceivedMessage(this,contact.getId(),messageText);
        }


        int notifId = LOCATION_REQUEST_NOTIF_ID;
        String title = getString(R.string.notification_location_request_title);
        String replyText = getString(R.string.notification_reply_action);
        String contentText = "From " + contact.getDisplayName();

        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this);

        Intent replyIntent = ApiService.sendLocationIntent(this, fromUserId, null);
        replyIntent.putExtra(ApiService.EXTRA_CANCEL_NOTIFICATION, notifId);
        PendingIntent replyPendingIntent = PendingIntent.getService(this, 0, replyIntent, PendingIntent.FLAG_ONE_SHOT);


        Intent contentIntent = ContactDetailActivity.getStartActivityIntent(this, fromUserId);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, contentIntent, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setContentIntent(contentPendingIntent)
                .addAction(R.drawable.ic_reply_24dp, replyText, replyPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText));

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.notify(notifId, builder.build());
    }

    private Contact getContact(String fromUserId) {
        ContactSelection where = new ContactSelection();
        where.userid(fromUserId);

        ContactCursor query = where.query(getContentResolver());
        if(query.moveToFirst())
        {
            return Contact.fromCursor(query);
        }
        else {
            return null;
        }
    }

    private void updateContact(String userId, android.location.Location loc, String message)
    {
        long timestamp = System.currentTimeMillis();
        ContactContentValues values = new ContactContentValues();
        values.putPositionLatitude(loc.getLatitude());
        values.putPositionLongitude(loc.getLongitude());
        values.putPositionTimestamp(timestamp);

        ContactSelection where = new ContactSelection();
        where.userid(userId);

        values.update(getContentResolver(), where);

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
