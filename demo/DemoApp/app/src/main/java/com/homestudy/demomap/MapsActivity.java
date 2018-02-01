package com.homestudy.demomap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mFirebaseTransportRef;
    private LatLng location;
    public Marker previousMarker;
    public Polyline polyline = null;
    public List<LatLng> mLocationList = new ArrayList<>();
    private boolean mIsStartPointAdded;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        showdialogTest();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    1);
            return;
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String deviceId = telephonyManager.getDeviceId(1);
        String path = getString(R.string.firebase_path);
        mFirebaseTransportRef = FirebaseDatabase.getInstance().getReference(path);

        mFirebaseTransportRef.addChildEventListener(new ChildEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                onDataChange(snapshot, deviceId, true);
                mIsStartPointAdded = true;
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String s) {
                onDataChange(snapshot, deviceId, false);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(getApplicationContext(), "data removed : " + dataSnapshot.getKey() , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void showdialogTest() {
//        CustomDialog customDialog=new CustomDialog(this,AMBULANCE_REQUEST);
//        customDialog.showCustomDialog("Alert","Accept Ambulance" ,"ACCEPT",
//                "CACEL",MapsActivity.this);
//    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void onDataChange(DataSnapshot snapshot, String deviceId, boolean isStartPoint) {
        if (snapshot.getKey().equals(deviceId)) {
            Double lat = (Double) ((HashMap) snapshot.getValue()).get("lat");
            Double lng = (Double) ((HashMap) snapshot.getValue()).get("lng");

            LatLng newLocation = new LatLng(lat, lng);

            if (!mIsStartPointAdded) {
                if (previousMarker != null) {
                    previousMarker.remove();
                }
            }
            else {
                mIsStartPointAdded = false;
            }
            previousMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(getIcon(isStartPoint)).position(newLocation));
            if (polyline == null) {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.add(newLocation);
                polylineOptions.color(Color.MAGENTA);
                polylineOptions.startCap(new ButtCap());
                polylineOptions.endCap(new ButtCap());
                polylineOptions.width(10);
                polyline = mMap.addPolyline(polylineOptions);

//                    Toast.makeText(getApplicationContext(), "start with : " + mLocationList.size() , Toast.LENGTH_SHORT).show();

                mLocationList.add(newLocation);
            } else {


//                    mMap.clear();
                mLocationList.add(newLocation);
                polyline.setPoints(mLocationList);
//                    PolylineOptions polylineOptions = new PolylineOptions();
//                    polylineOptions.addAll(mLocationList);
//                    polylineOptions.color(getColor(R.color.colorPrimary));
//                    polylineOptions.startCap(new ButtCap());
//                    polylineOptions.endCap(new ButtCap());
//                    polylineOptions.width(10);
//                    mMap.addPolyline(polylineOptions);
//                    Toast.makeText(getApplicationContext(), "continued with : " + mLocationList.size() , Toast.LENGTH_SHORT).show();


            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 18));
        }
    }

    @NonNull
    private BitmapDescriptor getIcon(boolean isStartPoint) {
        if (isStartPoint){
            int d = 50;
            Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            Paint p = new Paint();
            p.setColor(getResources().getColor(R.color.black));
            c.drawCircle(d/2, d/2, d/2, p);

            // generate BitmapDescriptor from circle Bitmap
            BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);
            return bmD;
        }
        return bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_first_aid);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(52.43732, 10.81098);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
