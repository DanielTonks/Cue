package uk.co.cue.app.activity.loginFlow;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uk.co.cue.app.R;
import uk.co.cue.app.activity.MainActivity;
import uk.co.cue.app.util.CueApp;

public class LoginChooserActivity extends AppCompatActivity {

    private final static int LOGIN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CueApp app = (CueApp) getApplication();


        if (app.isUserLoggedIn()) { //If the user is logged in then skip this and go to the main page
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // don't remember that we visited this activity first
            startActivity(i);
        }
        setContentView(R.layout.activity_login_chooser);


        findViewById(R.id.btn_login_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                //finish();
            }
        });
    }


}
