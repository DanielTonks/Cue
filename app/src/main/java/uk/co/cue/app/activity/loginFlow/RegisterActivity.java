package uk.co.cue.app.activity.loginFlow;

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

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;

public class RegisterActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest {

    private EditText usernameField;
    private EditText passwordField;
    private EditText passwordConfirmationField;

    private EditText firstName;
    private EditText surname;


    private LinearLayout fields;
    private RelativeLayout pending;

    private VolleyRequestFactory vrf;

    private CueApp app;
    private String username;


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

        vrf = new VolleyRequestFactory(this, getApplicationContext());
    }

    private void attemptRegister() {
        app.closeKeyboard(getCurrentFocus());

        username = usernameField.getText().toString().trim();
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
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", p1);
        params.put("name", fName + " " + sName);
        params.put("device_id", app.getFirebaseToken());

        vrf.doRequest(app.POST_register, params, Request.Method.POST);
    }

    @Override
    public void requestFinished(String response, String url) {
        try {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("username", username);
            returnIntent.putExtra("id", 0);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } catch (Exception err) {
        }
    }

    @Override
    public void requestFailed(int statusCode) {
        if (statusCode == 401) {
            pending.setVisibility(View.GONE);
            fields.setVisibility(View.VISIBLE);
            usernameField.setText("");
            passwordConfirmationField.setText("");
            passwordField.setText("");

            Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot connect to the server", Toast.LENGTH_SHORT).show();
            pending.setVisibility(View.GONE);
            fields.setVisibility(View.VISIBLE);
        }
    }
}
