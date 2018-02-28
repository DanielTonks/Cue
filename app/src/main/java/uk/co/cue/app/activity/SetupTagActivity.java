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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;

public class SetupTagActivity extends AppCompatActivity {

    private String venue_id = "1"; //dummy value, will need to be changed
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_tag);
        setTitle("Setup new table");
    }


    public void onClickSubmitSetup(View view) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating your link...");
        dialog.setTitle("Machine creation");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        dialog.setCancelable(false);

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

        EditText num = (EditText) findViewById(R.id.edit_machine_num);
        final String num_machines = num.getText().toString();

        EditText price = (EditText) findViewById(R.id.value);
        final String price_per_game = price.getText().toString();


        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest post = new StringRequest(
                Request.Method.POST,
                "https://idk-cue.club/machine/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        generateLink(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode == 500) {
                            //handle errors
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("venue_id", venue_id); //dummy value; will need to be changed later
                parameters.put("category", spinner_value);
                parameters.put("base_price", "0.50");


                return parameters;
            }
        };
        queue.add(post);
    }

    public void generateLink(String server_response) {

        Task<ShortDynamicLink> task = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://idk-cue.club/"))//implement
                .setDynamicLinkDomain("cjzd4.app.goo.gl")  //implement
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                    .setMinimumVersion(21)
                    .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> t) {
                        if(t.isSuccessful()) {
                            Uri shortLink = t.getResult().getShortLink();

                            Toast.makeText(getActivity(), "Link generated!",
                                    Toast.LENGTH_LONG).show();

                            String link = t.toString();

                            Intent NFCIntent = new Intent(getApplicationContext(), WriteTagActivity.class);
                            NFCIntent.putExtra("url", link);
                            startActivity(NFCIntent);

                        } else {
                            System.out.println(t.getException().getMessage());
                        }
                    }
                });


    }
}
