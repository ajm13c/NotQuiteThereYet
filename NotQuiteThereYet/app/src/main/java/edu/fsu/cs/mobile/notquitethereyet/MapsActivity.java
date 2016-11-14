package edu.fsu.cs.mobile.notquitethereyet;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;

import static edu.fsu.cs.mobile.notquitethereyet.R.id.RadiusText;
import static edu.fsu.cs.mobile.notquitethereyet.R.id.SelectButton;
import static edu.fsu.cs.mobile.notquitethereyet.R.id.map;
import static edu.fsu.cs.mobile.notquitethereyet.R.styleable.View;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    Marker marker = null;
    Circle circle = null;
    CircleOptions circleOptions = null;
    EditText rad_text;
    Button cont_button;
    LatLng dest;
    int radius = 0; /* meters */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS}, 1);
        rad_text = (EditText) findViewById(RadiusText);
        cont_button = (Button) findViewById(SelectButton);
        rad_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!rad_text.getText().toString().isEmpty()) {
                    radius = Integer.parseInt(rad_text.getText().toString());
                    cont_button.setClickable(true);
                } else {
                    radius = 0;
                    cont_button.setClickable(false);
                }
            }
        });
    }


    @Override
    public void onMapLongClick(LatLng pnt) {
        Toast.makeText(this, "lat=" + pnt.latitude + ", lon=" + pnt.longitude, Toast.LENGTH_SHORT).show();
        dest = pnt;
        if (marker != null) {
            marker.setPosition(pnt);
            marker.setTitle("lat: " + pnt.latitude + ", Lon: " + pnt.longitude);
        } else {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(pnt)
                    .title("lat: " + pnt.latitude + ", Lon: " + pnt.longitude));
            marker.setPosition(pnt);
        }
        if (circle == null) {
            circleOptions = new CircleOptions()
                    .center(pnt)
                    .radius(radius)
                    .strokeColor(0xff00bf62)
                    .fillColor(0x5000ff00);
            circle = mMap.addCircle(circleOptions);
        } else {
            circle.setCenter(pnt);
            circle.setRadius(radius);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
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
        mMap.setOnMapLongClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = lm.getBestProvider(new Criteria(), true);
        if(provider != null){
            Location loc = lm.getLastKnownLocation(provider);
            if(loc != null){
                LatLng current = new LatLng(loc.getLatitude(), loc.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 14));
            }
        }
    }

    public void OnClickListener(android.view.View v){
        Intent Contacts = new Intent(this, StartContact.class);
        Contacts.putExtra("radius", radius);
        Contacts.putExtra("lat", dest.latitude);
        Contacts.putExtra("lng", dest.longitude);
        startActivity(Contacts);
    }
}
