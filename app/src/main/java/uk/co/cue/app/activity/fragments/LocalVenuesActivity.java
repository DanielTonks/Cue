package uk.co.cue.app.activity.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.activity.VenueDetails;
import uk.co.cue.app.objects.Venue;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;

public class LocalVenuesActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest{

    private final int FINE_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient locManager;
    private CueApp app;
    private VolleyRequestFactory volleyRequest;
    private GoogleMap map = null;
    private Location current_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_venues);

        setTitle("Local Venues");
        app = (CueApp) getApplication();
        this.volleyRequest = new VolleyRequestFactory(this, getApplicationContext());

        SupportMapFragment mapFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView));
        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

            }
        });

        locManager =  LocationServices.getFusedLocationProviderClient(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setMessage("Cue wants to know your location.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestLocPerms();
                            }
                        })
                        .create()
                        .show();
            } else {
                requestLocPerms();
            }
        } else {
            getLocation();
        }


    }

    public void getLocalVenues() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("latitude", String.valueOf(current_pos.getLatitude()));
        parameters.put("longitude", String.valueOf(current_pos.getLongitude()));
        System.out.println("Latitude at time of GET: " + current_pos.getLatitude());
        System.out.println("Longitude at time of GET: " + current_pos.getLongitude());
        volleyRequest.doRequest(app.GET_local_venues, parameters, Request.Method.GET);
    }

    public void requestLocPerms() {
        ActivityCompat.requestPermissions(LocalVenuesActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case FINE_LOCATION_PERMISSION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {

                }
                return;
            }
        }
    }

    public void getLocation() {
        try {
            locManager.getLastLocation()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Failed to get last known location!");
                        }
                    })
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null) {
                                current_pos = location;
                                System.out.println("Current latitude: "+ current_pos.getLatitude());
                                System.out.println("Current longitude: "+ current_pos.getLongitude());
                            }
                            getLocalVenues();
                        }
                    });
        } catch (SecurityException e) {
            System.out.println("Problem with getting permissions");
        }
    }

    @Override
    public void requestFinished(JSONObject response, String url) {
        try {
            if (url.contains(app.GET_local_venues)) {
                System.out.println("JSon response: "+ response);
                int size = response.getJSONArray("Nearby").length();

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for(int i = 0; i < size; i++) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    JSONObject obj = response.getJSONArray("Nearby").getJSONObject(i);
                    double lat = obj.getDouble("latitude");
                    double lon = obj.getDouble("longitude");
                    LatLng loc = new LatLng(lat, lon);
                    Venue venue = new Venue(
                        obj.getInt("venue_id"),
                        obj.getString("venue_name"),
                        lat,
                        lon,
                        obj.getString("google_token")
                    );
                    String place_name = obj.getString("venue_name");
                    markerOptions.position(loc);
                    markerOptions.title(place_name);

                    builder.include(markerOptions.getPosition());

                    Marker marker = map.addMarker(markerOptions);
                    marker.setTag(venue);
                }
                LatLngBounds bounds = builder.build();


                //LatLng currentPos = new LatLng(current_pos.getLatitude(), current_pos.getLongitude());
//                CameraPosition cameraPosition = new CameraPosition.Builder()
//                        .target(currentPos)
//                        .zoom(16)
//                        .bearing(0)
//                        .tilt(0).build();

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                //map.animateCamera(cu);
                map.moveCamera(cu);

                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Venue ven = (Venue) marker.getTag();
                        Intent intent = new Intent(getApplicationContext(), VenueDetails.class);
                        intent.putExtra("venue", ven);
                        startActivity(intent);
                    }
                });
                //map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
    }

    @Override
    public void requestFailed(int statusCode) {
        System.out.println("Error with server: " + statusCode);
    }
}
