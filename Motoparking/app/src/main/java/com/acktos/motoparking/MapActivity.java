package com.acktos.motoparking;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acktos.motoparking.controllers.ParkingController;
import com.acktos.motoparking.models.Parking;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private ArrayList<Parking> parkings;
    public String coordenadas;

    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge;
            // Use the equals() method on a Marker to check for equals.  Do not use ==.
            badge = R.drawable.logo_32;
            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
                String title_parking = String.valueOf(titleText);
                String title_parking_array[] = title_parking.split("¡");
                titleUi.setText(title_parking_array[1]);
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, snippetText.length() , 0);
                snippetUi.setText(snippetText);
            }
        }
    }


    private GoogleApiClient mGoogleApiClient;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private Integer KEY_ZOOM = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ParkingController parkingController = new ParkingController(MapActivity.this);

        try {
            this.parkings = parkingController.getFile();
        }
        catch(NullPointerException e)
        {
            Log.i("Toasty","Toasty");
        }

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
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        map.setOnInfoWindowClickListener(this);
        try {
            load_points(map, 0);
        }
        catch(NullPointerException e)
        {

        }
    }

    private void load_points(GoogleMap map, Integer option) {
        if(this.parkings.size()>0) {
            map.clear();

            switch (option) {
                case 0:
                    for (int i = 0; i < this.parkings.size(); i++) {
                        String coordinates = this.parkings.get(i).coordinates;
                        String address = this.parkings.get(i).address;
                        String[] latitude = coordinates.split(",");
                        Double lat = Double.parseDouble(latitude[0]);
                        Double lon = Double.parseDouble(latitude[1]);
                        MarkerOptions markerOptions = new MarkerOptions();
                        String see_more = getString(R.string.see_more);
                        address = i + "¡" + address;
                        map.addMarker(markerOptions.position(new LatLng(lat, lon)).snippet(see_more).title(address)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_32));
                    }
                break;
                case 1:
                    for (int i = 0; i < this.parkings.size(); i++) {
                        if(this.parkings.get(i).price_hour.equals("0") && this.parkings.get(i).price_minute.equals("0") && this.parkings.get(i).price_standard.equals("0")) {
                            String coordinates = this.parkings.get(i).coordinates;
                            String address = this.parkings.get(i).address;
                            String[] latitude = coordinates.split(",");
                            Double lat = Double.parseDouble(latitude[0]);
                            Double lon = Double.parseDouble(latitude[1]);
                            MarkerOptions markerOptions = new MarkerOptions();
                            String see_more = getString(R.string.see_more);
                            address = i + "¡" + address;
                            map.addMarker(markerOptions.position(new LatLng(lat, lon)).snippet(see_more).title(address)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_32));
                        }
                    }
                break;
                case 2:
                    for (int i = 0; i < this.parkings.size(); i++) {
                        if(Integer.parseInt(this.parkings.get(i).price_standard)>0) {
                            String coordinates = this.parkings.get(i).coordinates;
                            String address = this.parkings.get(i).address;
                            String[] latitude = coordinates.split(",");
                            Double lat = Double.parseDouble(latitude[0]);
                            Double lon = Double.parseDouble(latitude[1]);
                            MarkerOptions markerOptions = new MarkerOptions();
                            String see_more = getString(R.string.see_more);
                            address = i + "¡" + address;
                            map.addMarker(markerOptions.position(new LatLng(lat, lon)).snippet(see_more).title(address)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_32));
                        }
                    }
                break;
            }
        }
        else {

            Toast.makeText(getApplicationContext(), R.string.verify, Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);  // LocationListener
        Location pos = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double lat = pos.getLatitude();
        double lon = pos.getLongitude();
        coordenadas = String.valueOf(lat)+","+String.valueOf(lon);
        CameraUpdate cam= CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), KEY_ZOOM);
        GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        map.animateCamera(cam);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String id_marker = marker.getTitle();
        String title_parking = String.valueOf(id_marker);
        String title_parking_array[] = title_parking.split("¡");

        ArrayList<Parking> parkings;
        ParkingController parkingController = new ParkingController(MapActivity.this);
        parkings = parkingController.getFile();

        Integer id_int = Integer.parseInt(title_parking_array[0]);
        String data_to_sent = parkings.get(id_int).toJson();

        Intent intent_name = new Intent(getApplicationContext(),DetailActivity.class);
        intent_name.putExtra("data_to_sent",data_to_sent);
        intent_name.putExtra("coordenadas",coordenadas);
        startActivity(intent_name);
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
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addmap:
                Intent intent_name = new Intent();
                intent_name.setClass(getApplicationContext(),AddMapActivity.class);
                startActivity(intent_name);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadoptions(View v)
    {
        final CharSequence[] items = {"Gratis","Tarifa fija","Todos"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.label_select));
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
        int position = 0;

                switch(item)
                {
                    case 0:
                        position=1;
                        break;
                    case 1:
                        position=2;
                        break;
                    case 2:
                        position=0;
                        break;

                }
                dialog.dismiss();
                try{
                    GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
                    load_points(map,position);
                }
                catch (NullPointerException e)
                {
                    Log.i("Error", "No hay mapa");
                }
            }
        });
        AlertDialog levelDialog = builder.create();
        levelDialog.show();
    }

}
