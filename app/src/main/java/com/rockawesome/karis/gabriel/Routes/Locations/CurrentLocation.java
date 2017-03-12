package com.rockawesome.karis.gabriel.Routes.Locations;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.location.LocationListener;

import com.google.android.gms.maps.model.LatLng;
import com.rockawesome.karis.gabriel.Routes.MapRoute;

/**
 * Created by karis on 3/12/2017.
 */

public class CurrentLocation extends AppCompatActivity implements LocationListener {
    private LatLng location;
    private MapRoute map;

    public CurrentLocation() {
        this.location = null;
        this.map = null;
    }

    @Override
    public void onLocationChanged(Location currentLocation) {
        if(currentLocation != null) {
            this.location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            if(this.map != null) this.map.updateCurrentLocation(this.location);
        }
    }

    public void onProviderEnabled(String provider) {}

    public void onProviderDisabled(String provider) {}

    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public LatLng getCurrentLocation() {
        if(this.location != null)
            return this.location;
        return null;
    }

    // Two-way connection so the current location marker will be updated when the location changes
    public void connectWithMap(MapRoute map) {
        this.map = map;
    }
}
