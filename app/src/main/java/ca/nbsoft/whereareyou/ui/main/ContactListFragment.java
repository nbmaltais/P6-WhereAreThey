package ca.nbsoft.whereareyou.ui.main;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.provider.contact.ContactColumns;
import ca.nbsoft.whereareyou.provider.contact.ContactCursor;
import ca.nbsoft.whereareyou.provider.contact.ContactSelection;
import ca.nbsoft.whereareyou.ui.main.ContactAdapter;

/**
 *
 */
public class ContactListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind (R.id.contact_list)
    RecyclerView mRecyclerView;

    ContactAdapter mAdapter;

    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new ContactAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));

        return view;
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

        ContactSelection where = new ContactSelection();


        CursorLoader loader = new CursorLoader(getActivity(),where.uri(), ContactColumns.ALL_COLUMNS,
                where.sel(),where.args(), where.order());

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setContactCursor(new ContactCursor(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
