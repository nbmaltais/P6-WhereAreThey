package ca.nbsoft.whereareyou.ui.main;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.TileOverlay;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.Utils;

public class AddContactActivity extends AppCompatActivity {

    @Bind(R.id.email_view)
    EditText mEmailView;


    BroadcastReceiver mReceiver = new ApiService.ResultBroadcastReceiver(){
        @Override
        public void onSendContactRequestResult(ApiService.Result resultCode) {
            hideProgressDialog();
            if( resultCode.getResultCode() == ApiService.Result.RESULT_SUCCESS)
            {
                //mEmailView.getText();
                Snackbar.make(mEmailView, R.string.add_contact_succeeded,Snackbar.LENGTH_LONG).show();
            }
            else
            {
                if( resultCode.getResultCode() == ApiService.Result.RESULT_BACKEND_ERROR_STATUSCODE
                &&resultCode.getSpecificResultCode() == ApiService.StatusCode.RESULT_NO_USER_WITH_EMAIL)
                {
                    onContactNotRegistered();
                }
            }
        }
    };

    private void onContactNotRegistered() {
        OnAddContactFailedDialog dialog = new OnAddContactFailedDialog();
        dialog.show(getSupportFragmentManager(),"tag");
    }

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ApiService.subscribeToResult(this, ApiService.ACTION_SEND_CONTACT_REQUEST, mReceiver);
    }

    @Override
    protected void onDestroy() {
        ApiService.unSubscribeFromResult(this, mReceiver);
        super.onDestroy();
    }

    @OnClick(R.id.add_button)
    void addContact()
    {
        String email = mEmailView.getText().toString();
        if(email.isEmpty())
            return;

        Utils.closeKeyboard(this);

        showProgressDialog();

        ApiService.sendContactRequest(this, email);
    }

    public static void startActivity(Context ctx) {
        Intent intent = new Intent(ctx,AddContactActivity.class);
        ctx.startActivity(intent);

    }

    // TODO: use a progressbar instead, ProgressDialog is discouraged
    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.add_contact_activity_progress));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void inviteContact() {
        // TODO
        Toast.makeText(this, R.string.feature_not_implemented, Toast.LENGTH_LONG).show();
    }

    static public class OnAddContactFailedDialog extends DialogFragment
    {
        public OnAddContactFailedDialog()
        {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final AddContactActivity activity = (AddContactActivity)getActivity();

            return new AlertDialog.Builder(activity)
                    //.setIcon(R.drawable.alert_dialog_dart_icon)
                    .setMessage(R.string.add_contact_activity_on_failed_dialog_text)
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    activity.inviteContact();
                                }
                            })
                    .setNegativeButton(R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {

                                }
                            }).create();
        }
    }




}
