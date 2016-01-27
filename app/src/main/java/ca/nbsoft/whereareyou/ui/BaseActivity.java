package ca.nbsoft.whereareyou.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.BuildConfig;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.PlayServicesUtils;
import ca.nbsoft.whereareyou.Utility.PreferenceUtils;

import static com.google.android.gms.common.api.CommonStatusCodes.SIGN_IN_REQUIRED;

/**
 * Created by Nicolas on 2015-12-14.
 * Base class for activity requiring the signed in account
 */
public  class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    static final String TAG = BaseActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private GoogleSignInAccount mSignInAccount;
    private GoogleSignInOptions mGoogleSignInOptions;

    // Method to override in subclass to react to sign in
    protected  void onSignedIn(GoogleSignInAccount signInAccount)
    {}
    protected  void onSignedOut()
    {}
    protected  void onAccessRevoked()
    {}
    protected  void onSignInFailed(GoogleSignInResult googleSignInResult)
    {}
    protected  void onSignInRequired()
    {}
    protected GoogleSignInAccount getGoogleSignInAccount(){
        return mSignInAccount;
    }

    protected GoogleSignInOptions gso() {return mGoogleSignInOptions;}

    protected void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            onSignedOut();
                        }
                    }
                });
    }


    protected void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    onAccessRevoked();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            if(!PlayServicesUtils.checkPlayServices(this))
            {
                Log.e(TAG, "Play service is not installed.");
                // TODO
            }
            else
            {
                setupPlayServices();
            }
        } catch (PlayServicesUtils.PlayServicesNotSupported playServicesNotSupported) {
            // TODO
            Log.e(TAG,"Play service is not supported.");
            finish();
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        doSilentSignIn();
    }


    private void setupPlayServices() {
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                        //.requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();
    }

    private void doSilentSignIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            Log.d(TAG, "Handling silent sign in");

            //showSignInProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //hideSignInProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult googleSignInResult) {

        if(googleSignInResult.isSuccess())
        {
            mSignInAccount = googleSignInResult.getSignInAccount();
            onSignedIn(mSignInAccount);
        }
        else
        {
            if( googleSignInResult.getStatus().getStatusCode() == SIGN_IN_REQUIRED)
            {
                onSignInRequired();
            }
            else
            {
                onSignInFailed(googleSignInResult);
            }
        }
    }



    protected void registerDeviceIfNeeded() {

        boolean registered = PreferenceUtils.getSentRegistrationToBackend(this);

        if(!registered )
        {
            ApiService.registerDevice(this);
        }
        else if(BuildConfig.isLocalServer)
        {
            // Only with debug server, refresh the contact list since it may be invalid if the
            // server was restarted
            ApiService.updateContactList(this);
        }


    }

    protected void forceRegisterDevice()
    {
        PreferenceUtils.setSentRegistrationToBackend(this,false);
        registerDeviceIfNeeded();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == PlayServicesUtils.PLAY_SERVICES_RESOLUTION_REQUEST )
        {
            doSilentSignIn();
        }
        else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "GoogleApiClient onConnectionFailed " + connectionResult);
    }

    protected void showSignInProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.base_activity_signin_progress_caption));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    protected void hideSignInProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
