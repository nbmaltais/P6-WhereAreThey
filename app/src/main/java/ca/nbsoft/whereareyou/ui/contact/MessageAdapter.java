package ca.nbsoft.whereareyou.ui.contact;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.Contact;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.message.MessageCursor;
import ca.nbsoft.whereareyou.ui.main.ContactAdapter;
import ca.nbsoft.whereareyou.ui.map.MapHelper;

/**
 * Created by Nicolas on 2016-01-20.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    class ViewHolder extends  RecyclerView.ViewHolder
    {
        public ViewHolder(View itemView) {
            super(itemView);

        }
    }

    class MessageViewHolder extends ViewHolder
    {
        @Bind(R.id.conversation_item_view)
        LinearLayout mContainer;
        @Bind(R.id.message_view)
        TextView mMessageView;
        @Bind(R.id.author_view)
        TextView mAuthorView;
        private String mContactName;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(String displayName, MessageCursor cursor)
        {
            mContactName = displayName;
            mMessageView.setText(cursor.getContent());
            if( cursor.getUserissender())
            {
                mAuthorView.setText(R.string.conversation_item_written_by_user);
                mContainer.setGravity(Gravity.LEFT);
            }
            else
            {
                String text = itemView.getContext().getString(R.string.conversation_item_written_by_contact,mContactName);
                mAuthorView.setText(text);
                mContainer.setGravity(Gravity.RIGHT);
            }
        }


    }

    class MapViewHolder extends ViewHolder
    {
        MapHelper mMapHelper;
        @Bind(R.id.map)
        MapView mMapView;
        public MapViewHolder(View itemView) {
            super(itemView);
        }

        void bind(Contact contact)
        {

        }
    }

    Contact mContact;
    MessageCursor mMessageCursor;

    private static final int MAP_VIEW = 0;
    private static final int MESSAGE_VIEW = 1;

    public MessageAdapter()
    {
    }

    void setContact(Contact c)
    {
        mContact = c;
        notifyDataSetChanged();
    }

    void setMessageCursor(MessageCursor c)
    {
        mMessageCursor = c;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==MAP_VIEW) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map,parent,false);
            return new MapViewHolder(v);
        }
        else if(viewType==MESSAGE_VIEW)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation_message,parent,false);
            return new MessageViewHolder(v);
        }

        return null;

    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        if( getItemViewType(position) == MAP_VIEW)
        {
            MapViewHolder vh = (MapViewHolder)holder;
            vh.bind(mContact);
        }
        else if(getItemViewType(position) == MESSAGE_VIEW)
        {
            mMessageCursor.moveToPosition(position-1);
            MessageViewHolder vh = (MessageViewHolder)holder;
            vh.bind(mContact.getDisplayName(),mMessageCursor);
        }
    }

    @Override
    public int getItemCount() {
        if(mContact == null)
            return 0;
        return 1 + (mMessageCursor!=null ? mMessageCursor.getCount() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return MAP_VIEW;
        else
            return MESSAGE_VIEW;
    }
}
