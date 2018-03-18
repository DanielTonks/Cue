package uk.co.cue.app.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        LinearLayout layout = new LinearLayout(this);
        ImageView image = new ImageView(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        try {
            System.out.println("Response for machines: "+response);
            JSONArray array = response.getJSONArray("Queues");
            for(int i =0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String num_people = obj.getString("people");
                String category = obj.getString("category");
                String num_machines = obj.getString("machines");
                String avg = obj.getString("avg");
                String price = obj.getString("price");

                switch(category) {
                    case "Pool":
                        image.setImageResource(R.mipmap.billiard_cue);
                        break;
                    case "Snooker":
                        image.setImageResource(R.mipmap.billiard_cue);
                        break;
                    case "Fruity Machine":
                        image.setImageResource(R.mipmap.slot_machine);
                        break;
                    case "Foosball":
                        image.setImageResource(R.mipmap.football);
                        break;
                    case "Arcade":
                        image.setImageResource(R.mipmap.arcade);
                        break;
                }

                layout.addView(image);

                LinearLayout inner_layout = new LinearLayout(this);
                inner_layout.setOrientation(LinearLayout.VERTICAL);
                TextView queueType = new TextView(this);
                queueType.setText(category);
                inner_layout.addView(queueType);

                TextView num_peeps = new TextView(this);
                num_peeps.setText(num_people);
                inner_layout.addView(num_peeps);

                TextView num_m = new TextView(this);
                num_m.setText(num_machines);
                inner_layout.addView(num_m);

                TextView average = new TextView(this);
                average.setText(avg);
                inner_layout.addView(average);

                TextView price_view = new TextView(this);
                price_view.setText(price);
                inner_layout.addView(price_view);

                layout.addView(inner_layout);
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
