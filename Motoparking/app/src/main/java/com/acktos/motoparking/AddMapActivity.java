package com.acktos.motoparking;


import android.app.ActionBar;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class AddMapActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {


    private GoogleApiClient mGoogleApiClient;
    private GoogleMap map;
    private Marker marker;
    private ActionBar ab;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private Integer KEY_ZOOM = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map=map;
        this.map.setMyLocationEnabled(true);
        this.map.setOnInfoWindowClickListener(this);

        marker = map.addMarker(new MarkerOptions().position(new LatLng(0, 0)));

        this.map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                LatLng coordinates;
                coordinates = new LatLng(point.latitude, point.longitude);

                marker.setTitle(getString(R.string.add));
                marker.setPosition(coordinates);
                String address = toAddress(coordinates.latitude, coordinates.longitude);
                ab = getActionBar();
                ab.setSubtitle(address);
            }
        });

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);  // LocationListener

        LatLng coordinates;
        Location pos = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double lat = pos.getLatitude();
        double lon = pos.getLongitude();

        try {
            String coordinates_intent = getIntent().getExtras().getString("coordinates");
            String[] latitude = coordinates_intent.split(",");
            Double lat_intent = Double.parseDouble(latitude[0]);
            Double lon_intent = Double.parseDouble(latitude[1]);
            coordinates = new LatLng(lat_intent, lon_intent);
        } catch (NullPointerException e) {
            coordinates = new LatLng(lat, lon);
        }

        marker.setTitle(getString(R.string.add));
        marker.setPosition(coordinates);

        String address=toAddress(coordinates.latitude,coordinates.longitude);
        ab = getActionBar();
        ab.setSubtitle(address);

        CameraUpdate cam= CameraUpdateFactory.newLatLngZoom(coordinates, KEY_ZOOM);
        GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        map.animateCamera(cam);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addmap:
                LatLng latLng = marker.getPosition();
                double lat = latLng.latitude;
                double lon = latLng.longitude;

                String coordinates = String.valueOf(lat)+","+String.valueOf(lon);
                String address=toAddress(lat,lon);
                //address = String.valueOf(lat);

                Intent intent_name = new Intent();
                intent_name.setClass(getApplicationContext(),AddParkingDataActivity.class);
                intent_name.putExtra("address",address);
                intent_name.putExtra("coordinates",coordinates);
                startActivity(intent_name);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String toAddress(Double lat, Double lon)
    {
        String result = null;
        Geocoder geocoder = new Geocoder(AddMapActivity.this);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat,lon,1);
            if(addresses.size()>0) {
                if (addresses.get(0) != null) {
                    Address address = addresses.get(0);
                    result = address.getAddressLine(0);
                    return result;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            result="No especificada";

        }
        return result;
    }
}
