package ca.nbsoft.whereareyou.ui.contact;


import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;

import ca.nbsoft.whereareyou.Utility.Utils;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailFragment extends Fragment {

    @Bind(R.id.top_container) View mTopContainer;
    @Bind(R.id.message)
    EditText mMessageView;
    @Bind(R.id.photo_view)
    ImageView mPhotoView;

    private String mUserId;
    private String mContactName;

    BroadcastReceiver mReceiver = new ApiService.ResultBroadcastReceiver()
    {
        @Override
        public void onRequestLocationResult(ApiService.Result resultCode) {

        }

        @Override
        public void onSendLocationResult(ApiService.Result resultCode) {

        }

        @Override
        public void onDeleteContactResult(ApiService.Result resultCode) {

        }
    };
    private String mPhotoUrl;

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

    @Override
    public void onResume() {
        super.onResume();
        String[] actions = new String[] {ApiService.ACTION_SEND_LOCATION,
                ApiService.ACTION_REQUEST_LOCATION, ApiService.ACTION_DELETE_CONTACT};
        ApiService.subscribeToResult(getContext(), actions, mReceiver);
    }

    @Override
    public void onPause() {
        ApiService.unSubscribeFromResult(getContext(), mReceiver);
        super.onPause();
    }

    private String getMessage() {
        return mMessageView.getText().toString();
    }

    @OnClick(R.id.request_location_button)
    void onRequestLocationClicked()
    {
        String text = getContext().getString(R.string.contact_detail_request_position_confirmation);

        Utils.cancelableActionSnackbar(mTopContainer, text, new Runnable() {
            @Override
            public void run() {
                ApiService.requestContactLocation(getContext(), mUserId, getMessage());
            }
        });


        //Snackbar.make(mTopContainer,  text, Snackbar.LENGTH_SHORT).show();
    }


    @OnClick(R.id.send_location_button)
    void onSendLocationClicked()
    {
        String text = getContext().getString(R.string.contact_detail_send_position_confirmation);

        Utils.cancelableActionSnackbar(mTopContainer, text, new Runnable() {
            @Override
            public void run() {
                ApiService.sendLocation(getContext(), mUserId, getMessage());
            }
        });

        //ApiService.sendLocation(getContext(), mUserId, getMessage());
        //Snackbar.make(mTopContainer,text, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.delete_contact_button)
    void onDeleteContactClicked()
    {
        String text = getContext().getString(R.string.contact_detail_delete_confirmation);
        Utils.cancelableActionSnackbar(mTopContainer, text, new Runnable() {
            @Override
            public void run() {
                ApiService.deleteContact(getContext(), mUserId);
                //Snackbar.make(mTopContainer,"Deleted " + mContactName, Snackbar.LENGTH_SHORT).show();

                // TODO use an interface to signal the parent activity
                getActivity().finish();
            }
        });

    }

    public void bind(ContactCursor cursor) {
        mUserId = cursor.getUserid();
        mContactName = cursor.getName();
        mPhotoUrl=cursor.getPhotoUrl();

        if(mPhotoUrl!=null && !mPhotoUrl.isEmpty()) {
            Picasso.with(getContext()).load(mPhotoUrl).centerCrop().fit().into(mPhotoView);
        }
        else
        {

        }
    }

}
