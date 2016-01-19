package ca.nbsoft.whereareyou.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private OnItemClickCallback mItemClickCallback;
    private ContactCursor mCursor;
    private ContactCursor mWaitingForConfirmationCursor;

    interface OnItemClickCallback
    {
        void onContactItemClicked(String userId);
    }



    public ContactAdapter(OnItemClickCallback callback)
    {
        super();
        mItemClickCallback=callback;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int threshold = mCursor != null ? mCursor.getCount() : 0;

        if(position <threshold) {
            if(mCursor.moveToPosition(position))
            {
                holder.bind(mCursor,mItemClickCallback);
            }
        }
        else
        {
            position -= threshold;

        }

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(mCursor!=null)
            count += mCursor.getCount();


        if(mWaitingForConfirmationCursor!=null)
            count += mWaitingForConfirmationCursor.getCount();

        return count;
    }

    static class ViewHolder extends  RecyclerView.ViewHolder
    {
        @Bind(R.id.email_view)
        TextView mEmailView;
        @Bind(R.id.name_view)
        TextView mNameView;
        @Bind(R.id.photo_view)
        ImageView mPhotoView;

        private String mUserId;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ContactCursor cursor, final OnItemClickCallback itemClickCallback) {
            mUserId = cursor.getUserid();
            mEmailView.setText(cursor.getEmail());
            mNameView.setText(cursor.getName());

            String photoUrl = cursor.getPhotoUrl();
            if(photoUrl!=null && !photoUrl.isEmpty()) {
                Picasso.with(itemView.getContext()).load(photoUrl).centerCrop().fit().into(mPhotoView);
            }
            else
            {
                Picasso.with(itemView.getContext()).load(R.drawable.ic_person_black_48dp).centerCrop().fit().into(mPhotoView);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickCallback.onContactItemClicked(mUserId);
                }
            });
        }
    }

}
