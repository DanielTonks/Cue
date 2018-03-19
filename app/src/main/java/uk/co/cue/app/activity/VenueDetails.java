package uk.co.cue.app.activity;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.objects.Machine;
import uk.co.cue.app.objects.Venue;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;

public class VenueDetails extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest{

    protected GeoDataClient mGeoDataClient;
    private String googleToken;
    private VolleyRequestFactory volleyRequest;
    private CueApp app;
    private GoogleMap map;
    private Venue venue;
    private LinearLayout main_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);


        app = (CueApp) getApplication();
        this.volleyRequest = new VolleyRequestFactory(this, getApplicationContext());

        Intent i = getIntent();
        venue = (Venue) i.getExtras().getParcelable("venue");
        googleToken = venue.getGoogleToken();

        final TextView address = findViewById(R.id.address);
        final TextView rating = findViewById(R.id.rating);
        final TextView phone = findViewById(R.id.phone);

        SupportMapFragment mapFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.individualMapView));
        mapFrag.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    map = googleMap;
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    LatLng loc = new LatLng(venue.getLatitude(), venue.getLongitude());
                                    markerOptions.position(loc);
                                    markerOptions.title(venue.getVenue_name());
                                    Marker marker = map.addMarker(markerOptions);


                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(loc)
                                            .zoom(16)
                                            .bearing(0)
                                            .tilt(0).build();

                                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }
                            });


        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone.getText().toString()));
                startActivity(intent);
            }
        });

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("venue_id", String.valueOf(venue.getVenue_id()));
        volleyRequest.doRequest(app.GET_venue_queues, parameters, Request.Method.GET);


        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        mGeoDataClient.getPlaceById(googleToken).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place myPlace = places.get(0);
                    Log.i("VenueDetails", "Place found: " + myPlace.getName());
                    setTitle(myPlace.getName());
                    address.setText(myPlace.getAddress());
                    rating.setText(String.valueOf(myPlace.getRating()) + "/5");

                    String phoneNum = myPlace.getPhoneNumber().toString();
                    phoneNum = phoneNum.replace("+44 ", "0");
                    phone.setText(phoneNum);
                    places.release();
                } else {
                    System.out.println(task.getException().getMessage());
                    Log.e("VenueDetails", "Place not found.");
                }
            }
        });

    }

    @Override
    public void requestFinished(JSONObject response, String url) {

        main_layout = findViewById(R.id.cue_features_layout);
        try {
            System.out.println("Response for machines: "+response);
            JSONArray array = response.getJSONArray("Queues");
            for(int i =0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String num_people = obj.getString("queue_size");
                String category = obj.getString("category");
                String num_machines = obj.getString("num_machines");
                //String avg = obj.getString("avg");
                String price = obj.getString("base_price");

                View row = LayoutInflater.from(this).inflate(R.layout.queue_details, main_layout, false);
                ImageView img = row.findViewById(R.id.image);
                TextView type = row.findViewById(R.id.queue_type);
                TextView queue_size = row.findViewById(R.id.queue_size);
                TextView num_m = row.findViewById(R.id.queue_num_machines);
                TextView pr = row.findViewById(R.id.queue_base_price);

                switch(category) {
                    case "Pool":
                        img.setImageResource(R.mipmap.billiard_cue);
                        break;
                    case "Snooker":
                        img.setImageResource(R.mipmap.billiard_cue);
                        break;
                    case "Fruit Machine":
                        img.setImageResource(R.mipmap.slot_machine);
                        break;
                    case "Foosball":
                        img.setImageResource(R.mipmap.football);
                        break;
                    case "Arcade":
                        img.setImageResource(R.mipmap.arcade);
                        break;
                    case "Other":
                        img.setImageResource(R.mipmap.other);
                }

                type.setText("Queue type: "+ category);
                queue_size.setText("Queue size: "+ num_people);
                num_m.setText("Number of machines: "+ num_machines);
                pr.setText("Price: "+ price);

                main_layout.addView(row);
            }

        } catch(JSONException e) {
            System.out.println("problem with JSON: "+e);
        }

    }

    @Override
    public void requestFailed(int statusCode) {
        System.out.println("Error getting venue machines: "+ statusCode);
    }
}
