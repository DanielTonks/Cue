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
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.activity.MainActivity;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;

public class LoginActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest {

    private EditText usernameField;
    private EditText passwordField;

    private LinearLayout fields;
    private RelativeLayout pending;

    private VolleyRequestFactory vrf;


    private CueApp app;
    private String username;

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

        this.vrf = new VolleyRequestFactory(this, getApplicationContext());

    }

    /**
     * Using the username and password the user has entered, try to log in.
     */
    private void attemptLogin() {
        this.username = usernameField.getText().toString();
        final String password = passwordField.getText().toString();

        if (password.trim().equals("") || username.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Incomplete fields", Toast.LENGTH_SHORT).show();
            return;
        }

        app.closeKeyboard(getCurrentFocus());

        fields.setVisibility(View.GONE);
        pending.setVisibility(View.VISIBLE);

        //Make a network request to log in.
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        params.put("device_id", app.getFirebaseToken());

        vrf.doRequest(app.POST_login, params, Request.Method.POST);


    }

    @Override
    public void requestFinished(String response, String url) {
        try {
            if (response.equals("OK")) {
                app.setLoggedInUser(10, username); // dummy value for now
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i); // user logged in.
                finish(); // prevent the user from returning here
            }
        } catch (Exception err) {
        }

    }

    @Override
    public void requestFailed(VolleyError error) {
        if (error.networkResponse.statusCode == 401) {
            pending.setVisibility(View.GONE);
            fields.setVisibility(View.VISIBLE);
            passwordField.setText("");

            Toast.makeText(getApplicationContext(), "Incorrect login details", Toast.LENGTH_SHORT).show();
        }
    }
}
