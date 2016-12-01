package com.example.fixit.facebookexample;

import android.*;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    public GoogleMap mMap;
    Marker myLocation = null;
    //LocationManager lm;
    Location loc;
    String provider;
    LatLng tallahassee = new LatLng(30.44, -84.29);
    String response;
    String myFBID;
    ArrayList<mUser> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        myFBID = bundle.getString("FBID");
        users = new ArrayList<mUser>();
        Log.i("LLLLL", myFBID);
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
        mMap.setMyLocationEnabled(true);
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = lm.getBestProvider(new Criteria(), true);
        if (provider != null) {
            loc = lm.getLastKnownLocation(provider);
            if (loc != null) {
                mMap.clear();
                LatLng current = new LatLng(loc.getLatitude(), loc.getLongitude());
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
                            url = new URL("http://98.230.35.254:39048/insert?FBid="+myFBID+"&lat="+loc.getLatitude()+"&lon="+loc.getLongitude());
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
                            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            response=in.readLine();
                            Log.d("Read: ","response -> "+response);
                            if(response != null){
                                try {
                                    url = new URL("http://98.230.35.254:39048/pollusers?FBid="+myFBID);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }

                                urlConnection = null;
                                try {
                                    urlConnection = (HttpURLConnection) url.openConnection();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    BufferedReader in2 = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                    String response2=in2.readLine();
                                    Log.d("Read: ","response2 -> "+response2);
                                    JSONArray jArray = new JSONArray(response2);
                                    users.clear();
                                    for(int i=0; i<jArray.length(); i++){
                                        try{
                                            JSONObject oneObject = jArray.getJSONObject(i);
                                            String UserName = oneObject.getString("user_name");
                                            String InterestName = oneObject.getString("name");
                                            Double Lat = oneObject.getDouble("lat");
                                            Double Lon = oneObject.getDouble("lon");
                                            Integer id = oneObject.getInt("id");
                                            //If id does not exist
                                                // Create user at location (lat,lon)
                                                // With name user_name
                                                // Add InterestName to user's interests
                                            //Else
                                                // Add InterestName to user's interests
                                            boolean flag = false;
                                            for(mUser u : users){
                                                if(id == u.getUid()){
                                                    u.myInterests.add(InterestName);
                                                    flag = true;
                                                    break;
                                                }
                                            }
                                            if(!flag){
                                                users.add(new mUser(id,UserName,InterestName,Lat,Lon));
                                            }
                                        }catch(JSONException e) {

                                        }
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mMap.clear();
                                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                @Override
                                                public boolean onMarkerClick(Marker marker) {
                                                    Intent intent = new Intent(MapsActivity.this,UserDialog.class);
                                                    intent.putExtra("Name",marker.getTitle());
                                                    intent.putExtra("Interests",marker.getSnippet());
                                                    startActivity(intent);
                                                    return true;
                                                };
                                            });
                                            if(!users.isEmpty()) {
                                                for (mUser u : users) {
                                                    Marker m = mMap.addMarker(u.getMarkerOptions());

                                                }
                                            }
                                        }
                                    });


                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    urlConnection.disconnect();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            urlConnection.disconnect();
                        }
                    }
                });
                thread.start();
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

class mUser{
    Integer uid;
    String user_name;
    ArrayList<String> myInterests = new ArrayList<String>();
    Double myLat;
    Double myLon;
    MarkerOptions m;

    mUser(){
        uid = 0;
        user_name = "";
        myLat = 0.0;
        myLon = 0.0;
    }

    mUser(Integer u, String n, String i, Double la, Double lo){
        uid = u;
        user_name = n;
        myInterests.add(i);
        myLat = la;
        myLon = lo;
    }

    void addInterest(String i){
        myInterests.add(i);
    }

    int getUid(){
        return uid;
    }

    String getInterests(){
        String CompiledInterests = "";
        for(String s : myInterests){
            CompiledInterests += s;
            CompiledInterests += "\n";
        }
        return CompiledInterests;
    }
    MarkerOptions getMarkerOptions(){
        m = new MarkerOptions().position(new LatLng(myLat,myLon)).title(user_name).snippet(getInterests());

        return m;
    }
}