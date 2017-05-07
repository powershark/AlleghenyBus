package com.example.alleghenybus.Activities;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.alleghenybus.Utils.PermissionUtils;
import com.example.alleghenybus.R;
import com.example.alleghenybus.Beans.RoutesBean;
import com.example.alleghenybus.Beans.StopsBean;
import com.example.alleghenybus.Xmlparser.GetStopsXmlParser;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        OnInfoWindowClickListener,
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
    private Map<Marker,StopsBean> stopMarkerMap = new HashMap<>();
    private List<Marker> stopMarkerList = new ArrayList<>();
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        // Get the SupportMapFragment and register for the callback
        // when the map is ready for use.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // For the auto complete fragment
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            public static final String TAG = "place fragment";
            public Marker marker;
            FragmentManager fragmentManager = getFragmentManager();
            RoutesFragment routesFragment;
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                if (marker!=null) marker.remove();
                marker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).draggable(true));
                marker.setIcon(BitmapDescriptorFactory.fromAsset("dest_marker.png"));
                marker.setTag("Destination");
                marker.setSnippet(place.getName().toString());
                marker.setTitle(place.getName().toString());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(routesFragment==null) {
                    routesFragment = new RoutesFragment();
                    fragmentTransaction.add(R.id.fragment_container, routesFragment, "routes");

                } else {
                    fragmentTransaction.detach(routesFragment);
                    fragmentTransaction.attach(routesFragment);
                }
                fragmentTransaction.commit();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });



    }

    //Jump to RouteSelect activity
    public void onRecommendRouteFragmentClicked(View v){
        Intent intent = new Intent(MapsActivity.this,SelectRouteActivity.class);
        startActivityForResult(intent,1);
    }

    //Jump to BookMark activity
    public void onFabClicked(View v){
        Intent intent = new Intent(MapsActivity.this,BookMarkActivity.class);
        startActivityForResult(intent,1);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready for use.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //to show the my location bar
        mMap.setPadding(0,200,0,0);
        mMap.setOnInfoWindowClickListener(this);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style_json));

            if (!success) {
                Log.e("Map Styling" , "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Map Styling", "Can't find style. Error: ", e);
        }

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(this);
        enableMyLocation();
        // Focuses on current location
        focusOnCurrentLocation();

        // Gets all bus stops on the map
        renderAllStops();

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if(mMap.getCameraPosition().zoom<17.00){
                    for (Marker m : stopMarkerList){
                        m.setVisible(false);
                    }
                } else {
                    for (Marker m : stopMarkerList){
                        m.setVisible(true);
                    }
                }
                Log.e("ZOOM", String.valueOf(mMap.getCameraPosition().zoom));
            }
        });
    }







    /**
     * Renders all the bus stops on the map.
     */
    public void renderAllStops(){
        try {
            GetStopsXmlParser getStopsXmlParser = new GetStopsXmlParser();
            stopList = getStopsXmlParser.parse(this.getResources().openRawResource(R.raw.stops));
            for(StopsBean s : stopList){
                addMarkersToMap(s);
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Function marks stopbeans on the map
     * @param stop type StopBean
     */
    private void addMarkersToMap(StopsBean stop) {
        List<String> routes = stop.getRoutes();
        StringBuilder snip = new StringBuilder("Routes: ");
        for(String s : routes){
            snip.append(s + ", ");
        }
        snip.delete(snip.length() - 2, snip.length());

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(stop.getLatitute(), stop.getLontitute()))
                .title(stop.getStpName()));
        marker.setTag("bus_stops");
        stopMarkerList.add(marker);
        stopMarkerMap.put(marker,stop);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (stopMarkerMap.containsKey(marker)) {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url ="http://realtime.portauthority.org/bustime/api/v3/getpredictions?key=5fYCfDAUi4ZteFN5bFpH9JRwW&stpid="+stopMarkerMap.get(marker).getStpId()+"&rtpidatafeed=Port%20Authority%20Bus";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Intent stopRoutesIntent = new Intent(getBaseContext(),PredictionActivity.class);
                            stopRoutesIntent.putExtra("xmlResponse",response);
                            getBaseContext().startActivity(stopRoutesIntent);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error","No Internet Connection/PAAC API down");
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

        Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
    }

    //Get info of routes&stops.xml and mark the stops.xml
    

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

