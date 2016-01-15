package ca.nbsoft.whereareyou.ui.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.Utils;
import ca.nbsoft.whereareyou.ui.BaseActivity;
import ca.nbsoft.whereareyou.ui.login.LoginActivity;

public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.fab) FloatingActionButton mFab;

    BroadcastReceiver mReceiver = new ApiService.ResultBroadcastReceiver()
    {
        @Override
        public void onDeleteAccountResult(ApiService.Result result) {
            if(result.isOk())
            {
                registerDeviceIfNeeded();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                AddContactActivity.startActivity(MainActivity.this);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiService.subscribeToResult(this, ApiService.ACTION_DELETE_ACCOUNT, mReceiver);
    }

    @Override
    protected void onPause() {
        ApiService.unSubscribeFromResult(this, mReceiver);
        super.onPause();

    }

    @Override
    protected void onSignInRequired() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_delete_account)
        {
            askDeleteAccount();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void askDeleteAccount() {
        DeleteAccountDialog d = new DeleteAccountDialog();
        d.show(getSupportFragmentManager(),"tag");
    }


    static public class DeleteAccountDialog extends DialogFragment
    {
        public DeleteAccountDialog()
        {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final MainActivity activity = (MainActivity)getActivity();

            return new AlertDialog.Builder(activity)
                    //.setIcon(R.drawable.alert_dialog_dart_icon)
                    .setMessage(R.string.main_activity_delete_account_dialog_text)
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    ApiService.deleteAccount(activity);
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {

                                }
                            }).create();
        }
    }


}
