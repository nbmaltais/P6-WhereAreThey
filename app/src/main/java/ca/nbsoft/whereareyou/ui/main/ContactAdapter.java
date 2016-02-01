package ca.nbsoft.whereareyou.ui.main;

import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;

/**
 * Created by Nicolas on 2015-12-14.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private final OnContactRequestClickCallback mRequestItemClickCallback;
    private final OnContactClickCallback  mItemClickCallback;
    private ContactCursor mCursor;
    private ContactCursor mWaitingForConfirmationCursor;

    private static final int VIEW_CONTACT=1;
    private static final int VIEW_CONTACT_REQUEST =2;
    private static final int VIEW_HEADER=3;

    interface OnContactClickCallback
    {
        void onContactItemClicked(String userId, View transitionView);
    }

    interface OnContactRequestClickCallback
    {
        void onContactItemClicked(String userId, View transitionView);
        void onAcceptRequest(String userId);
        void onRefuseRequest(String userId);
    }



    public ContactAdapter(OnContactClickCallback callback, OnContactRequestClickCallback pendingContactClickCallback)
    {
        super();
        mItemClickCallback=callback;
        mRequestItemClickCallback =pendingContactClickCallback;
    }

    public void setContactCursor( ContactCursor c )
    {
        mCursor = c;
        notifyDataSetChanged();
    }

    public void setWaitingForConfirmationCursor(ContactCursor contactCursor) {
        mWaitingForConfirmationCursor = contactCursor;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_CONTACT  ) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
            ViewHolder vh = new ContactViewHolder(v,mItemClickCallback);
            return vh;
        }
        else if(viewType == VIEW_CONTACT_REQUEST)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_request, parent, false);
            ViewHolder vh = new ContactRequestViewHolder(v, mRequestItemClickCallback);
            return vh;
        }
        else if(viewType == VIEW_HEADER)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_header, parent, false);
            ViewHolder vh = new HeaderViewHolder(v);
            return vh;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if( mWaitingForConfirmationCursor.getCount()!=0 )
        {
            if(position==0)
            {
                ((HeaderViewHolder)holder).setSectionName(R.string.section_pending_contact_request);
                return;
            }
            position -=1;
            if(position < mWaitingForConfirmationCursor.getCount())
            {
                mWaitingForConfirmationCursor.moveToPosition(position);
                ((BaseContactViewHolder)holder).bind(mWaitingForConfirmationCursor);
                return;
            }

            position -= mWaitingForConfirmationCursor.getCount();
        }

        if(position==0)
        {
            ((HeaderViewHolder)holder).setSectionName(R.string.section_contacts);
            return;
        }
        position -=1;

        mCursor.moveToPosition(position);
        ((BaseContactViewHolder)holder).bind(mCursor);

    }

    @Override
    public int getItemViewType(int position) {

        if(mWaitingForConfirmationCursor.getCount()!=0)
        {
            if( position==0 )
                return VIEW_HEADER;

            position-=1;

            if(position < mWaitingForConfirmationCursor.getCount())
                return VIEW_CONTACT_REQUEST;

            position-=mWaitingForConfirmationCursor.getCount();
        }

        if(mCursor.getCount()!=0)
        {
            if(position==0)
                return VIEW_HEADER;
            position-=1;
            if(position < mCursor.getCount())
                return VIEW_CONTACT;
        }

        assert(false);
        return -1;

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(mCursor==null || mWaitingForConfirmationCursor==null)
            return 0;

        if(mWaitingForConfirmationCursor.getCount()!=0)
            count += (mWaitingForConfirmationCursor.getCount()+1);

        if(mCursor.getCount()!=0)
            count += (mCursor.getCount()+1);

        return count;
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class HeaderViewHolder extends ViewHolder
    {
        @Bind(R.id.header)
        TextView mTextView;

        public HeaderViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setSectionName( String name )
        {
            mTextView.setText(name);
        }
        void setSectionName( @StringRes int id )
        {
            mTextView.setText(id);
        }

    }

    static class BaseContactViewHolder extends ViewHolder {
        @Bind(R.id.email_view)
        TextView mEmailView;
        @Bind(R.id.name_view)
        TextView mNameView;
        @Bind(R.id.photo_view)
        ImageView mPhotoView;

        String mUserId;

        public BaseContactViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(ContactCursor cursor) {
            mUserId = cursor.getUserid();
            mEmailView.setText(cursor.getEmail());
            mNameView.setText(cursor.getName());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mPhotoView.setTransitionName("photo_user_" + mUserId);
            }

            String photoUrl = cursor.getPhotoUrl();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Picasso.with(itemView.getContext()).load(photoUrl).centerCrop().fit().into(mPhotoView);
            } else {
                Picasso.with(itemView.getContext()).load(R.drawable.ic_person_black_48dp).centerCrop().fit().into(mPhotoView);
            }

        }
    }

    static class ContactViewHolder extends BaseContactViewHolder {

        private OnContactClickCallback mCallback;

        public ContactViewHolder(View itemView, OnContactClickCallback itemClickCallback) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCallback = itemClickCallback;
        }

        public void bind(ContactCursor cursor) {
            super.bind(cursor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null)
                        mCallback.onContactItemClicked(mUserId, mPhotoView);
                }
            });
        }


    }

    static class ContactRequestViewHolder extends BaseContactViewHolder {
        OnContactRequestClickCallback mCallback;

        @Bind(R.id.button_accept)
        ImageButton mAcceptButton;
        @Bind(R.id.button_reject)
        ImageButton mRejectButton;

        public ContactRequestViewHolder(View itemView, OnContactRequestClickCallback itemClickCallback) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCallback = itemClickCallback;
        }

        @Override
        public void bind(ContactCursor cursor) {
            super.bind(cursor);

            mAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null)
                        mCallback.onAcceptRequest(mUserId);
                }
            });
            mRejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null)
                        mCallback.onRefuseRequest(mUserId);
                }
            });
        }
    }

}
