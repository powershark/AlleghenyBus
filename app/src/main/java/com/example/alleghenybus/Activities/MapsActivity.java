package com.example.alleghenybus.Activities;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.alleghenybus.Beans.StopRoute;
import com.example.alleghenybus.Beans.StopsBean;
import com.example.alleghenybus.R;
import com.example.alleghenybus.Utils.PermissionUtils;
import com.example.alleghenybus.Xmlparser.GetStopsXmlParser;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private List<StopsBean> stopList = new ArrayList<>();
    private Marker mSelectedMarker;
    private Map<Marker,StopsBean> stopMarkerMap = new HashMap<>();
    private List<Marker> stopMarkerList = new ArrayList<>();
    private List<Marker> destMarkerList;
    private List<Marker> srcMarkerList;
    List<StopRoute> destStopRoutesList;
    List<StopRoute> srcStopRoutesList;
    private List<StopRoute> topStopsList;
    private Location myLocation;
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
            PlaceAutocompleteFragment fromAutocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.from_place_autocomplete_fragment);
            LinearLayout fromSearchBarLayout = (LinearLayout)findViewById(R.id.fromSearchBarLayout);
            View line = findViewById(R.id.theLine);
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                fromAutocompleteFragment.setHint("Current Location");
                fromSearchBarLayout.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                Log.i(TAG, "Place: " + place.getName());
                if (marker!=null) marker.remove();
                marker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).draggable(true));
                marker.setIcon(BitmapDescriptorFactory.fromAsset("dest_marker.png"));
                marker.setTag("Destination");
                marker.setSnippet(place.getName().toString());
                marker.setTitle(place.getName().toString());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

                destMarkerList = new ArrayList<>();
                srcMarkerList = new ArrayList<>();

                Location destLocation = new Location("destLocation");
                destLocation.setLatitude(marker.getPosition().latitude);
                destLocation.setLongitude(marker.getPosition().longitude);

                for(Marker m : stopMarkerList){
                    Location stopLocation = new Location("stopLocation");
                    stopLocation.setLatitude( m.getPosition().latitude);
                    stopLocation.setLongitude(m.getPosition().longitude);
                    if (destLocation.distanceTo(stopLocation)<400){
                        destMarkerList.add(m);
                    }
                    if(myLocation.distanceTo(stopLocation)<400){
                        srcMarkerList.add(m);
                    }

                }

                Log.e("stopsList", destMarkerList.toString());
                Log.e("srcList", srcMarkerList.toString());


                TopRoutesTask topRoutesTask = new TopRoutesTask();
                //ProgressDialog.show(getBaseContext(),"q","q",false);
                topRoutesTask.execute(destMarkerList, srcMarkerList);

                //Log.e("topStopsList", topStopsList.toString());


                // Add fragment
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
        intent.putExtra("topStopsList", (Serializable) topStopsList);
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

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(mMap.getCameraPosition().zoom<16.00){
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
//        List<String> routes = stop.getRoutes();
//        StringBuilder snip = new StringBuilder("Routes: ");
//        for(String s : routes){
//            snip.append(s + ", ");
//        }
//        snip.delete(snip.length() - 2, snip.length());
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(stop.getLatitute(), stop.getLontitute()))
                .title(stop.getStpName()));
//        marker.setSnippet(snip.toString());
//        marker.setIcon(BitmapDescriptorFactory.fromAsset("bus_marker.png"));
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
                            Intent stopRoutesIntent = new Intent(MapsActivity.this,PredictionActivity.class);
                            stopRoutesIntent.putExtra("xmlResponse",response);
                            MapsActivity.this.startActivity(stopRoutesIntent);
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
            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (myLocation != null) {
                currentLatitude = myLocation.getLatitude();
                currentLongitude = myLocation.getLongitude();
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
            myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (myLocation != null) {
                currentLatitude = myLocation.getLatitude();
                currentLongitude = myLocation.getLongitude();
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




    private class TopRoutesTask extends AsyncTask<List<Marker>, Integer, List<StopRoute>> {

        protected List<StopRoute> doInBackground(List<Marker>... lists) {
            topStopsList = new ArrayList<StopRoute>();
            return calculateTopStops(lists[0],lists[1]);
        }

        @Override
        protected void onPostExecute(List<StopRoute> stopRoutes) {
            super.onPostExecute(stopRoutes);
            Log.e("topStopsList", topStopsList.toString());
        }

        public List<StopRoute> calculateTopStops(List<Marker> destMarkerList, List<Marker>srcMarkerList){
            srcStopRoutesList = new ArrayList<>();
            destStopRoutesList = new ArrayList<>();
            for(Marker m : destMarkerList) {
                String url = "http://realtime.portauthority.org/bustime/api/v3/getpredictions?key=5fYCfDAUi4ZteFN5bFpH9JRwW&stpid=" + stopMarkerMap.get(m).getStpId() + "&rtpidatafeed=Port%20Authority%20Bus";
                try {
                    String xml = sendGet(url);
                    setUpData(xml, destStopRoutesList, stopMarkerMap.get(m).getStpName());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            for(Marker m : srcMarkerList){
                String url ="http://realtime.portauthority.org/bustime/api/v3/getpredictions?key=5fYCfDAUi4ZteFN5bFpH9JRwW&stpid="+stopMarkerMap.get(m).getStpId()+"&rtpidatafeed=Port%20Authority%20Bus";
                try {
                    String xml = sendGet(url);
                    setUpData(xml, srcStopRoutesList, stopMarkerMap.get(m).getStpName());
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
            //ProgressDialog.show(this, "Loading", "Wait while loading...");


            Log.e("srcStopList",srcStopRoutesList.toString());
            Log.e("destStopList",destStopRoutesList.toString());


            for(StopRoute srcRoute : srcStopRoutesList){
                for(StopRoute destRoute : destStopRoutesList){
                    if(srcRoute.equals(destRoute)){
                        srcRoute.setDestStop(destRoute.getArrStop());
                        int busTime = Integer.parseInt(destRoute.getEta()) - Integer.parseInt(srcRoute.getEta());
                        if(busTime <= 0 || srcRoute.getArrStop().equals(srcRoute.getDestStop())) continue;
                        srcRoute.setBusTime(String.valueOf(busTime));
                        topStopsList.add(srcRoute);
                        break;
                    }
                }
            }
                Log.e("topinThread",topStopsList.toString());
            return topStopsList;
        }

        public void setUpData(String xmlResponse, List<StopRoute> finalList, String stopName) throws XmlPullParserException, IOException {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            StopRoute stopRoute = null;
            String text = null;
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader(xmlResponse));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("prd")) {
                            stopRoute = new StopRoute();
                            Log.e("**", stopName);
                            stopRoute.setArrStop(new String(stopName));
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("rt")) {
                            stopRoute.setRouteId(text);
                        } else if (tagname.equalsIgnoreCase("rtdir")) {
                            stopRoute.setDirection(text);

                        } else if (tagname.equalsIgnoreCase("vid")) {
                            stopRoute.setVid(text);
                        } else if (tagname.equalsIgnoreCase("prdctdn")){
                            if (text.equalsIgnoreCase("due"))
                                stopRoute.setEta("0");
                            else stopRoute.setEta(text);
                        }else if (tagname.equalsIgnoreCase("prd")){
                                finalList.add(stopRoute);
                        }
                    default:
                        break;
                }
                eventType = xpp.next();

            }
//            Log.e("srcStopList",srcStopRoutesList.toString());
//            Log.e("destStopList",destStopRoutesList.toString());
        }
    }

    private String sendGet(String url) throws Exception {


        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString();
    }
}

