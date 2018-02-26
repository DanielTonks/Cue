package uk.co.cue.app.activity.loginFlow;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uk.co.cue.app.R;
import uk.co.cue.app.activity.MainActivity;
import uk.co.cue.app.util.CueApp;

/**
 * This is the first activity in the app.
 */
public class LoginChooserActivity extends AppCompatActivity {

    private final static int LOGIN = 1;
    private final static int REGISTER = 2;

    private SharedPreferences sharedPref;
    private CueApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (CueApp) getApplication();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = null;
        final boolean loggedIn = sharedPref.getBoolean("logged_in", false);

        if (loggedIn) { //If the user is logged in then skip this and go to the main page
            username = sharedPref.getString("username", null);
            System.out.println(username + " just logged in");
            app.setLoggedInUser(0, username);
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // don't remember that we visited this activity first
            startActivity(i);
        }

        //Otherwise we show the login chooser, so the user can login or register.
        setContentView(R.layout.activity_login_chooser);

        findViewById(R.id.btn_login_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, LOGIN);
                //finish();
            }
        });

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REGISTER);
                //finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN) {
            if (resultCode == RESULT_OK) {
                System.out.println("login");
                app.setLoggedInUser(0, data.getStringExtra("username"));
            }
        } else if (requestCode == REGISTER) {
            if (resultCode == RESULT_OK) {
                System.out.println("register");
                app.setLoggedInUser(0, data.getStringExtra("username"));
            }
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("logged_in", true);
        editor.putString("username", data.getStringExtra("username"));


        // Commit the edits!
        editor.apply();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish(); // make sure you cannot return to this activity

    }
}
