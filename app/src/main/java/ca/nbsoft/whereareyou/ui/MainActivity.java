package ca.nbsoft.whereareyou.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.PreferenceUtils;
import ca.nbsoft.whereareyou.R;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.fab) FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        signinIfNeeded();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        setSupportActionBar(mToolbar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
