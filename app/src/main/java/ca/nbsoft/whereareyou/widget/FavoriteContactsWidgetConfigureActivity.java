package ca.nbsoft.whereareyou.widget;

import android.app.Activity;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.PreferenceUtils;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.contact.ContactSelection;

/**
 * The configuration screen for the {@link FavoriteContactsWidget FavoriteContactsWidget} AppWidget.
 */
public class FavoriteContactsWidgetConfigureActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String PREFS_NAME = "ca.nbsoft.whereareyou.widget.FavoriteContactsWidget";
    private static final String PREF_NAME_KEY = "name_";
    private static final String PREF_WIDGET_PREFIX = "appwidget_";
    private static final String PREF_USERID_KEY = "userid_";
    private static final int MAX_CONTACT = 3;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Bind(R.id.contact1)
    Spinner mContactSpinner1;
    @Bind(R.id.contact2)
    Spinner mContactSpinner2;
    @Bind(R.id.contact3)
    Spinner mContactSpinner3;

    private String mAccountName;
    private ArrayAdapter<String> mAdapter;
    private ContactCursor mContactCursor;

    public FavoriteContactsWidgetConfigureActivity() {
        super();
    }

    static public String nameKey(int idx, int widgetid)
    {
        return PREF_NAME_KEY + idx + PREF_WIDGET_PREFIX + widgetid;
    }

    static public String userIdKey(int idx, int widgetid)
    {
        return PREF_USERID_KEY + idx + PREF_WIDGET_PREFIX + widgetid;
    }


    static public SharedPreferences getSharedPreferences(Context context)
    {
        return context.getSharedPreferences(PREFS_NAME, 0);
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.favorite_contacts_widget_configure);

        ButterKnife.bind(this);


        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        // Can't create a widget if we are not signed in
        mAccountName = PreferenceUtils.getAccountName(this);
        if(mAccountName==null)
        {
            finish();
            return;
        }

        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAdapter.add(getString(R.string.widget_configure_no_contact_selected));

        mContactSpinner1.setAdapter(mAdapter);
        mContactSpinner2.setAdapter(mAdapter);
        mContactSpinner3.setAdapter(mAdapter);

        getLoaderManager().initLoader(0,null,this);

    }

    @OnClick(R.id.add_button)
    void onAddWidgetClick(View v) {

        int pos[] = new int[3];
        pos[0] = mContactSpinner1.getSelectedItemPosition() - 1;
        pos[1] = mContactSpinner2.getSelectedItemPosition() - 1;
        pos[2] = mContactSpinner3.getSelectedItemPosition() - 1;


        SharedPreferences.Editor prefs = this.getSharedPreferences(PREFS_NAME, 0).edit();
        for( int idx =0;idx<3;idx++) {
            if (pos[idx] != -1) {
                mContactCursor.moveToPosition(pos[idx]);

                prefs.putString(nameKey(idx,mAppWidgetId), mContactCursor.getName());
                prefs.putString(userIdKey(idx, mAppWidgetId), mContactCursor.getUserid());
            } else {
                prefs.remove(nameKey(idx,mAppWidgetId));
                prefs.remove(userIdKey(idx, mAppWidgetId));
            }
        }

        // Save config values
        prefs.apply();

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        FavoriteContactsWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        ContactSelection sel = new ContactSelection();

        sel.account(mAccountName);
        sel.orderByName();

        String[] projection = {ContactColumns.USERID,ContactColumns.NAME};

        return new CursorLoader(this,sel.uri(),projection,sel.sel(),sel.args(),sel.order());

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mContactCursor  = new ContactCursor(data);


        while (mContactCursor.moveToNext())
        {
            mAdapter.add(mContactCursor.getName());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mContactCursor=null;
    }

    public static void deletePref(Context context, int appWidgetId) {
        SharedPreferences pref = getSharedPreferences(context);
        SharedPreferences.Editor edit = pref.edit();
        for(int i=0;i<MAX_CONTACT;i++)
        {
            edit.remove(nameKey(i,appWidgetId));
            edit.remove(userIdKey(i, appWidgetId));
        }

        edit.apply();
    }
}

