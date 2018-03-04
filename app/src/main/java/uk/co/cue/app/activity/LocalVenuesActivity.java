package uk.co.cue.app.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;

public class LocalVenuesActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest{

    private double latitude;
    private double longitude;
    private LocationManager locManager;
    private LocationListener locListener;
    private final int FINE_LOCATION_PERMISSION = 1;
    private CueApp app;
    private VolleyRequestFactory volleyRequest;
    private GoogleMap map = null;
    LatLng current_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_venues);

        app = (CueApp) getApplication();
        this.volleyRequest = new VolleyRequestFactory(this, getApplicationContext());

        SupportMapFragment mapFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView));
        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

            }
        });

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                System.out.println("Current latitude:" + latitude);
                System.out.println("Current longitude:" + longitude);
                locManager.removeUpdates(this);
                current_pos = new LatLng(latitude, longitude);
                getLocalVenues();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

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
            attachLocManager();
        }
    }

    public void attachLocManager() {
        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locListener);
        } catch(SecurityException err) {

        }
    }

    public void getLocalVenues() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("latitude", String.valueOf(latitude));
        parameters.put("longitude", String.valueOf(longitude));
        System.out.println("Latitude at time of GET: " + latitude);
        System.out.println("Longitude at time of GET: " + longitude);

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
                    attachLocManager();
                } else {

                }
                return;
            }
        }
    }

    @Override
    public void requestFinished(JSONObject response, String url) {
        try {
            if (url.equals(app.GET_local_venues)) {

                System.out.println(response.toString());
                int size = response.getJSONArray("Nearby").length();
                for(int i = 0; i < size; i++) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    double lat = response.getJSONArray("Nearby").getJSONObject(i).getDouble("latitude");
                    double lon= response.getJSONArray("Nearby").getJSONObject(i).getDouble("longitude");
                    LatLng pos = new LatLng(lat, lon);
                    String place_name = response.getJSONArray("Nearby").getJSONObject(i).getString("venue_name");
                    markerOptions.position(pos);
                    markerOptions.title(place_name);
                    map.addMarker(markerOptions);


                }
                map.moveCamera(CameraUpdateFactory.newLatLng(current_pos));
                map.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
    }

    @Override
    public void requestFailed(int statusCode) {

    }
}
