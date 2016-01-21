package ca.nbsoft.whereareyou.ui.main;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.ui.BaseActivity;
import ca.nbsoft.whereareyou.ui.ErrorMessages;
import ca.nbsoft.whereareyou.ui.login.LoginActivity;

public class MainActivity extends BaseActivity  implements AddContactHelper.Client{

    private static final int PICK_CONTACT_REQUEST = 1123;
    private static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.fab) FloatingActionButton mFab;

    AddContactHelper mAddContactHelper;

    BroadcastReceiver mReceiver = new ApiService.ResultBroadcastReceiver()
    {
        @Override
        public void onDeleteAccountResult(ApiService.Result result, Bundle args) {
            if(result.isOk())
            {
                registerDeviceIfNeeded();
            }
            else{
                ErrorMessages.showErrorMessage(MainActivity.this,result);
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if(savedInstanceState == null)
        {
            mAddContactHelper = new AddContactHelper();
            getSupportFragmentManager().beginTransaction().add(mAddContactHelper,"AddContactHelper").commit();
        }
        else
        {
            mAddContactHelper = (AddContactHelper)getSupportFragmentManager().findFragmentByTag("AddContactHelper");
        }


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: ask permission, if refused, got to AddContactActivity, else
                // use the pick contact intent

                //AddContactActivity.startActivity(MainActivity.this);

                mAddContactHelper.pickContact();
            }
        });


    }



    @Override
    protected void onResume() {
        super.onResume();
        String[] actions = {ApiService.ACTION_DELETE_ACCOUNT};
        ApiService.subscribeToResult(this, actions, mReceiver);
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
        else if(item.getItemId()==R.id.action_add_contact)
        {
            AddContactActivity.startActivity(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void askDeleteAccount() {
        DeleteAccountDialog d = new DeleteAccountDialog();
        d.show(getSupportFragmentManager(),"tag");
    }

    @Override
    public void showProgressDialog(String text) {
        // TODO
    }

    @Override
    public void hideProgressDialog() {
        // TODO
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
                    .setPositiveButton(R.string.main_activity_delete_account_dialog_confirm,
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
