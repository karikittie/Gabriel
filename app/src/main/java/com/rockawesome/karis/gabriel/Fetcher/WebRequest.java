package com.rockawesome.karis.gabriel.Fetcher;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rockawesome.karis.gabriel.R;
import com.rockawesome.karis.gabriel.Routes.MapRoute;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by karis on 3/12/2017.
 */

public class WebRequest extends Application {
    // Various trimet requests to build with
    private String baseURLRequest;
    private String vehicleURLAddon;
    private String routeURLAddon;
    private String appIDAddon;

    private JsonParser parser;
    private JsonObject cachedRouteRequest;
    private int routeCacheTimeout;
    private long lastRouteCache;
    private JsonObject cachedCarRequest;
    private long lastCarCache;
    private int carCacheTimeout;

    @Override
    public void onCreate() {
        super.onCreate();

        Resources r = getApplicationContext().getResources();
        this.baseURLRequest = r.getString(R.string.trimet_base_url);
        this.vehicleURLAddon = r.getString(R.string.trimet_vehicle_addon);
        this.routeURLAddon = r.getString(R.string.trimet_route_addon);
        this.appIDAddon = r.getString(R.string.trimet_app_key);

        this.cachedRouteRequest = null;
        this.parser = new JsonParser();
        this.lastRouteCache = 0;
        this.routeCacheTimeout = 300000;
        this.cachedCarRequest = null;
        this.lastCarCache = 0;
        this.carCacheTimeout = 60000;
    }

    public JsonObject blockRouteLoad(boolean forceUpdate) {
        if(System.currentTimeMillis() > (this.lastRouteCache + this.routeCacheTimeout) || forceUpdate) {
            String response = executeRequest(this.baseURLRequest
                                            + this.routeURLAddon
                                            + appIDAddon);
            if(response == null) parseError(new IOException("Response was empty"));
            this.cachedRouteRequest = (JsonObject) this.parser.parse(response);
            this.lastRouteCache = System.currentTimeMillis();
        }
        if(this.cachedRouteRequest == null) return blockRouteLoad(true);
        return this.cachedRouteRequest;
    }

    public JsonObject blockCarLoad(boolean forceUpdate) {
        if(System.currentTimeMillis() > (this.lastCarCache + this.carCacheTimeout) || forceUpdate) {
            String response = executeRequest(this.baseURLRequest
                                            + this.vehicleURLAddon
                                            + appIDAddon);
            if(response == null) parseError(new IOException("Response was empty"));
            this.cachedCarRequest = (JsonObject) this.parser.parse(response);
            this.lastCarCache = System.currentTimeMillis();
        }
        if(this.cachedRouteRequest == null) return blockCarLoad(true);
        return this.cachedCarRequest;
    }

    private String executeRequest(String url) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            try {
                return connection.getResponseMessage();
            } finally {
                connection.disconnect();
            }
        } catch(MalformedURLException error) {
            connectionError(error);
        } catch(IOException error) {
            parseError(error);
        }
        return null;
    }

    private void connectionError(Exception error) {
        Log.e(error.getCause().toString(), error.getMessage());
        AlertDialog errorAlert = new AlertDialog.Builder(WebRequest.this).create();
        errorAlert.setTitle("Connection Error");
        errorAlert.setMessage("Gabriel encountered an error when trying to connect to TriMet");
    }

    private void parseError(Exception error) {
        Log.e(error.getCause().toString(), error.getMessage());
        AlertDialog errorAlert = new AlertDialog.Builder(WebRequest.this).create();
        errorAlert.setTitle("Connection Error");
        errorAlert.setMessage("Gabriel encountered an error when trying to interpret the TriMet data");
    }
}
