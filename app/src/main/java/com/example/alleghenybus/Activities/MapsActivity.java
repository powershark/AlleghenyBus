package com.example.alleghenybus.Activities;

import com.example.alleghenybus.Utils.PermissionUtils;
import com.example.alleghenybus.R;
import com.example.alleghenybus.Beans.RoutesBean;
import com.example.alleghenybus.Beans.StopsBean;
import com.example.alleghenybus.Xmlparser.GetRouteDirectionsXmlParser;
import com.example.alleghenybus.Xmlparser.GetRoutesXmlParser;
import com.example.alleghenybus.Xmlparser.GetStopsXmlParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class MapsActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    private List<RoutesBean> routeList;
    private List<StopsBean> stopList = new ArrayList<StopsBean>();
    private Marker mSelectedMarker;

    private class InfoGenerator implements Runnable {

        @Override
        public void run() {
            try {
                //get routes
                URL url = new URL("http://realtime.portauthority.org/bustime/api/v1/getroutes?key=5fYCfDAUi4ZteFN5bFpH9JRwW");
                InputStream in = url.openStream();
                GetRoutesXmlParser routesXmlParserparser = new GetRoutesXmlParser();
                routeList = routesXmlParserparser.parse(in);

                //get route directions
                for (int i = 0; i < routeList.size(); i++) {
                    url = new URL("http://realtime.portauthority.org/bustime/api/v1/getdirections?key=5fYCfDAUi4ZteFN5bFpH9JRwW&rt="
                            + routeList.get(i).getRouteId());
                    in = url.openStream();
                    GetRouteDirectionsXmlParser routesDirectionXmlParser = new GetRouteDirectionsXmlParser();
                    routeList.get(i).setRouteDirections(routesDirectionXmlParser.parse(in));
                }

                //get stops
                for(int i = 0; i < routeList.size(); i++){
                    for(int j = 0; j < routeList.get(i).getRouteDirections().size(); j++) {
                        url = new URL("http://realtime.portauthority.org/bustime/api/v1/getstops?key=5fYCfDAUi4ZteFN5bFpH9JRwW&rt="
                                + routeList.get(i).getRouteId() + "&dir=" + routeList.get(i).getRouteDirections().get(j));
                        in = url.openStream();
                        GetStopsXmlParser stopsXmlParser = new GetStopsXmlParser();
                        List<StopsBean> temp = stopsXmlParser.parse(in);
                        for(int k = 0; k < temp.size(); k++) {
                            if (!stopList.contains(temp.get(k))) {
                                stopList.add(temp.get(k));
                            }
                        }
                    }
                }
                System.out.println(stopList.size());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(this);
        enableMyLocation();
        focusOnCurrentLocation();
        Toast.makeText(this, "Geting Stops", Toast.LENGTH_SHORT).show();
        generateInfo();
    }

    //Get info of routes&stops
    public void generateInfo() {
        //create a thread to do the http requests
        InfoGenerator p = new InfoGenerator();
        Thread t = new Thread(p);
        t.start();
        //check if the thread is done
        while(t.isAlive()) try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //if done, put the markers on the map
        for(int i = 0; i < stopList.size(); i++)
            addMarkersToMap(stopList.get(i));
        Toast.makeText(this, "Stops got", Toast.LENGTH_SHORT).show();
    }

    private void addMarkersToMap(StopsBean stop) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(stop.getLatitute(), stop.getLontitute()))
                .title(stop.getStpName()));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // The user has re-tapped on the marker which was already showing an info window.
        if (marker.equals(mSelectedMarker)) {
            // The showing info window has already been closed - that's the first thing to happen
            // when any marker is clicked.
            // Return true to indicate we have consumed the event and that we do not want the
            // the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            mSelectedMarker = null;
            return true;
        }

        mSelectedMarker = marker;

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        focusOnCurrentLocation();
        return false;
    }

    public void focusOnCurrentLocation() {
        double currentLatitude = 0;
        double currentLongitude = 0;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
            }
        } else {
            LocationListener locationListener = new LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }

                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Log.e("Map", "Location changed : Lat: "
                                + location.getLatitude() + " Lng: "
                                + location.getLongitude());
                    }
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
            }
        }
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        float zoomLevel = 16; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}

