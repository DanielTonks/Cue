package uk.co.cue.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.User;
import uk.co.cue.app.util.VolleyRequestFactory;

/** Class that sets up a new machine in a venue
 *
 */

public class SetupTagActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest{

    private String venue_id = "1"; //dummy value, will need to be changed
    private VolleyRequestFactory volleyRequest;
    private CueApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_tag);
        setTitle("Setup new table");

        app = (CueApp) getApplication();

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

        EditText pub = (EditText) findViewById(R.id.edit_pub_name);
        final String pub_name = pub.getText().toString();

        EditText price = (EditText) findViewById(R.id.value);
        final String price_per_game = price.getText().toString();

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("venue_id", venue_id); //dummy value; will need to be changed later
        parameters.put("category", spinner_value);
        parameters.put("base_price", "0.50");

        volleyRequest.doRequest(app.POST_add_machine, parameters, Request.Method.POST);
    }

    public void generateLink(int machineID, int venueID) {
        String machineIDString = String.valueOf(machineID);
        String venueIDString = String.valueOf(venueID);
        String url_with_params = "https://idk-cue.club/queue/add?machine_id="+machineIDString+"&venue_id="+venueIDString;

        Task<ShortDynamicLink> task = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(url_with_params))//implement
                .setDynamicLinkDomain("cjzd4.app.goo.gl")  //implement
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                    .setMinimumVersion(1)
                    .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> t) {
                        if(t.isSuccessful()) {
                            Uri shortLink = t.getResult().getShortLink();

                            Toast.makeText(getApplicationContext(), "Link generated!",
                                    Toast.LENGTH_LONG).show();

                            String link = shortLink.toString();

                            Intent NFCIntent = new Intent(getApplicationContext(), WriteTagActivity.class);
                            NFCIntent.putExtra("url", link);
                            startActivity(NFCIntent);

                        } else {
                            System.out.println(t.getException().getMessage());
                        }
                    }
                });


    }

    @Override
    public void requestFinished(JSONObject response, String url) {
        try {
            if (url.equals(app.POST_add_machine)) {

                System.out.println(response.toString());
                int machineID = response.getJSONArray("Machine").getJSONObject(0).getInt("machine_id");
                int venueID = response.getJSONArray("Machine").getJSONObject(0).getInt("venue_id");
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
}
