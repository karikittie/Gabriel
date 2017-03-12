package com.rockawesome.karis.gabriel.Routes;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rockawesome.karis.gabriel.R;
import com.rockawesome.karis.gabriel.Routes.Locations.CurrentLocation;

public class MapRoute extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private CurrentLocation currentLocation;
    private boolean foundLocation;
    private Marker locationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        // Add a marker to current location
        this.currentLocation = new CurrentLocation();
        connectWithCurrentLocation(); // Connect to CurrentLocation instance for on-the-fly updates
        long start = System.currentTimeMillis();
        long end = start + (15 * 1000); // 15 second GPS timeout
        LatLng location = null;
        while(location == null && System.currentTimeMillis() < end)
            location = this.currentLocation.getCurrentLocation();
        if(location == null) { // If no location, set flag and GTFO
            this.foundLocation = false;
            return;
        }
        // Otherwise, set a marker on the map
        this.locationMarker = this.mMap.addMarker(new MarkerOptions().position(location).title("Me"));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    // Two-way connection so the current location marker will be updated when the location changes
    public void connectWithCurrentLocation() {
        if(this.currentLocation != null) this.currentLocation.connectWithMap(this);
    }

    public void updateCurrentLocation(LatLng location) {
        if(this.locationMarker != null) this.locationMarker.remove(); // Remove old marker
        this.locationMarker = this.mMap.addMarker(new MarkerOptions().position(location).title("Me"));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}
