package ca.nbsoft.whereareyou.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ca.nbsoft.whereareyou.PreferenceUtils;
import ca.nbsoft.whereareyou.ui.login.LoginActivity;

/**
 * Created by Nicolas on 2015-12-14.
 */
public class BaseActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        signinIfNeeded();
        super.onCreate(savedInstanceState);
    }

    private void signinIfNeeded() {
        String accountName = PreferenceUtils.getAccountName(this);
        boolean registered = PreferenceUtils.getSentRegistrationToBackend(this);
        if(!registered || accountName==null)
        {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish(); // remove from back stack
        }
    }
}
