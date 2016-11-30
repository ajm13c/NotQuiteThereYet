package com.example.fixit.facebookexample;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    Marker myLocation = null;
    LocationManager lm;
    Location loc;
    String provider;
    LatLng tallahassee = new LatLng(30.44, -84.29);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tallahassee, 14));
        mMap.setOnMapLongClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        //mMap.setMyLocationEnabled(true);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = lm.getBestProvider(new Criteria(), true);
        if(provider != null){
            loc = lm.getLastKnownLocation(provider);
            if(loc != null){
                LatLng current = new LatLng(loc.getLatitude(), loc.getLongitude());
                myLocation = mMap.addMarker(new MarkerOptions()
                        .position(current)
                        .title("Self")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 14));
            }
            else{
                myLocation = mMap.addMarker(new MarkerOptions()
                                            .position(tallahassee)
                                            .title("Self")
                                            .snippet("Default value")
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }
        }
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        loc = lm.getLastKnownLocation(provider);
        /*
        * This is why it crashes ^^^ Use this method:
        *
        LocationManager locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //Here we put the code which will A) Update the location of all the other users
                //B) Will upload your current location to the database
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        });
        * */
        //clear all markers from map, then add relevant markers back.
        mMap.clear();
        if(loc != null) {
            myLocation = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                    .title("self")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
        else{
            Log.i("loc", "Unable to get current location, using past value or default");
        }
        /*
        add code to push and pull from database
        int num_results = SQL query to get all users with X+ interests in common
        float distance;
        Location target = new Location("");
        for(int i = 0; i < num_results; i++){
                set target latitude and longitude according to query results
                distance = loc.distanceTo(target);
                if(distance < Some arbitrary constant distance){  //means target has enough common likes and is in range
                   Marker temp = mMap.addMarker(new MarkerOptions().position(pos_from_db).title(name_from_db).snippet(common_interests))
                }
         }
         */
        //Example add marker only to test InfoWindowClick
        LatLng arr[] = new LatLng[2];
        arr[0] = latLng;
        arr[1] = new LatLng(30.44, -84.29);
        for(int i = 0; i < 2; i++) {
            Marker temp = mMap.addMarker(new MarkerOptions().position(arr[i]).title("Testing" + i).snippet("info"));
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.getTitle().equals("Self")){
            Log.i("self", "Clicked self");
            return;
        }
        else
            Log.i("other", "Clicked " + marker.getTitle());
        /*
        send request for contact information to clicked marker
        message via fb api?
        */
    }
}
