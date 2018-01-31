package com.homestudy.demomap;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.LatLng;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
   CustomDialog.CustomDialogClickListener{

    private GoogleMap mMap;
    private DatabaseReference mFirebaseTransportRef;
    private LatLng location;
    private final int AMBULANCE_REQUEST=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        showdialogTest();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String path = getString(R.string.firebase_path);
        mFirebaseTransportRef = FirebaseDatabase.getInstance().getReference(path);

        mFirebaseTransportRef.addChildEventListener(new ChildEventListener() {
            public Polyline polyline = null;
            public List<LatLng> mLocationList = new ArrayList<>();

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
/*
                String key = "";
//                key = getKeyForPrevCase(snapshot);
//                key = (String) ((HashMap)snapshot.getValue()).keySet().toArray()[0];
                Double lat = (Double) ((HashMap) snapshot.getValue()).get("lat");
                Double lng = (Double) ((HashMap) snapshot.getValue()).get("lng");

                LatLng newLocation = new LatLng(lat, lng);

                mMap.addMarker(new MarkerOptions().position(newLocation));
                if (polyline == null){
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.add(newLocation);
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.startCap(new ButtCap());
                    polylineOptions.endCap(new ButtCap());
                    polylineOptions.width(10);
                    polyline = mMap.addPolyline(polylineOptions);

                    Toast.makeText(getApplicationContext(), "start with : " + mLocationList.size() , Toast.LENGTH_SHORT).show();

                    mLocationList.add(newLocation);
                }
                else {


//                    mMap.clear();
                    mLocationList.add(newLocation);
//                    PolylineOptions polylineOptions = new PolylineOptions();
//                    polylineOptions.addAll(mLocationList);
//                    polylineOptions.color(getColor(R.color.colorPrimary));
//                    polylineOptions.startCap(new ButtCap());
//                    polylineOptions.endCap(new ButtCap());
//                    polylineOptions.width(10);
//                    mMap.addPolyline(polylineOptions);
                    Toast.makeText(getApplicationContext(), "continued with : " + mLocationList.size() , Toast.LENGTH_SHORT).show();


                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 13));
*/

            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String s) {
                String key = "";
//                key = getKeyForPrevCase(snapshot);
//                key = (String) ((HashMap)snapshot.getValue()).keySet().toArray()[0];
                Double lat = (Double) ((HashMap) snapshot.getValue()).get("lat");
                Double lng = (Double) ((HashMap) snapshot.getValue()).get("lng");

                LatLng newLocation = new LatLng(lat, lng);

                mMap.addMarker(new MarkerOptions().position(newLocation));
                if (polyline == null){
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.add(newLocation);
                    polylineOptions.color(Color.MAGENTA);
                    polylineOptions.startCap(new ButtCap());
                    polylineOptions.endCap(new ButtCap());
                    polylineOptions.width(10);
                    polyline = mMap.addPolyline(polylineOptions);

                    Toast.makeText(getApplicationContext(), "start with : " + mLocationList.size() , Toast.LENGTH_SHORT).show();

                    mLocationList.add(newLocation);
                }
                else {


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
                    Toast.makeText(getApplicationContext(), "continued with : " + mLocationList.size() , Toast.LENGTH_SHORT).show();


                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 18));

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        mFirebaseTransportRef.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String key = "";
////                key = getKeyForPrevCase(snapshot);
//                key = (String) ((HashMap)snapshot.getValue()).keySet().toArray()[0];
//                Double lat = (Double) ((HashMap) ((HashMap) snapshot.getValue()).get("101")).get("lat");
//                Double lng = (Double) ((HashMap) ((HashMap) snapshot.getValue()).get("101")).get("lng");
//
//                location = new LatLng(lat, lng);
//
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(location);
//                markerOptions.title("location");
//                mMap.addMarker(markerOptions);
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
//                Log.d("Change", "Value is: " + key);
//            }
//
//            private String getKeyForPrevCase(DataSnapshot snapshot) {
//                String value = "";
//                Map.Entry entry = (Map.Entry) ((HashMap) snapshot.getValue(false)).entrySet().toArray()[0];
//                Double lat = (Double) ((HashMap) ((List) entry.getValue()).get(7)).get("lat");
//                Double lng = (Double) ((HashMap) ((List) entry.getValue()).get(7)).get("lng");
//                String key = (String) entry.getKey();
//
//                location = new LatLng(lat, lng);
//                return key;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w("Failed", "Failed to read value.", error.toException());
//            }
//        });
    }

    private void showdialogTest() {
        CustomDialog customDialog=new CustomDialog(this,AMBULANCE_REQUEST);
        customDialog.showCustomDialog("Alert","Accept Ambulance" ,"ACCEPT",
                "CACEL",this);
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
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    @Override
    public void onPositiveButtonClicked(DialogInterface dialog, int requestCode) {
        if (requestCode==AMBULANCE_REQUEST){
            Toast.makeText(this,"Request ACCEPT",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=20.5666,45.345"));
            startActivity(intent);
        }
    }

    @Override
    public void onNegativeButtonClicked(DialogInterface dialog, int requestCode) {
        if (requestCode==AMBULANCE_REQUEST){
            Toast.makeText(this,"Request CANCELLED",Toast.LENGTH_LONG).show();
        }
    }
}
