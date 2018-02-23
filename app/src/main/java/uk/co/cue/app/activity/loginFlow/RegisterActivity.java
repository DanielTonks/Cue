package uk.co.cue.app.activity.loginFlow;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private EditText passwordConfirmationField;

    private EditText firstName;
    private EditText surname;


    private LinearLayout fields;
    private RelativeLayout pending;

    private CueApp app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");

        this.app = (CueApp) getApplication();

        this.usernameField = findViewById(R.id.edit_username);
        this.passwordField = findViewById(R.id.edit_password);
        this.passwordConfirmationField = findViewById(R.id.edit_password_confirm);

        this.firstName = findViewById(R.id.edit_firstname);
        this.surname = findViewById(R.id.edit_surname);

        this.fields = findViewById(R.id.loginFields);
        this.pending = findViewById(R.id.login_progress);

        findViewById(R.id.btn_register_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        app.closeKeyboard(getCurrentFocus());

        final String username = usernameField.getText().toString().trim();
        final String p1 = passwordField.getText().toString().trim();
        final String p2 = passwordConfirmationField.getText().toString().trim();
        final String fName = firstName.getText().toString().trim();
        final String sName = surname.getText().toString().trim();

        if (username.equals("") || p1.equals("") || p2.equals("") || fName.equals("") || sName.equals("")) {
            Toast.makeText(getApplicationContext(), "No fields should be left blank", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!p1.equals(p2)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        fields.setVisibility(View.GONE);
        pending.setVisibility(View.VISIBLE);

        // Create a request to sign up the user
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, "https://idk-cue.club/user/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            app.setLoggedInUser(10, username); // dummy value for now
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i); // user logged in.
                            finish(); // prevent the user from returning here
                        } catch (Exception err) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.statusCode == 500) {
                            pending.setVisibility(View.GONE);
                            fields.setVisibility(View.VISIBLE);
                            usernameField.setText("");
                            passwordConfirmationField.setText("");
                            passwordField.setText("");

                            Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("username", username);
                params.put("password", p1);
                params.put("name", fName + " " + sName);
                params.put("device_id", app.getFirebaseToken());

                return params;
            }
        };
        requestQueue.add(postRequest);

    }
}
