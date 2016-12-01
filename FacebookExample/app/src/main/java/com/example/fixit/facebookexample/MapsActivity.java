package com.example.fixit.facebookexample;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    Marker myLocation = null;
    //LocationManager lm;
    Location loc;
    String provider;
    LatLng tallahassee = new LatLng(30.44, -84.29);
    String response;

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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myLocation = mMap.addMarker(new MarkerOptions()
                .position(tallahassee)
                .title("Self")
                .snippet("Default")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.setMyLocationEnabled(true);
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = lm.getBestProvider(new Criteria(), true);
        if (provider != null) {
            loc = lm.getLastKnownLocation(provider);
            if (loc != null) {
                mMap.clear();
                LatLng current = new LatLng(loc.getLatitude(), loc.getLongitude());
                myLocation = mMap.addMarker(new MarkerOptions()
                        .position(current)
                        .title("Self")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 14));
            }
        }

        mMap.setOnInfoWindowClickListener(this);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new LocationListener() {
            public void onLocationChanged(Location location) {
                mMap.clear();
                loc = location;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        URL url = null;
                        try {
                            url = new URL("http://98.230.35.254:39048/insert?FBid=1234TEST1234&lat="+loc.getLatitude()+"&lon="+loc.getLongitude());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        HttpURLConnection urlConnection = null;
                        try {
                            urlConnection = (HttpURLConnection) url.openConnection();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            response = in.toString();
                            Log.d("Read: ","response -> "+response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            urlConnection.disconnect();
                        }
                    }
                });
                thread.start();
                myLocation = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                .title("Self")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                // Called when a new location is found by the network location provider.
                //Here we put the code which will A) Update the location of all the other users
                //B) Will upload your current location to the database
                /*
                loc = location
                add code to push and pull from database
                //some sort of communication with the database
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
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.getTitle().equals("Self")){
            Log.i("self", "Clicked self");

        }
        else
            Log.i("other", "Clicked " + marker.getTitle());
        /*
        send request for contact information to clicked marker
        message via fb api?
        */
    }
}
