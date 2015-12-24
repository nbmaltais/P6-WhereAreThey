package ca.nbsoft.whereareyou.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ca.nbsoft.whereareyou.Utility.PlayServicesUtils;
import ca.nbsoft.whereareyou.Utility.PreferenceUtils;
import ca.nbsoft.whereareyou.ui.login.LoginActivity;

/**
 * Created by Nicolas on 2015-12-14.
 */
public class BaseActivity extends AppCompatActivity{
    static final String TAG = BaseActivity.class.getSimpleName();
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
                signinIfNeeded();
            }
        } catch (PlayServicesUtils.PlayServicesNotSupported playServicesNotSupported) {
            // TODO
            Log.e(TAG,"Play service is not supported.");
            finish();
        }

        super.onCreate(savedInstanceState);
    }

    protected void signinIfNeeded() {
        String userId = PreferenceUtils.getUserId(this);
        String accountName = PreferenceUtils.getAccountName(this);
        boolean registered = PreferenceUtils.getSentRegistrationToBackend(this);
        if(!registered || accountName==null || userId==null)
        {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish(); // remove from back stack
        }
    }

    protected void forceSignin()
    {
        PreferenceUtils.setSentRegistrationToBackend(this,false);
        signinIfNeeded();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == PlayServicesUtils.PLAY_SERVICES_RESOLUTION_REQUEST )
        {
            signinIfNeeded();
        }
    }
}
