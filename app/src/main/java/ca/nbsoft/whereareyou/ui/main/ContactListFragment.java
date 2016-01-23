package ca.nbsoft.whereareyou.ui.main;


import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.PreferenceUtils;
import ca.nbsoft.whereareyou.common.ContactStatus;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.contact.ContactSelection;
import ca.nbsoft.whereareyou.ui.contact.ContactDetailActivity;
import ca.nbsoft.whereareyou.ui.main.ContactAdapter;

/**
 *
 */
public class ContactListFragment extends Fragment implements LoaderCallbacks<Cursor>, ContactAdapter.OnItemClickCallback {

    private static final String TAG = ContactListFragment.class.getSimpleName();
    @Bind (R.id.contact_list)
    RecyclerView mRecyclerView;

    private String mAccountName;

    private ContactAdapter mAdapter;

    private int LOADER_CONTACT = 0;
    private int LOADER_CONTACT_WAITING_CONFIRMATION = 1;

    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new ContactAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAccountName = PreferenceUtils.getAccountName(getContext());

        getLoaderManager().initLoader(LOADER_CONTACT, null, this);
        //getLoaderManager().initLoader(LOADER_CONTACT_WAITING_CONFIRMATION, null, this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_refresh_contacts)
        {
            ApiService.updateContactList(getContext());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");

        if(id == LOADER_CONTACT) {
            ContactSelection where = new ContactSelection();
            where.account(mAccountName);
            //where.status(ContactStatus.NONE);

            CursorLoader loader = new CursorLoader(getActivity(), where.uri(), ContactColumns.ALL_COLUMNS,
                    where.sel(), where.args(), where.order());

            return loader;
        }
        else if(id == LOADER_CONTACT_WAITING_CONFIRMATION)
        {
            ContactSelection where = new ContactSelection();
            where.account(mAccountName);
            where.status(ContactStatus.WAITING_FOR_CONFIRMATION);

            CursorLoader loader = new CursorLoader(getActivity(), where.uri(), ContactColumns.ALL_COLUMNS,
                    where.sel(), where.args(), where.order());

            return loader;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onCreateLoader");
        if(loader.getId() == LOADER_CONTACT) {
            mAdapter.setContactCursor(new ContactCursor(data));
        }
        else if(loader.getId() == LOADER_CONTACT_WAITING_CONFIRMATION)
        {
            mAdapter.setWaitingForConfirmationCursor(new ContactCursor(data));
        }

    }

    @Override
    public void onLoaderReset( Loader<Cursor> loader) {

    }


    @Override
    public void onContactItemClicked(String userId, View transitionView) {
        //ContactDetailActivity.startActivity(getContext(),userId);
        ContactDetailActivity.startActivityWithTransition(getActivity(), userId, transitionView );
    }
}
