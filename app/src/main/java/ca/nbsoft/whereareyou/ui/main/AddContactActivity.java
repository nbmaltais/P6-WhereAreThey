package ca.nbsoft.whereareyou.ui.main;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.Utils;
import ca.nbsoft.whereareyou.common.StatusCode;

public class AddContactActivity extends AppCompatActivity implements AddContactHelper.Client {

    @Bind(R.id.email_view)
    EditText mEmailView;
    AddContactHelper mAddContactHelper;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        ButterKnife.bind(this);

        if(savedInstanceState == null)
        {
            mAddContactHelper = new AddContactHelper();
            getSupportFragmentManager().beginTransaction().add(mAddContactHelper,"AddContactHelper").commit();
        }
        else
        {
            mAddContactHelper = (AddContactHelper)getSupportFragmentManager().findFragmentByTag("AddContactHelper");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.add_button)
    void addContact()
    {
        String email = mEmailView.getText().toString();
        if(email.isEmpty())
            return;

        Utils.closeKeyboard(this);

        mAddContactHelper.addContactByEmail(email);


    }

    public static void startActivity(Context ctx) {
        Intent intent = new Intent(ctx,AddContactActivity.class);
        ctx.startActivity(intent);

    }

    @Override
    public void showProgressDialog(String text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(text);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }




}
