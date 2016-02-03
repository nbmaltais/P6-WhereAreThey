package ca.nbsoft.whereareyou.ui.contact;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.contact.ContactSelection;

public class ContactDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String EXTRA_CONTACT_USER_ID = "CONTACT_USER_ID";
    public static final String EXTRA_TRANSITION = "TRANSITION";// is there another way ?
    private static final String TAG = ContactDetailActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;

    private String mContactUserId;


    public static Intent getStartActivityIntent(Context ctx, String contactId) {
        Intent intent = new Intent(ctx,ContactDetailActivity.class);
        intent.putExtra(EXTRA_CONTACT_USER_ID,contactId);

        return intent;
    }

    static public void startActivity( Context ctx, String contactId )
    {
        Intent intent = new Intent(ctx,ContactDetailActivity.class);
        intent.putExtra(EXTRA_CONTACT_USER_ID,contactId);

        ctx.startActivity(intent);
    }

    public static void startActivityWithTransition(FragmentActivity context, String userId, View transitionView) {
        ActivityOptionsCompat sceneTransitionAnimation
                = ActivityOptionsCompat.makeSceneTransitionAnimation(context, transitionView,
                context.getString(R.string.transition_user_photo));

        // Starts the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();

        Intent intent = getStartActivityIntent(context,userId);
        intent.putExtra(EXTRA_TRANSITION,true);
        context.startActivity(intent,transitionBundle);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if(savedInstanceState==null)
        {
            supportPostponeEnterTransition();
        }



        final ContactDetailFragment fragment = (ContactDetailFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().getEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    Log.d(TAG, "onTransitionEnd");
                    fragment.onSharedElementEnd();
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });


        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContactUserId = getIntent().getStringExtra(EXTRA_CONTACT_USER_ID);
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        ContactSelection where = new ContactSelection();
        where.userid(mContactUserId);

        CursorLoader loader = new CursorLoader(this,where.uri(), ContactColumns.ALL_COLUMNS,
                where.sel(),where.args(),null);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            ContactCursor cursor = new ContactCursor(data);

            mCollapsingToolbarLayout.setTitle(cursor.getName());
            ContactDetailFragment contactFragment = (ContactDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

            contactFragment.bind(cursor);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
