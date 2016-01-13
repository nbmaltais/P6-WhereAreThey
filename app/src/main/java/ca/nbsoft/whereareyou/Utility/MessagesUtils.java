package ca.nbsoft.whereareyou.Utility;

import android.content.Context;

import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.contact.ContactSelection;
import ca.nbsoft.whereareyou.provider.message.MessageContentValues;
import ca.nbsoft.whereareyou.provider.message.MessageSelection;

/**
 * Created by Nicolas on 2016-01-12.
 */
public class MessagesUtils {
    static public void addSentMessage( Context ctx, long contactId, String message )
    {
        MessageContentValues values = new MessageContentValues();
        values.putContactId(contactId);
        values.putContent(message);
        values.putTimestamp(System.currentTimeMillis());
        values.putUserissender(true);

        values.insert(ctx);
    }

    static public void addSentMessage( Context ctx, String contactUserId, String message )
    {
        ContactSelection where = new ContactSelection();
        where.userid(contactUserId);

        ContactCursor query = where.query(ctx, new String[] {ContactColumns._ID});
        if(query.moveToFirst())
        {
            long id = query.getId();
            addSentMessage(ctx,id,message);
        }
    }

    static public void addReceivedMessage( Context ctx,long contactId, String message )
    {
        MessageContentValues values = new MessageContentValues();
        values.putContactId(contactId);
        values.putContent(message);
        values.putTimestamp(System.currentTimeMillis());
        values.putUserissender(false);

        values.insert(ctx);
    }
}
