package uk.co.cue.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.activity.loginFlow.LoginChooserActivity;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;


public class MainActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest {

    private RelativeLayout standard;
    private RelativeLayout inQueue;

    private TextView queuePos;
    private TextView queueDescription;

    private CueApp app;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View topLevelView;

    private VolleyRequestFactory vrf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CueApp app = (CueApp) getApplication();
        super.onCreate(savedInstanceState);

        if (app.getUser().getSession() != null) {
            setUp();
        }
        this.vrf = new VolleyRequestFactory(this, getApplicationContext());
    }

    private void setUp() {
        setContentView(R.layout.activity_main);
        this.app = (CueApp) getApplication();

        showHomeFragment(new HomeFragment());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

//        Button btn = findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), ReserveTableActivity.class);
//                // i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                startActivity(i);
//                //Intent i = new Intent(getApplicationContext(), NFCDetectedActivity.class);
//                //startActivityForResult(i, 0);
//            }
//        });

        this.standard = findViewById(R.id.standard);
//        this.inQueue = findViewById(R.id.inQueue);
//        this.queuePos = findViewById(R.id.queuePosition);
//
//        this.queueDescription = findViewById(R.id.queueDescription);

        this.topLevelView = findViewById(R.id.main_layout);

        Intent i = getIntent();
        boolean confirmed = i.getBooleanExtra("confirmed", false);
        System.out.println(confirmed);
        if (confirmed) {
            updateUI(i);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Menu m = navigationView.getMenu();

        //TextView username = navigationView.findViewById(R.id.drawer_username);
        TextView username = navigationView.getHeaderView(0).findViewById(R.id.drawer_username);
        username.setText(app.getUser().getUsername());

        boolean visible = app.getUser().isBusiness();
        m.findItem(R.id.local_venues).setVisible(!visible);
        m.findItem(R.id.favourite_venues).setVisible(!visible);
        m.findItem(R.id.past_games).setVisible(!visible);
        m.findItem(R.id.add_machine).setVisible(visible);
        m.findItem(R.id.edit_machine).setVisible(visible);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        menuItem.setChecked(false);

                        switch (menuItem.getTitle().toString()) {
                            case "Help":
                                showHelpFragment(new HelpFragment());
                                break;

                            case "Home":
                                showHomeFragment(new HomeFragment());
                                break;

                            case "Add new machine":
                                Intent i = new Intent(MainActivity.this, SetupTagActivity.class);
                                startActivity(i);
                                break;

                            case "Local Venues":
                                Intent intent = new Intent(MainActivity.this, LocalVenuesActivity.class);
                                startActivity(intent);
                                break;

                            case "Log out":
                                logout();
                                break;
                        }


                        return true;
                    }
                });

    }

    private void logout() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", String.valueOf(app.getUser().getUserid()));
        params.put("session_cookie", app.getUser().getSession());
        vrf.doRequest(app.POST_logout, params, Request.Method.POST);
    }

    private void showHelpFragment(HelpFragment f) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, f);
        fragmentTransaction.commit();
    }

    private void showHomeFragment(HomeFragment f) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, f);
        fragmentTransaction.commit();
    }

    private void updateUI(Intent data) {
        showHomeFragment(new HomeFragment());
//        standard.setVisibility(View.GONE);
//        inQueue.setVisibility(View.VISIBLE);

        String pub = data.getStringExtra("pubID");
        String name = data.getStringExtra("userName");
        System.out.println(name + "; you're now in the queue at " + pub + ", at position");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void requestFinished(JSONObject response, String url) {
        if (url.equals(app.POST_logout)) {
            logoutOfApp();
        }
    }

    @Override
    public void requestFailed(int statusCode) {

        System.out.println(statusCode);
        logoutOfApp();
    }

    public void logoutOfApp() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("logged_in", false);
        editor.putInt("user_id", -1);
        editor.putString("username", null);
        editor.putString("session_cookie", null);
        editor.putBoolean("isBusiness", false);
        editor.apply();


        Intent i = new Intent(getApplicationContext(), LoginChooserActivity.class);
        startActivity(i);
        finish();
    }
}
