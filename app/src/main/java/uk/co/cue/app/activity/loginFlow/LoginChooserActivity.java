package uk.co.cue.app.activity.loginFlow;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

import uk.co.cue.app.R;
import uk.co.cue.app.activity.MainActivity;
import uk.co.cue.app.objects.Venue;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.User;

/**
 * This is the first activity in the app.
 */
public class LoginChooserActivity extends AppCompatActivity {

    private final static int LOGIN = 1;
    private final static int REGISTER = 2;

    private SharedPreferences sharedPref;
    private CueApp app;

    //BUG: Sometimes the user can click 'Up' and it will return to this activity. For now this can be caught here
    @Override
    protected void onResume() {
        checkForLogin();
        super.onResume();
    }

    /**
     * Method that checks if the user is logged in, and if so skips this UI.
     */
    private void checkForLogin() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean loggedIn = sharedPref.getBoolean("logged_in", false);

        if (loggedIn) { //If the user is logged in then skip this and go to the main page
            int userId = sharedPref.getInt("user_id", 0);
            String username = sharedPref.getString("username", null);
            boolean isBusiness = sharedPref.getBoolean("isBusiness", false);
            String session = sharedPref.getString("session_cookie", null);
            Gson gson = new Gson();
            String json = sharedPref.getString("venues", "");
            Type type = new TypeToken<ArrayList<Venue>>(){}.getType();
            ArrayList<Venue> array = gson.fromJson(json, type);
            User newUsr = new User(userId, username, session, isBusiness, array);
            app.setUser(newUsr);
            Intent i = new Intent(LoginChooserActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (CueApp) getApplication();
        this.setTheme(R.style.AppTheme);

        System.out.println("and we're back here again");

        checkForLogin();

        //Otherwise we show the login chooser, so the user can login or register.
        setContentView(R.layout.activity_login_chooser);

        findViewById(R.id.btn_login_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, LOGIN);
            }
        });

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REGISTER);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        User u = app.getUser();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("logged_in", true);
        editor.putInt("user_id", u.getUserid());
        editor.putString("username", u.getUsername());
        editor.putString("session_cookie", u.getSession());
        editor.putBoolean("isBusiness", u.isBusiness());
        Gson gson = new Gson();
        String json = gson.toJson(u.getVenues());
        editor.putString("venues", json);

        // Commit the edits!
        editor.apply();
        Intent i = new Intent(LoginChooserActivity.this, MainActivity.class);
        startActivity(i);

        finish(); // make sure you cannot return to this activity

    }
}
