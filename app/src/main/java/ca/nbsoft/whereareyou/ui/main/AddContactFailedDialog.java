package ca.nbsoft.whereareyou.ui.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.common.StatusCode;

/**
 * Created by Nicolas on 2016-01-18.
 */
public class AddContactFailedDialog extends DialogFragment {




    static public boolean handleOnAddContactFailed( FragmentActivity activity, ApiService.Result resultCode)
    {
        if( resultCode.getResultCode() == ApiService.Result.RESULT_BACKEND_ERROR_STATUSCODE
                &&resultCode.getSpecificResultCode() == StatusCode.RESULT_NO_USER_WITH_EMAIL)
        {
            AddContactFailedDialog dialog = new AddContactFailedDialog();
            dialog.show(activity.getSupportFragmentManager(), "tag_AddContactFailedDialog");
            return true;
        }

        return false;
    }

    public AddContactFailedDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity activity = getActivity();

        return new AlertDialog.Builder(activity)
                //.setIcon(R.drawable.alert_dialog_dart_icon)
                .setMessage(R.string.add_contact_activity_on_failed_dialog_text)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                inviteContact();
                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                            }
                        }).create();
    }

    private void inviteContact() {
        Toast.makeText(getContext(), R.string.feature_not_implemented, Toast.LENGTH_LONG).show();
    }
}
