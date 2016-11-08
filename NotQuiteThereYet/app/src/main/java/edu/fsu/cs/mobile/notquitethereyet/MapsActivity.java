package edu.fsu.cs.mobile.notquitethereyet;

import android.app.PendingIntent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
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

import static edu.fsu.cs.mobile.notquitethereyet.R.id.map;
import static edu.fsu.cs.mobile.notquitethereyet.R.styleable.View;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener{

    private GoogleMap mMap;
    Marker marker = null;
    Circle circle = null;
    CircleOptions circleOptions = null;
    int radius = 1000; /* meters */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapLongClick(LatLng pnt){
        Toast.makeText(this, "lat="+pnt.latitude + ", lon="+pnt.longitude, Toast.LENGTH_SHORT).show();
        if (marker != null){
            marker.setPosition(pnt);
            marker.setTitle("lat: " + pnt.latitude + ", Lon: " + pnt.longitude);
        } else {
            marker = mMap.addMarker(new MarkerOptions()
            .position(pnt)
            .title("lat: " + pnt.latitude + ", Lon: " + pnt.longitude));
            marker.setPosition(pnt);
        }
        if (circle == null){
            circleOptions = new CircleOptions()
                    .center(pnt)
                    .radius(1000)
                    .strokeColor(0xff00bf62)
                    .fillColor(0x5000ff00);
            circle = mMap.addCircle(circleOptions);
        } else
            circle.setCenter(pnt);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
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
    }
}
