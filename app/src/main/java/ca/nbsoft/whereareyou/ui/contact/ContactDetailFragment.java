package ca.nbsoft.whereareyou.ui.contact;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.Contact;
import ca.nbsoft.whereareyou.R;

import ca.nbsoft.whereareyou.Utility.Utils;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.ui.main.AddContactActivity;
import ca.nbsoft.whereareyou.ui.map.MapFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailFragment extends Fragment {

    private static final String TAG = ContactDetailFragment.class.getSimpleName();
    private static final int DELETE_CONTACT = 0;
    @Bind(R.id.top_container) View mTopContainer;
    @Bind(R.id.message)
    EditText mMessageView;
    @Bind(R.id.photo_view)
    ImageView mPhotoView;

    MapFragment mMapFragment;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.layout_contact_detail, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            mMapFragment = new MapFragment();
            getFragmentManager().beginTransaction().add(R.id.map,mMapFragment).commit();

        } else{
            mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        }

        if(mMapFragment==null)
            Log.w(TAG,"onCreateView : mMapFragment==null");
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_contact_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_delete_contact)
        {
            askDeleteContact();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void askDeleteContact() {
        DialogFragment dialog = new DeleteContactDialog();
        dialog.setTargetFragment(this, DELETE_CONTACT);
        dialog.show(getFragmentManager(), "delete-contact-dialog");
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



    private void deleteContact()
    {
        // TODO: use dialog to ask if user is sure

        ApiService.deleteContact(getContext(), mUserId);
                //Snackbar.make(mTopContainer,"Deleted " + mContactName, Snackbar.LENGTH_SHORT).show();

        // TODO use an interface to signal the parent activity
        getActivity().finish();

    }

    public void bind(ContactCursor cursor) {
        Log.d(TAG,"bind contact cursor");
        mUserId = cursor.getUserid();
        mContactName = cursor.getName();
        mPhotoUrl=cursor.getPhotoUrl();

        if(mPhotoUrl!=null && !mPhotoUrl.isEmpty()) {
            Picasso.with(getContext()).load(mPhotoUrl).centerCrop().fit().into(mPhotoView);
        }
        else
        {

        }

        Contact contact = Contact.fromCursor(cursor);

        if(mMapFragment!=null)
            mMapFragment.addContactMarker(contact,true);
        else
            Log.w(TAG,"bind : mMapFragment==null");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(requestCode == DELETE_CONTACT)
        {
            if( resultCode == Activity.RESULT_OK)
            {
                deleteContact();
            }
            else
            {
                Log.d(TAG,"Canceled delete contact");
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }

    static public class DeleteContactDialog extends DialogFragment
    {
        public DeleteContactDialog()
        {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Activity activity = getActivity();

            return new AlertDialog.Builder(activity)
                    //.setIcon(R.drawable.alert_dialog_dart_icon)
                    //.setTitle(R.string.contact_detail_delete_contact_dialog_title)
                    .setMessage(R.string.contact_detail_delete_contact_dialog_text)
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                                }
                            }).create();
        }
    }

}
