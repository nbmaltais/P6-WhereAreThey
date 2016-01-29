package ca.nbsoft.whereareyou.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.Contact;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.MessagesUtils;
import ca.nbsoft.whereareyou.Utility.PreferenceUtils;
import ca.nbsoft.whereareyou.common.ContactStatus;
import ca.nbsoft.whereareyou.common.GcmMessageTypes;
import ca.nbsoft.whereareyou.common.GcmMessageKeys;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.contact.ContactContentValues;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.contact.ContactSelection;
import ca.nbsoft.whereareyou.ui.contact.ContactDetailActivity;
import ca.nbsoft.whereareyou.ui.main.MainActivity;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = GcmListenerService.class.getSimpleName();


    //private static final int LOCATION_REQUEST_NOTIF_ID = 1012;
    //private static final int LOCATION_NOTIF_ID = 1013;

    private static final int CONTACT_REQUEST_NOTIF_ID = -1;
    private static final int CONTACT_CONFIRMATION_NOTIF_ID = -2;


    boolean mNotificationSound=true;
    private String mAccountName;

    public MyGcmListenerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAccountName = PreferenceUtils.getAccountName(this);
        mNotificationSound = PreferenceUtils.getPlayNotificationSound(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle message) {
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        // Check if the user id match the logged in user
        String userId = message.getString(GcmMessageKeys.KEY_USER_ID);
        if(!checkUserId(userId))
            return;

        String type = message.getString(GcmMessageKeys.KEY_TYPE);
        switch(type)
        {
            case GcmMessageTypes.LOCATION_REQUEST:
                onLocationRequest(message);
                break;
            case GcmMessageTypes.LOCATION:
                onLocation(message);
                break;
            case GcmMessageTypes.CONTACT_REQUEST:
                onContactRequest(message);
                break;
            case GcmMessageTypes.CONTACT_CONFIRMATION:
                onContactConfirmation(message);
                break;
            case GcmMessageTypes.CONTACTS_MODIFIED:
                onContactsModified(message);
        }

        //showToast(message.getString(KEY_MESSAGE));

    }

    private boolean checkUserId(String userId) {
        return PreferenceUtils.getAccountName(this).equals(userId);
    }

    private void onContactsModified(Bundle message) {
        Log.d(TAG,"onContactsModified");

        ApiService.updateContactList(this);
    }

    private void onContactConfirmation(Bundle message) {

        Log.d(TAG,"onContactConfirmation");

        String fromEmail = message.getString(GcmMessageKeys.KEY_CONTACT_EMAIL);
        String fromUserId = message.getString(GcmMessageKeys.KEY_CONTACT_ID);

        String title = "Contact Confirmation";
        String contentText = "From " + fromEmail;

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

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

        String fromEmail = message.getString(GcmMessageKeys.KEY_CONTACT_EMAIL);
        String fromUserId = message.getString(GcmMessageKeys.KEY_CONTACT_ID);
        String fromUserName = message.getString(GcmMessageKeys.KEY_CONTACT_NAME);

        String contentTitle = getString(R.string.notification_contact_request_title);
        String contentText = getString(R.string.notification_contact_from, fromUserName);
        String acceptText = getString(R.string.notification_accept_action);

        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this);

        // Get the number of contact waiting fo confirmation
        int pending = getPendingConfirmationRequest();


        int notifId = CONTACT_REQUEST_NOTIF_ID;

        Intent clickIntent = new Intent(this,MainActivity.class);
        PendingIntent clickPendingIntent = TaskStackBuilder.create(this).addNextIntentWithParentStack(clickIntent).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent acceptIntent = ApiService.confirmContactRequestIntent(this, fromUserId,true);
        acceptIntent.putExtra(ApiService.EXTRA_CANCEL_NOTIFICATION, notifId);
        // TODO: handle case where we have multiple pending contact request
        PendingIntent acceptPendingIntent = PendingIntent.getService(this,0,acceptIntent,PendingIntent.FLAG_ONE_SHOT);

        Intent rejectIntent = ApiService.confirmContactRequestIntent(this, fromUserId,false);
        acceptIntent.putExtra(ApiService.EXTRA_CANCEL_NOTIFICATION, notifId);
        // TODO: handle case where we have multiple pending contact request
        PendingIntent rejectPendingIntent = PendingIntent.getService(this,0,rejectIntent,PendingIntent.FLAG_ONE_SHOT);

        //  we should check the number of pending contact request. If there is more than one, don't show actions

        builder.setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(clickPendingIntent);

        // If there are no other pending, show action
        if(pending==0) {
            builder.addAction(R.drawable.ic_confirm_contact, acceptText, acceptPendingIntent);
        }
        else
        {
            String subText = getString(R.string.notification_other_request_pending,pending);
            builder.setSubText(subText);
            builder.setNumber(pending);
        }

        addWaitForConfirmationContactRequest(fromUserId, fromUserName, fromEmail);

        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.notify(notifId, builder.build());
    }



    private void onLocation(Bundle message) {

        Log.d(TAG, "onLocation");

        String fromUserId = message.getString(GcmMessageKeys.KEY_CONTACT_ID);
        String messageText= message.getString(GcmMessageKeys.KEY_MESSAGE, null);

        Contact contact = getContact(fromUserId);
        if(contact==null)
        {
            Log.w(TAG,"onLocation, no contact matching contact id");
            return;
        }


        android.location.Location loc =null;
        if(message.containsKey( GcmMessageKeys.KEY_LATITUDE) && message.containsKey(GcmMessageKeys.KEY_LONGITUDE))
        {
            try {
                String latString = message.getString(GcmMessageKeys.KEY_LATITUDE);
                String longString = message.getString(GcmMessageKeys.KEY_LONGITUDE);

                double latDouble = Double.parseDouble(latString);
                double longDouble = Double.parseDouble(longString);

                loc = new android.location.Location("WhereAreYouBackend");
                loc.setLatitude(latDouble);
                loc.setLongitude(longDouble);
                Log.d(TAG, "location = " + loc);

                // update the contact object
                contact.setLatLng(loc);
            }
            catch(NumberFormatException e)
            {
                Log.w(TAG,"Invalid location format");
            }
        }


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
        showLocationNotification(contact, messageText);

    }

    private void showLocationNotification(Contact contact, String messageText) {
        // Create a notification

        Log.d(TAG,"showLocationNotification, messageText = " + messageText);

        String title = "Location Received";
        String contentText = "From " + contact.getDisplayName();
        int notifId = (int)contact.getId();

        Intent contentIntent = ContactDetailActivity.getStartActivityIntent(this, contact.getUserId());
        // Use contact id to differentiate pending inten since extra is not enough
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, (int)contact.getId(),
                contentIntent, PendingIntent.FLAG_ONE_SHOT);

        /*Intent locationIntent = MapsActivity.getShowContactIntent(this, contact);
        locationIntent.putExtra(Constants.EXTRA_MESSAGE,messageText);
        locationIntent.putExtra(ApiService.EXTRA_CANCEL_NOTIFICATION, notifId);
        PendingIntent locationPendingIntent = TaskStackBuilder.create(this)
                // add all of DetailsActivity's parents to the stack,
                // followed by DetailsActivity itself
                .addNextIntentWithParentStack(locationIntent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);*/

        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this);

        builder.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageText)
                        .setBigContentTitle(title)
                        .setSummaryText(contentText) );

        if( mNotificationSound) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
        }


        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.cancel(notifId);
        notifyMgr.notify(notifId, builder.build());
    }

    private void onLocationRequest(Bundle message) {

        Log.d(TAG,"onLocationRequest");

        String fromUserId = message.getString(GcmMessageKeys.KEY_CONTACT_ID);
        String messageText = message.getString(GcmMessageKeys.KEY_MESSAGE, null);

        Contact contact = getContact(fromUserId);
        if(contact==null)
        {
            Log.w(TAG,"onLocationRequest, no contact matching contact id");
            return;
        }

        // Add the message to the DB
        if(messageText!=null && !messageText.isEmpty())
        {
            MessagesUtils.addReceivedMessage(this, contact.getId(), messageText);
        }

        showLocationRequestNotification(contact, messageText);

    }

    private void showLocationRequestNotification(Contact contact, String messageText)
    {
        // Use the contact id for notification id
        int notifId = (int)contact.getId();
        String title = getString(R.string.notification_location_request_title);
        String replyText = getString(R.string.notification_reply_action);
        String contentText = "From " + contact.getDisplayName();

        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this);


        Intent replyIntent = ApiService.sendLocationIntent(this, contact.getUserId(), null);
        replyIntent.putExtra(ApiService.EXTRA_CANCEL_NOTIFICATION, notifId);
        PendingIntent replyPendingIntent = PendingIntent.getService(this, 0, replyIntent, PendingIntent.FLAG_ONE_SHOT);


        Intent contentIntent = ContactDetailActivity.getStartActivityIntent(this, contact.getUserId());
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, (int) contact.getId(), contentIntent, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setContentIntent(contentPendingIntent)
                .addAction(R.drawable.ic_reply_24dp, replyText, replyPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(messageText)
                                .setBigContentTitle(title)
                                .setSummaryText(contentText));

        if( mNotificationSound) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
        }

        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.cancel(notifId);
        notifyMgr.notify(notifId, builder.build());
    }

    private Contact getContact(String fromUserId) {
        ContactSelection where = new ContactSelection();
        where.userid(fromUserId).and().account(mAccountName);

        ContactCursor query = where.query(getContentResolver());
        if(query.moveToFirst())
        {
            return Contact.fromCursor(query);
        }
        else {
            return null;
        }
    }

    private void addWaitForConfirmationContactRequest(String fromUserId, String fromUserName, String fromEmail) {
        ContactContentValues values = new ContactContentValues();

        values.putAccount(mAccountName);
        values.putUserid(fromUserId);
        values.putName(fromUserName);
        values.putEmail(fromEmail);
        values.putStatus(ContactStatus.WAITING_FOR_CONFIRMATION);

        values.insert(this);
    }

    private void updateContact(String userId, android.location.Location loc, String message)
    {
        long timestamp = System.currentTimeMillis();
        ContactContentValues values = new ContactContentValues();
        if(loc!=null) {
            values.putPositionLatitude(loc.getLatitude());
            values.putPositionLongitude(loc.getLongitude());
            values.putPositionTimestamp(timestamp);
        }


        ContactSelection where = new ContactSelection();
        where.userid(userId).and().account(mAccountName);

        values.update(getContentResolver(), where);

    }

    private int getPendingConfirmationRequest()
    {
        ContactSelection sel = new ContactSelection();
        sel.account(mAccountName).and().status(ContactStatus.WAITING_FOR_CONFIRMATION);

        ContactCursor cursor = sel.query(getContentResolver(), new String[] {ContactColumns._ID});

        return cursor.getCount();
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
