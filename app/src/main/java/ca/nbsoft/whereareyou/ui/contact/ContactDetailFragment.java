package ca.nbsoft.whereareyou.ui.contact;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v4.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.Contact;
import ca.nbsoft.whereareyou.R;

import ca.nbsoft.whereareyou.Utility.Utils;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.message.MessageColumns;
import ca.nbsoft.whereareyou.provider.message.MessageCursor;
import ca.nbsoft.whereareyou.provider.message.MessageSelection;
import ca.nbsoft.whereareyou.ui.ErrorMessages;
import ca.nbsoft.whereareyou.ui.map.MapHelper;
import ca.nbsoft.whereareyou.ui.map.MapsActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ContactDetailFragment.class.getSimpleName();
    private static final int DELETE_CONTACT = 0;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.compose)
    View mComposeContainer;
    @Bind(R.id.message)
    EditText mMessageView;
    @Bind(R.id.photo_view)
    ImageView mPhotoView;


    private MessageAdapter mAdapter;
    private Contact mContact;

    BroadcastReceiver mReceiver = new ApiService.ResultBroadcastReceiver()
    {
        @Override
        public void onRequestLocationResult(ApiService.Result resultCode, Bundle args) {
            if(!resultCode.isOk())
                ErrorMessages.showErrorMessage(getContext(), resultCode);
        }

        @Override
        public void onSendLocationResult(ApiService.Result resultCode, Bundle args) {
            if(!resultCode.isOk())
                ErrorMessages.showErrorMessage(getContext(), resultCode);
        }

        @Override
        public void onDeleteContactResult(ApiService.Result resultCode, Bundle args) {
            if(!resultCode.isOk())
                ErrorMessages.showErrorMessage(getContext(), resultCode);
        }
    };



    public ContactDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MapsInitializer.initialize(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.layout_contact_detail, container, false);
        ButterKnife.bind(this, view);

        //TODO find a cleaner way to detect if we where launched with transition animation
        boolean withTransition = getActivity().getIntent().getBooleanExtra(ContactDetailActivity.EXTRA_TRANSITION, false);


        mAdapter = new MessageAdapter( new MessageAdapter.Callbacks(){

            @Override
            public void onMapClicked() {
                MapsActivity.startShowContact(getContext(),mContact);
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || withTransition==false) {
            mAdapter.showMap();
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Adjust the padding of the coordinato layout main content so that we
        // can see the last message. If we don't do that, the message is hidden by the compose layout
        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (vto.isAlive())
                    vto.removeOnPreDrawListener(this);

                mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(),
                        mRecyclerView.getPaddingTop(),
                        mRecyclerView.getPaddingRight(),
                        mComposeContainer.getHeight());

                return true;
            }
        });

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

        Utils.cancelableActionSnackbar(getView(), text, new Runnable() {
            @Override
            public void run() {
                ApiService.requestContactLocation(getContext(), mContact.getUserId(), getMessage());
            }
        });


    }


    @OnClick(R.id.send_location_button)
    void onSendLocationClicked()
    {
        String text = getContext().getString(R.string.contact_detail_send_position_confirmation);

        Utils.cancelableActionSnackbar(getView(), text, new Runnable() {
            @Override
            public void run() {
                ApiService.sendLocation(getContext(), mContact.getUserId(), getMessage());
            }
        });


    }



    private void deleteContact()
    {
        // TODO: use dialog to ask if user is sure

        ApiService.deleteContact(getContext(), mContact.getUserId());
                //Snackbar.make(mTopContainer,"Deleted " + mContactName, Snackbar.LENGTH_SHORT).show();

        // TODO use an interface to signal the parent activity
        getActivity().finish();

    }

    public void bind(ContactCursor cursor) {
        Log.d(TAG,"bind contact cursor");
        mContact = Contact.fromCursor(cursor);


        String photoUrl=cursor.getPhotoUrl();

        if(photoUrl!=null && !photoUrl.isEmpty()) {
            Picasso.with(getContext()).load(photoUrl).centerCrop().fit().into(mPhotoView);
        }
        else
        {

        }


        mAdapter.setContact(mContact);

        getLoaderManager().initLoader(0, null, this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        MessageSelection sel = new MessageSelection();
        sel.contactId(mContact.getId());
        sel.orderByTimestamp();

        String[] projection = {MessageColumns._ID, MessageColumns.TIMESTAMP,MessageColumns.CONTENT,MessageColumns.USERISSENDER};

        CursorLoader loader = new CursorLoader(getContext(),sel.uri(),projection,sel.sel(),sel.args(),sel.order() );

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        MessageCursor cursor = new MessageCursor(data);
        mAdapter.setMessageCursor(cursor);

        getActivity().supportStartPostponedEnterTransition();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setMessageCursor(null);
    }

    public void onSharedElementEnd() {
        Log.d(TAG,"onSharedElementEnd");
        mAdapter.showMap();
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
