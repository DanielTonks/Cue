package uk.co.cue.app.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.objects.Venue;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;

public class VenueDetails extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest{

    protected GeoDataClient mGeoDataClient;
    private String googleToken;
    private VolleyRequestFactory volleyRequest;
    private CueApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);

        app = (CueApp) getApplication();
        this.volleyRequest = new VolleyRequestFactory(this, getApplicationContext());

        Intent i = getIntent();
        Venue venue = (Venue) i.getExtras().getParcelable("venue");
        googleToken = venue.getGoogleToken();

        final TextView address = findViewById(R.id.address);
        final TextView rating = findViewById(R.id.rating);
        final TextView phone = findViewById(R.id.phone);

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
        volleyRequest.doRequest(app.GET_venue_machines, parameters, Request.Method.GET);


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
        try {
            //System.out.println("Response for machines: "+response);
            JSONArray array = response.getJSONArray("Machines");
            TextView pool_num = findViewById(R.id.pool_num);
            TextView snooker_num = findViewById(R.id.snooker_num);
            TextView foosball_num = findViewById(R.id.foosball_num);
            TextView arcade_num = findViewById(R.id.arcade_num);
            int pool =0;
            int snooker=0;
            int foosball=0;
            int arcade=0;
            int fruitMachine = 0;
            int shuffleBoard = 0;

            if(array.length()== 0) {
                TextView cue = findViewById(R.id.cue_features);
                cue.setText("No Cue features available!");
            } else {
                for(int i=0; i<array.length(); i++) {
                    String category = array.getJSONObject(i).getString("category");
                    switch(category) {
                        case "Pool":
                            pool++;
                            break;
                        case "Snooker":
                            snooker++;
                            break;
                        case "Foosball":
                            foosball++;
                            break;
                        case "Arcade":
                            arcade++;
                            break;
                        case "Fruity Machine":
                            fruitMachine++;
                            break;
                        case "Shuffleboard":
                            shuffleBoard++;
                            break;
                    }
                }


                pool_num.setText("Pool tables: "+ pool);
                snooker_num.setText("Snooker tables: "+ snooker);
                foosball_num.setText("Foosball tables: "+ foosball);
                arcade_num.setText("Arcade machines: "+arcade);
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
