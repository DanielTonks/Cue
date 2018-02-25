package uk.co.cue.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.cue.app.R;
import uk.co.cue.app.util.CueApp;


public class MainActivity extends AppCompatActivity {

    private RelativeLayout standard;
    private RelativeLayout inQueue;

    private TextView queuePos;
    private TextView queueDescription;

    private CueApp app;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View topLevelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CueApp app = (CueApp) getApplication();
        super.onCreate(savedInstanceState);

        if (!app.isUserLoggedIn()) {
            //Intent i = new Intent(getApplicationContext(), LoginChooserActivity.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // Do not add this activity to the backstack, otherwise the user can go back from Login to logged in state.
            //startActivity(i);
        } else {
            setUp();
        }
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
        this.inQueue = findViewById(R.id.inQueue);
        this.queuePos = findViewById(R.id.queuePosition);

        this.queueDescription = findViewById(R.id.queueDescription);

        this.topLevelView = findViewById(R.id.main_layout);

        Intent i = getIntent();
        boolean confirmed = i.getBooleanExtra("confirmed", false);
        System.out.println(confirmed);
        if (confirmed) {
            updateUI(i);
        }


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        //TextView username = navigationView.findViewById(R.id.drawer_username);
        TextView username = navigationView.getHeaderView(0).findViewById(R.id.drawer_username);
        username.setText(app.getUsername());

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        menuItem.setChecked(false);

                        System.out.println(menuItem.getTitle());

                        switch (menuItem.getTitle().toString()) {
                            case "Help":
                                showHelpFragment(new HelpFragment());
                                break;

                            case "Home":
                                showHomeFragment(new HomeFragment());
                                break;
                        }


                        return true;
                    }
                });

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
        standard.setVisibility(View.GONE);
        inQueue.setVisibility(View.VISIBLE);

        String pub = data.getStringExtra("pubID");
        String name = data.getStringExtra("userName");
        queueDescription.setText(name + "; you're now in the queue at " + pub + ", at position");
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





}
