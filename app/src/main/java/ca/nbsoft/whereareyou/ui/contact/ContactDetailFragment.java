package ca.nbsoft.whereareyou.ui.contact;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import ca.nbsoft.whereareyou.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailFragment extends Fragment {


    public ContactDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_contact_detail, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

}
