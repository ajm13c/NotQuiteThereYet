package edu.fsu.cs.mobile.notquitethereyet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
    public static final String CONTACT_LIST_PARAM = "com.example.fixit.textingservice.extra.PARAM1";
    public static final String DISTANCE_PARAM = "com.example.fixit.textingservice.extra.PARAM2";
    public static final String LATITUDE_PARAM = "com.example.fixit.textingservice.extra.LATITUDE";
    public static final String LONGITUDE_PARAM = "com.example.fixit.textingservice.extra.LONGITUDE";

    public LocationManager locManager;
    public boolean Sent = false;
    public LocationListener mLL;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        String[] contacts = bundle.getStringArray(CONTACT_LIST_PARAM);
        int distance = bundle.getInt(DISTANCE_PARAM);
        double lat = bundle.getDouble(LATITUDE_PARAM);
        double lon = bundle.getDouble(LONGITUDE_PARAM);
        handleActionText(contacts, distance, lat, lon);
        Log.i("AAA", "lat lng = " + lat + "," + lon);
        return Service.START_NOT_STICKY;
    }

    public void onDestroy() {
        Log.d("dbg", "DBG: Ending Service.");
        if (mLL != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locManager.removeUpdates(mLL);
        }
    }

    ;

    private boolean checkToText(Location location, String[] numbers, int Distance, double Lat, double Lon) {
        Log.d("dbg", "DBG: GPS Moved");
        SmsManager smsManager = SmsManager.getDefault();
        Location targetLoc = new Location("");
        targetLoc.setLatitude(Lat);
        targetLoc.setLongitude(Lon);
        float distanceInMeters = location.distanceTo(targetLoc);
        Toast.makeText(getApplicationContext(), distanceInMeters + " away", Toast.LENGTH_SHORT).show();
        Log.d("dbg", "DBG: Distance is... " + distanceInMeters);
        for (String number : numbers) {
            if (distanceInMeters <= Distance && !Sent) {
                Log.d("dbg", "DBG: Entered Range!");
                Toast.makeText(getApplicationContext(), "sending text", Toast.LENGTH_SHORT).show();
                smsManager.sendTextMessage(number, null, "Hey, I'm "+distanceInMeters+" meters out!", null, null);
                Sent = true;
                return true;
            }
        }
        return false;
    }

    private void handleActionText(String[] param1, int param2, double param3, double param4) {
        final String[] p1 = param1;
        final int p2 = param2;
        final double p3 = param3;
        final double p4 = param4;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {

                Log.d("dbg", "DBG: Permission Denied");
                stopSelf();

            }

        }
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLL = new LocationListener(){
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                boolean end = checkToText(location, p1, p2, p3, p4);
                if (end) {
                    Intent intent = new Intent(getApplicationContext(), LocationService.class);
                    stopService(intent);
                }
            }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
        };

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLL);
    }

}
