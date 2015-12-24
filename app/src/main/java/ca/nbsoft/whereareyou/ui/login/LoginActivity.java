package ca.nbsoft.whereareyou.ui.login;

import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.Endpoints;
import ca.nbsoft.whereareyou.Utility.PreferenceUtils;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.gcm.RegistrationIntentService;
import ca.nbsoft.whereareyou.ui.main.MainActivity;

/**
 * todo Handle network connctivity
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_ACCOUNT_PICKER = 1010;

    @Bind(R.id.status_view)
    TextView mStatusView;

    @Bind(R.id.login_button)
    Button mLoginButton;


    GoogleAccountCredential mCredential;


    BroadcastReceiver mResultReceiver = new RegistrationIntentService.RegistrationResultReceiver() {

        @Override
        protected void onLoginFailed() {
            LoginActivity.this.onLoginFailed();
        }

        @Override
        protected void onLoginSucceeded() {
            LoginActivity.this.onLoginSucceeded();
        }
    };

    private void onLoginFailed() {
        Log.e(TAG,"Registration with backend failed.");

        mStatusView.setText(R.string.login_error_registration_failed);

        Toast.makeText(this,"Login failed",Toast.LENGTH_LONG).show();
    }

    private void onLoginSucceeded() {
        Log.d(TAG, "Registration with backend succeeded.");
        returnToMainActivity();
    }

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

        RegistrationIntentService.subscribeToResult(this, mResultReceiver);
        mCredential = Endpoints.getCredential(this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginProcess();
            }
        });

        startLoginProcess();
    }

    private void startLoginProcess() {
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

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        RegistrationIntentService.unSubscribeFromResult(this, mResultReceiver);
        super.onDestroy();
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
    }
}
