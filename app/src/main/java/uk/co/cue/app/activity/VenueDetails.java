package uk.co.cue.app.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import uk.co.cue.app.R;
import uk.co.cue.app.objects.Venue;

public class VenueDetails extends AppCompatActivity {

    protected GeoDataClient mGeoDataClient;
    private String googleToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);

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
}
