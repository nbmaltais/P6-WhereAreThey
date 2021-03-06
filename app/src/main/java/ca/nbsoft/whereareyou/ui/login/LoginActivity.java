package ca.nbsoft.whereareyou.ui.login;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.Endpoints;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.PreferenceUtils;
import ca.nbsoft.whereareyou.common.StatusCode;
import ca.nbsoft.whereareyou.ui.BaseActivity;
import ca.nbsoft.whereareyou.ui.ErrorMessages;
import ca.nbsoft.whereareyou.ui.main.MainActivity;

/**
 * todo Handle network connctivity
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_ACCOUNT_PICKER = 1010;


    @Bind(R.id.signin_button)
    SignInButton mSignInButton;


    GoogleAccountCredential mCredential;

    BroadcastReceiver mReceiver = new ApiService.ResultBroadcastReceiver()
    {
        @Override
        public void onCreateAccountResult(ApiService.Result result, Bundle args) {
            if(result.isOk())
            {
                boolean alreadyExixts = result.getSpecificResultCode() == StatusCode.RESULT_ACCOUNT_ALREADY_EXISTS;
                onAccountCreated(alreadyExixts);
            }
            else
            {
                onOperationFailed(result);
            }
        }

        @Override
        public void onRegisterDeviceResult(ApiService.Result result, Bundle args) {
            if(result.isOk())
            {
                onDeviceRegistered(  );
            }
            else
            {
                onOperationFailed(result);
            }
        }
    };





    private void returnToMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish(); // clear from stack
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        String[] actions = {ApiService.ACTION_CREATE_ACCOUNT, ApiService.ACTION_REGISTER_DEVICE};
        ApiService.subscribeToResult(this, actions, mReceiver);
        mCredential = Endpoints.getCredential(this);

        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInButton.setScopes(gso().getScopeArray());

        /**mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginProcess();
            }
        });*/
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        ApiService.unSubscribeFromResult(this, mReceiver);
        super.onDestroy();
    }

    @OnClick(R.id.signin_button)
    void onSignInClicked()
    {
        signIn();
    }

    @Override
    protected void onSignedIn(GoogleSignInAccount signInAccount) {

        Log.d(TAG,"onSignedIn");
        String accountName = signInAccount.getEmail();
        PreferenceUtils.setAccountName(this, accountName);
        Log.d(TAG, "Account choosen: " + accountName);
        mCredential.setSelectedAccountName(accountName);

        showSignInProgressDialog();
        ApiService.createAccount(this, signInAccount.getDisplayName(), signInAccount.getPhotoUrl());
    }

    protected void onAccountCreated(boolean accountAlreadyExists)
    {
        Log.d(TAG, "onAccountCreated");
        //PreferenceUtils.setAccountCreated(this, true);
        ApiService.registerDevice(this);

        if(accountAlreadyExists)
        {
            ApiService.updateContactList(this);
        }
    }

    protected void onDeviceRegistered( )
    {
        Log.d(TAG,"onDeviceRegistered");
        returnToMainActivity();
    }

    private void onOperationFailed(ApiService.Result result) {
        hideSignInProgressDialog();
        ErrorMessages.showErrorMessage(this,result);
    }

    /*private void startLoginProcess() {
        if(mCredential.getSelectedAccountName()==null)
        {
            chooseAccount();
        }
        else
        {
            registerDevice();
        }
    }

    private void registerDevice() {
        if(!RegistrationIntentService.registerDeviceIfNeeded(this))
        {
            Log.d(TAG,"Registration already done.");
            // registration was not needed, we are done

            returnToMainActivity();
        }
        else
        {
            mStatusView.setText(R.string.login_message_registration_in_progress);
        }
    }

    void chooseAccount() {
        startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    void onChooseAccountResult( Intent data )
    {
        if (data != null && data.getExtras() != null) {
            String accountName = data.getExtras().getString( AccountManager.KEY_ACCOUNT_NAME);
            if (accountName != null) {
                PreferenceUtils.setAccountName(this, accountName);
                Log.d(TAG, "Account choosen: " + accountName);
                mCredential.setSelectedAccountName(accountName);
                Toast.makeText(this, "Logged into acount " + accountName, Toast.LENGTH_LONG).show();

                registerDevice();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                onChooseAccountResult(data);
                break;
        }
    }*/
}
