package ca.nbsoft.whereareyou.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;

/**
 * Created by Nicolas on 2015-12-14.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private ContactCursor mCursor;

    public ContactAdapter()
    {
        super();
    }

    public void setContactCursor( ContactCursor c )
    {
        mCursor = c;
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
        if(mCursor.moveToPosition(position))
        {
            holder.bind(mCursor);
        }
    }

    @Override
    public int getItemCount() {
        if(mCursor==null)
            return 0;
        else
            return mCursor.getCount();
    }

    static class ViewHolder extends  RecyclerView.ViewHolder
    {
        @Bind(R.id.email_view)
        TextView mEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ContactCursor cursor) {
            mEmail.setText(cursor.getEmail());
        }
    }

}
