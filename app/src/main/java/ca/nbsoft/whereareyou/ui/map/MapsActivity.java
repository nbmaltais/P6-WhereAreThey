package ca.nbsoft.whereareyou.ui.map;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ca.nbsoft.whereareyou.Constants;
import ca.nbsoft.whereareyou.Contact;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.PreferenceUtils;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.contact.ContactSelection;

public class MapsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MapsActivity.class.getSimpleName();
    public static final String ACTION_SHOW_CONTACT = "ca.nbsoft.whereareyou.ui.map.action.SHOW_CONTACT";
    public static final String ACTION_SHOW_ALL_CONTACTS = "ca.nbsoft.whereareyou.ui.map.action.SHOW_ALL_CONTACTS";

    MapFragment mMapFragment;

    static public Intent getShowContactIntent(Context ctx, Contact contact )
    {
        Intent intent = new Intent(ctx, MapsActivity.class);
        intent.setAction(ACTION_SHOW_CONTACT);
        intent.putExtra(Constants.EXTRA_CONTACT, contact);
        return intent;
    }

    static public void startShowContact( Context ctx, Contact contact )
    {
        Intent intent = getShowContactIntent(ctx,contact);

        ctx.startActivity(intent);
    }

    static public void startShowAllContacts( Context ctx )
    {
        Intent intent = new Intent(ctx, MapsActivity.class);
        intent.setAction(ACTION_SHOW_ALL_CONTACTS);

        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       mMapFragment = (MapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switch(getIntent().getAction())
        {
            case ACTION_SHOW_CONTACT:
                Contact contact = getIntent().getParcelableExtra(Constants.EXTRA_CONTACT);
                mMapFragment.addContactMarker(contact,true);
                break;
            case ACTION_SHOW_ALL_CONTACTS:
                getLoaderManager().initLoader(0,null,this);
                break;
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String accountName = PreferenceUtils.getAccountName(this);
        ContactSelection sel = new ContactSelection();
        sel.account(accountName);
        //sel.positionTimestampGt();

        return new CursorLoader(this,sel.uri(), ContactColumns.ALL_COLUMNS,sel.sel(),sel.args(),sel.order());

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ContactCursor cursor = new ContactCursor(data);
        while(cursor.moveToNext())
        {
            Contact c = Contact.fromCursor(cursor);
            mMapFragment.addContactMarker(c,false);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
