package com.homestudy.demomap;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.homestudy.demomap.entity.RequestObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends Activity implements com.google.android.gms.location.LocationListener {

    private DatabaseReference mFirebaseTransportRef;
    private LatLng location;
    private AlertDialog.Builder builder;
    private static String[] PERMISSIONS_REQUIRED = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient.ConnectionCallbacks mLocationRequestCallback = new GoogleApiClient
            .ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle bundle) {
            LocationRequest request = new LocationRequest();
            request.setInterval(10000);
            request.setFastestInterval(5000);
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    request, (com.google.android.gms.location.LocationListener) MainActivity.this);
        }

        @Override
        public void onConnectionSuspended(int reason) {
            // TODO: Handle gracefully
        }
    };

    private static final int PERMISSIONS_REQUEST = 1;
    private LocationRequest request;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonStart = findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                getLocationUpdates();
                sendNotificationToAllAmbs();
            }

            private void getLocationUpdates() {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION},
                            1);
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        request, MainActivity.this);
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mLocationRequestCallback)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        checkLocationPermission();
        String path = getString(R.string.firebase_request);
        mFirebaseTransportRef = FirebaseDatabase.getInstance().getReference(path);

        mFirebaseTransportRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(getApplicationContext(), "added child with key : " + dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    /**
     * Second validation check - ensures the app has location permissions, and
     * if not, requests them, otherwise runs the next check.
     */
    private void checkLocationPermission() {
        int locationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST);
        } else {
            checkGpsEnabled();
        }
    }

    /**
     * Third and final validation check - ensures GPS is enabled, and if not, prompts to
     * enable it, otherwise all checks pass so start the location tracking service.
     */
    private void checkGpsEnabled() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            reportGpsError();
        } else {
            resolveGpsError();
        }
    }

    private void reportGpsError() {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.activity_main), getString(R.string
                        .gps_required), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.enable, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();

    }

    private void resolveGpsError() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendNotificationToAllAmbs() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    1);
            return;
        }

        Map<String, Object> transportStatus = new HashMap<>();
        if (mLocation != null){
            location = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        }
        else
            location = new LatLng(52.425041, 10.788461);
        transportStatus.put("lat", location.latitude);
        transportStatus.put("lng", location.longitude);
        transportStatus.put("imei", telephonyManager.getDeviceId(1));

        mFirebaseTransportRef.push().setValue(transportStatus);
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            mLocation = location;
        }
    }
}
