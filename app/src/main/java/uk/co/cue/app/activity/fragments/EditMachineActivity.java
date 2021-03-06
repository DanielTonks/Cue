package uk.co.cue.app.activity.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.objects.Venue;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;

public class EditMachineActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest{

    private Spinner venue, category;
    private EditText price;
    private Button submit;
    private CueApp app;
    private VolleyRequestFactory vrf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_tag);
        setTitle("Edit table");

        vrf = new VolleyRequestFactory(this, getApplicationContext());

        app = (CueApp) getApplication();
        venue = findViewById(R.id.edit_pub_name);
        category = findViewById(R.id.machine_type_spinner);
        price = findViewById(R.id.value);
        submit = findViewById(R.id.submit);

        TextView instruction = findViewById(R.id.instruction);
        instruction.setText(R.string.edit_instruction);

        ArrayAdapter<Venue> venueAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                app.getUser().getVenues());
        venue.setAdapter(venueAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Venue ven = (Venue) venue.getSelectedItem();
                int venueID = ven.getVenue_id();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", String.valueOf(app.getUser().getUserid()));
                    params.put("session_cookie", app.getUser().getSession());
                    params.put("venue_id", String.valueOf(venueID));
                    params.put("category", category.getSelectedItem().toString());
                    params.put("new_price", price.getText().toString());
                    vrf.doRequest(app.PUT_edit_machine, params, Request.Method.PUT);
            }
        });
    }

    @Override
    public void requestFinished(JSONObject response, String url) {
        Toast.makeText(this, "Machines updated", Toast.LENGTH_LONG).show();
        price.setText("");
    }

    @Override
    public void requestFailed(int statusCode) {
        System.out.println("Something went wrong");
    }
}
