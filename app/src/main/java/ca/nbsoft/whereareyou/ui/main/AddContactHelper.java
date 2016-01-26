package ca.nbsoft.whereareyou.ui.main;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.ui.ErrorMessages;

/**
 * Created by Nicolas on 2016-01-18.
 */
public class AddContactHelper extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PICK_CONTACT_REQUEST = 10345;
    private static final int CONFIRM_CONTACT = 10346;
    private static final String TAG = AddContactHelper.class.getSimpleName();
    private String mEmail;

    interface Client
    {
        void showProgressDialog(String text);
        void hideProgressDialog();
    }

    Client mClient;


    BroadcastReceiver mReceiver = new ApiService.ResultBroadcastReceiver()
    {

        @Override
        public void onSendContactRequestResult(ApiService.Result resultCode, Bundle args) {

            View v = getActivity().findViewById(android.R.id.content);
            mClient.hideProgressDialog();
            if( resultCode.getResultCode() == ApiService.Result.RESULT_SUCCESS)
            {
                //mEmailView.getText();
                Snackbar.make(v, R.string.add_contact_succeeded, Snackbar.LENGTH_LONG).show();
            }
            else
            {
                boolean handled = AddContactFailedDialog.handleOnAddContactFailed(getActivity(), resultCode);
                if(!handled)
                    ErrorMessages.showErrorMessage(getContext(),resultCode);
            }
        }
    };

    public AddContactHelper()
    {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mClient = (Client)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();

        ApiService.subscribeToResult(getContext(), ApiService.ACTION_SEND_CONTACT_REQUEST, mReceiver);

    }

    @Override
    public void onPause() {
        ApiService.unSubscribeFromResult(getContext(), mReceiver);
        super.onPause();
    }

    public void pickContact()
    {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE); // Show user only contacts w/ email
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                loadContact(data);
            }
        }
        else if(requestCode == CONFIRM_CONTACT)
        {
            if (resultCode == Activity.RESULT_OK) {
                addContactByEmail(mEmail);
            }
        }
    }

    private void loadContact(Intent intent) {

        Uri contactUti = intent.getData();
        Bundle args = new Bundle();
        args.putParcelable("URI",contactUti);
        getLoaderManager().restartLoader(0, args, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contactUri = args.getParcelable("URI");

        Log.d(TAG, "onCreateLoader: Loading contact " + contactUri);

        String[] projection = {ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Nickname.NAME};
        CursorLoader loader = new CursorLoader(getContext(),contactUri,projection,null,null,null);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG,"onLoadFinished, email count = " + cursor.getCount());

        if(cursor.moveToFirst()) {

            int emailColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            int nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME);
            mEmail = cursor.getString(emailColumn);
            final String name = cursor.getString(nameColumn);
            Log.d(TAG, "onLoadFinished: got email:" + mEmail);


            // We can't create the dialog from the loader callback, so
            // post it
            Handler handler = new Handler();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    addContactByEmailWithConfirmation(name,mEmail);
                }
            });

        }
    }

    private void addContactByEmailWithConfirmation(String name,String email) {
        ConfirmAddContactDialog d = ConfirmAddContactDialog.newInstance(this,name,email);

        d.show(getFragmentManager(),"ConfirmAddContactDialog");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void addContactByEmail(String email) {
        mClient.showProgressDialog(getString(R.string.add_contact_activity_progress));
        ApiService.sendContactRequest(getContext(), email);
    }

    static public class ConfirmAddContactDialog extends DialogFragment
    {


        static public ConfirmAddContactDialog newInstance( AddContactHelper fragment, String name, String email)
        {
            ConfirmAddContactDialog f = new ConfirmAddContactDialog();
            Bundle args = new Bundle();
            args.putString("name",name);
            args.putString("email", email);
            f.setArguments(args);
            f.setTargetFragment(fragment,CONFIRM_CONTACT);
            return f;
        }

        public ConfirmAddContactDialog()
        {

        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Activity activity = getActivity();

            Bundle args = getArguments();
            String name = args.getString("name");
            String email = args.getString("email");

            String message = getString(R.string.add_contact_activity_confirm_dialog_text,name);

            return new AlertDialog.Builder(activity)
                    //.setIcon(R.drawable.alert_dialog_dart_icon)
                    .setMessage(message)
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    getTargetFragment().onActivityResult(getTargetRequestCode(),Activity.RESULT_OK,null);
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    getTargetFragment().onActivityResult(getTargetRequestCode(),Activity.RESULT_CANCELED,null);
                                }
                            }).create();

        }
    }
}
