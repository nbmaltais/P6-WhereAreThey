package ca.nbsoft.whereareyou.ui.map;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.nbsoft.whereareyou.Constants;
import ca.nbsoft.whereareyou.Contact;
import ca.nbsoft.whereareyou.R;

public class MapsActivity extends AppCompatActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    //Location mLocation;
    Contact mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //mLocation = getIntent().getParcelableExtra(Constants.EXTRA_LOCATION);
        mContact = getIntent().getParcelableExtra(Constants.EXTRA_CONTACT);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapFragment.addContactMarker(mContact,true);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at contact position and move the camera
        LatLng contactPos = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        Log.d(TAG,"onMapReady, latlong = " + contactPos);
        mMap.addMarker(new MarkerOptions().position(contactPos).title(mContact.getDisplayName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom (contactPos,18));

    }*/
}
