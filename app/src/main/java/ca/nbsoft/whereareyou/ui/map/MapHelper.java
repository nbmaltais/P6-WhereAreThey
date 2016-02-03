package ca.nbsoft.whereareyou.ui.map;


import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import ca.nbsoft.whereareyou.Contact;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapHelper  {
    static final String TAG = MapHelper.class.getSimpleName();
    GoogleMap mMap=null;
    Map<String,Contact> mContacts = new HashMap<>();
    CameraUpdate mCameraUpdate=null;

    public MapHelper() {

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
