package uk.co.cue.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.objects.Venue;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;

/** Class that sets up a new machine in a venue
 *
 */

public class SetupTagActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest{

    private String venue_id = "1"; //dummy value, will need to be changed
    private VolleyRequestFactory volleyRequest;
    private CueApp app;
    private Spinner pub;
    private EditText price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_tag);
        setTitle("Setup new table");


        app = (CueApp) getApplication();
        System.out.println("printing: "+app.getUser().getVenues().get(0).getVenue_name());
        pub = (Spinner) findViewById(R.id.edit_pub_name);
        ArrayAdapter<Venue> venueAdapter = new ArrayAdapter<Venue>(this,
                android.R.layout.simple_spinner_dropdown_item,
                app.getUser().getVenues());
        pub.setAdapter(venueAdapter);


        this.volleyRequest = new VolleyRequestFactory(this, getApplicationContext());
    }


    public void onClickSubmitSetup(View view) {
        sendPostRequest();
    }

    public Activity getActivity() {
        return this;
    }

    public void sendPostRequest() {
        Spinner machine_name = (Spinner) findViewById(R.id.machine_type_spinner);
        final String spinner_value = machine_name.getSelectedItem().toString();

;
        //sort stuff out here as well

        price = (EditText) findViewById(R.id.value);
        final String price_per_game = price.getText().toString();

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("venue_id", venue_id);
        parameters.put("category", spinner_value);
        parameters.put("base_price", price_per_game);
        parameters.put("user_id", String.valueOf(app.getUser().getUserid()));
        parameters.put("session_cookie", app.getUser().getSession());

        volleyRequest.doRequest(app.POST_add_machine, parameters, Request.Method.POST);
    }

    public void generateLink(int machineID, int venueID) {
        String machineIDString = String.valueOf(machineID);
        String venueIDString = String.valueOf(venueID);
        String url_with_params = "https://idk-cue.club/queue/add?machine_id="+machineIDString+"&venue_id="+venueIDString;
        String firebase_link = "https://cjzd4.app.goo.gl/?link=" + url_with_params + "&apn=uk.co.cue.app";


        Intent NFCIntent = new Intent(getApplicationContext(), WriteTagActivity.class);
        NFCIntent.putExtra("url", firebase_link);
        startActivityForResult(NFCIntent, 0);

    }

    @Override
    public void requestFinished(JSONObject response, String url) {
        try {
            if (url.equals(app.POST_add_machine)) {

                System.out.println(response.toString());
                int machineID = response.getJSONArray("Machine").getJSONObject(0).getInt("machine_id");
                Venue venue = (Venue) pub.getSelectedItem();
                int venueID = venue.getVenue_id();
                generateLink(machineID, venueID);
            }
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
    }

    @Override
    public void requestFailed(int statusCode) {
        if (statusCode == 401) {
            //IMPLEMENT
        } else {
            //IMPLEMENT
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Tag written. You can now place this near to the hub box.", Toast.LENGTH_LONG).show();
            price.setText("");

        }


    }
}
