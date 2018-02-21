package uk.co.cue.app.activity.loginFlow;

/*
 * Class that handles logging into a previously created account.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.activity.MainActivity;
import uk.co.cue.app.util.CueApp;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;

    private LinearLayout fields;
    private RelativeLayout pending;

    private CueApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.app = (CueApp) getApplication();
        setTitle("Login");

        this.usernameField = findViewById(R.id.edit_username);
        this.passwordField = findViewById(R.id.edit_password);

        this.fields = findViewById(R.id.loginFields);
        this.pending = findViewById(R.id.login_progress);

        findViewById(R.id.btn_login_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    /**
     * Using the username and password the user has entered, try to log in.
     */
    private void attemptLogin() {

        fields.setVisibility(View.GONE);
        pending.setVisibility(View.VISIBLE);

        final String username = usernameField.getText().toString();
        final String password = passwordField.getText().toString();

        //Make a network request to log in.

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, "https://idk-cue.club/user/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.equals("OK")) {
                                app.setLoggedInUserId(10); // dummy value for now

                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i); // user logged in.
                                finish(); // prevent the user from returning here
                            }
                        } catch (Exception err) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.statusCode == 401) {
                            pending.setVisibility(View.GONE);
                            fields.setVisibility(View.VISIBLE);
                            passwordField.setText("");

                            Toast.makeText(getApplicationContext(), "Incorrect login details", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("device_id", app.getFirebaseToken());

                return params;
            }
        };
        requestQueue.add(postRequest);


    }
}
