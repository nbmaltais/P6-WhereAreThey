package ca.nbsoft.whereareyou.ui.contact;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.Contact;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.provider.message.MessageCursor;
import ca.nbsoft.whereareyou.ui.map.MapHelper;

/**
 * Created by Nicolas on 2016-01-20.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    static class ViewHolder extends  RecyclerView.ViewHolder
    {
        public ViewHolder(View itemView) {
            super(itemView);

        }
    }

    static class MessageViewHolder extends ViewHolder
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
            }
            else
            {
                String text = itemView.getContext().getString(R.string.conversation_item_written_by_contact,mContactName);
                mAuthorView.setText(text);
            }
        }


    }

    class MapViewHolder extends ViewHolder implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
        MapHelper mMapHelper = new MapHelper();
        @Bind(R.id.map)
        MapView mMapView;
        public MapViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mMapView.onCreate(null);
            mMapView.getMapAsync(this);
        }

        void bind(Contact contact)
        {
            mMapHelper.addContactMarker(contact,true);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d("MapViewHolder","onMapReady");
            mMapHelper.onMapReady(googleMap);

            googleMap.setOnMapClickListener(this);
        }

        @Override
        public void onMapClick(LatLng latLng) {
            mCallbacks.onMapClicked();
        }
    }

    Contact mContact;
    MessageCursor mMessageCursor;

    private static final int MAP_VIEW = 0;
    private static final int USER_MESSAGE_VIEW = 1;
    private static final int CONTACT_MESSAGE_VIEW = 2;

    interface Callbacks{
        void onMapClicked();
    }

    Callbacks mCallbacks;

    public MessageAdapter(Callbacks cb)
    {
        mCallbacks = cb;
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
        else if(viewType==USER_MESSAGE_VIEW)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message,parent,false);
            return new MessageViewHolder(v);
        }
        else if(viewType==CONTACT_MESSAGE_VIEW)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_message,parent,false);
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
        else
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
        {
            mMessageCursor.moveToPosition(position-1);
            if( mMessageCursor.getUserissender() == true)
                return USER_MESSAGE_VIEW;
            else
                return CONTACT_MESSAGE_VIEW;
        }

    }
}
