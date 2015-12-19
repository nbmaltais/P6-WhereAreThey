package ca.nbsoft.whereareyou.ui.contact;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.contact.ContactSelection;

public class ContactDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String EXTRA_CONTACT_USER_ID = "CONTACT_USER_ID";
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;

    private String mContactUserId;

    static public void startActivity( Context ctx, String contactId )
    {
        Intent intent = new Intent(ctx,ContactDetailActivity.class);
        intent.putExtra(EXTRA_CONTACT_USER_ID,contactId);

        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contact_detail);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContactUserId = getIntent().getStringExtra(EXTRA_CONTACT_USER_ID);
        getLoaderManager().initLoader(0,null,this);

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
        ContactCursor cursor = new ContactCursor(data);

        mCollapsingToolbarLayout.setTitle( cursor.getEmail() );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
