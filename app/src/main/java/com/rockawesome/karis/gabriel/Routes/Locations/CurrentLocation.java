package com.rockawesome.karis.gabriel.Routes.Locations;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.location.LocationListener;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by karis on 3/12/2017.
 */

public class CurrentLocation extends AppCompatActivity implements LocationListener {
    private LatLng location;
    private boolean activeLocation;

    public CurrentLocation() {
        this.location = null;
        this.activeLocation = false;
    }

    @Override
    public void onLocationChanged(Location currentLocation) {
        if(currentLocation != null) {
            this.location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            this.activeLocation = true;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        this.activeLocation = true;
    }

    @Override
    public void onProviderDisabled(String provider) {
        this.activeLocation = false;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public LatLng getCurrentLocation() {
        if(this.activeLocation)
            return this.location;
        return null;
    }
}
