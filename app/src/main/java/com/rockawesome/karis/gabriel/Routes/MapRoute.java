package com.rockawesome.karis.gabriel.Routes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rockawesome.karis.gabriel.R;

public class MapRoute extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker locationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.locationMarker = null;
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(locationMarker != null) locationMarker.remove();
                LatLng currentLocation;
                if(location == null) {
                    try {
                        location = locationManager.getLastKnownLocation("gps");
                    } catch(SecurityException error) {
                        securityExceptionHandler(error);
                    }
                }
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                locationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Me"));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0F));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                        }, 1);
                return;
            }
        } else {
            startLocationServices();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startLocationServices();
        }
    }

    private void startLocationServices() {
        try {
            this.locationManager.requestLocationUpdates("gps", 5000, 5, this.locationListener);
        } catch(SecurityException error) {
            securityExceptionHandler(error);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        // Add a marker to current location

        // Otherwise, set a marker on the map

    }

    private void securityExceptionHandler(SecurityException error) {
        AlertDialog errorAlert = new AlertDialog.Builder(MapRoute.this).create();
        errorAlert.setTitle("Location Error");
        errorAlert.setMessage("Gabriel encountered an error when trying to access your location");
        errorAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "Go To Location Services", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
    }
}
