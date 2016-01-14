package ca.nbsoft.whereareyou.ui.map;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.nbsoft.whereareyou.Contact;
import ca.nbsoft.whereareyou.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {
    static final String TAG = MapFragment.class.getSimpleName();
    GoogleMap mMap=null;
    Map<String,Contact> mContacts = new HashMap<>();
    CameraUpdate mCameraUpdate=null;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMapAsync(this);
    }

    public void addContactMarker( Contact contact, boolean centerMap)
    {
        Log.d(TAG,"addContactMarker");
        mContacts.put(contact.getUserId(),contact);
        if(centerMap)
            mCameraUpdate = CameraUpdateFactory.newLatLngZoom(contact.getLatLong(), 18);
        if(mMap!=null)
        {
            showMarker(contact);
            if(centerMap)
                centerCamera();
        }
    }

    private void centerCamera() {
        if(mCameraUpdate!=null)
        {
            mMap.moveCamera(mCameraUpdate);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady");
        mMap = googleMap;
        showMarkers();
    }

    private void showMarker(Contact contact) {

        LatLng contactPos = contact.getLatLong();

        mMap.addMarker(new MarkerOptions().position(contactPos).title(contact.getDisplayName()));
    }


    private void showMarkers() {
        for( Contact c: mContacts.values())
        {
            showMarker(c);
        }
        centerCamera();
    }
}
