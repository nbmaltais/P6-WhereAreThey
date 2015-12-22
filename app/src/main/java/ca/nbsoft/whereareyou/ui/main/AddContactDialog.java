package ca.nbsoft.whereareyou.ui.main;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddContactDialog extends DialogFragment {


    public AddContactDialog() {
        // Required empty public constructor
    }


    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.fragment_add_contact, null);
        final EditText mTextEdit = (EditText)rootView.findViewById(R.id.email_view);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton(R.string.add_contact_fragment_add_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String email = mTextEdit.getText().toString();
                        ApiService.sendContactRequest(getContext(),email);

                        // TODO: wait for success/failure
                    }
                })
                .setNegativeButton(R.string.add_contact_fragment_cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddContactDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

}
