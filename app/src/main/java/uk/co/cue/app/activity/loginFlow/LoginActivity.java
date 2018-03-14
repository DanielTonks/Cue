package uk.co.cue.app.activity.loginFlow;

/*
 * Class that handles logging into a previously created account.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.User;
import uk.co.cue.app.util.VolleyRequestFactory;

public class LoginActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest {

    private EditText usernameField;
    private EditText passwordField;

    private LinearLayout fields;
    private RelativeLayout pending;

    private VolleyRequestFactory vrf;

    private CueApp app;
    private String username;
    private int userID;

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
        params.put("device_id", app.getUser().getFirebaseToken());

        vrf.doRequest(app.POST_login, params, Request.Method.POST);
    }

    @Override
    public void requestFinished(JSONObject response, String url) {
        try {
            if (url.equals(app.POST_login)) {
                int userID = response.getJSONArray("User").getJSONObject(0).getInt("user_id");
                String session = response.getJSONArray("User").getJSONObject(0).getString("session_cookie");

                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", String.valueOf(userID));
                JSONArray arrayOfVenues = response.getJSONArray("Admin");
                boolean isBusiness = false;
                if (arrayOfVenues.length() != 0) {
                    isBusiness = true;
                }

                User usr = new User(userID, username, session, isBusiness, null);
                app.setUser(usr);

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }

    }

    @Override
    public void requestFailed(int statusCode) {
        if (statusCode == 401) {
            pending.setVisibility(View.GONE);
            fields.setVisibility(View.VISIBLE);
            passwordField.setText("");
            Toast.makeText(getApplicationContext(), "Incorrect login details", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot connect to the server", Toast.LENGTH_SHORT).show();
            pending.setVisibility(View.GONE);
            fields.setVisibility(View.VISIBLE);
        }
    }
}
