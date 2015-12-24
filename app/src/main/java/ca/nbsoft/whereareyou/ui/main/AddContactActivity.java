package ca.nbsoft.whereareyou.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.Utils;

public class AddContactActivity extends AppCompatActivity {

    @Bind(R.id.email_view)
    EditText mEmailView;


    BroadcastReceiver mReceiver = new ApiService.ResultBroadcastReceiver(){
        @Override
        public void onSendContactRequestResult(@ApiService.ResultCode int resultCode) {
            if( resultCode == ApiService.RESULT_SUCCESS)
            {
                //mEmailView.getText();
                Snackbar.make(mEmailView, R.string.add_contact_succeeded,Snackbar.LENGTH_SHORT).show();
            }
            else
            {
                Snackbar.make(mEmailView, R.string.add_contact_failed,Snackbar.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ApiService.subscribeToResult(this,ApiService.ACTION_SEND_CONTACT_REQUEST,mReceiver);
    }

    @Override
    protected void onDestroy() {
        ApiService.unSubscribeFromResult(this,mReceiver);
        super.onDestroy();
    }

    @OnClick(R.id.add_button)
    void addContact()
    {
        Utils.closeKeyboard(this);

        String email = mEmailView.getText().toString();
        ApiService.sendContactRequest(this, email);
    }

    public static void startActivity(Context ctx) {
        Intent intent = new Intent(ctx,AddContactActivity.class);
        ctx.startActivity(intent);

    }
}
