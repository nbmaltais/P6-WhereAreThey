package ca.nbsoft.whereareyou.ui.contact;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailFragment extends Fragment {

    @Bind(R.id.top_container) View mTopContainer;

    private String mUserId;
    private String mContactName;

    public ContactDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_contact_detail, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.request_location_button)
    void onRequestLocationClicked()
    {
        ApiService.requestContactLocation(getContext(),mUserId,null);
        Snackbar.make(mTopContainer,"Requested position of " + mContactName, Snackbar.LENGTH_SHORT);
    }

    public void bind(ContactCursor cursor) {
        mUserId = cursor.getUserid();
        mContactName = cursor.getEmail();
    }
}
