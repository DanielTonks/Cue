package uk.co.cue.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.cue.app.R;
import uk.co.cue.app.util.CueApp;


public class MainActivity extends AppCompatActivity {

    private RelativeLayout standard;
    private RelativeLayout inQueue;

    private TextView queuePos;
    private TextView queueDescription;


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

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ReserveTableActivity.class);
                // i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                //Intent i = new Intent(getApplicationContext(), NFCDetectedActivity.class);
                //startActivityForResult(i, 0);
            }
        });

        this.standard = findViewById(R.id.standard);
        this.inQueue = findViewById(R.id.inQueue);
        this.queuePos = findViewById(R.id.queuePosition);

        this.queueDescription = findViewById(R.id.queueDescription);

        ImageView help = findViewById(R.id.helpImage);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            }
        });


        Intent i = getIntent();
        boolean confirmed = i.getBooleanExtra("confirmed", false);
        System.out.println(confirmed);
        if (confirmed) {
            updateUI(i);
        }

    }

    private void updateUI(Intent data) {
        standard.setVisibility(View.GONE);
        inQueue.setVisibility(View.VISIBLE);

        String pub = data.getStringExtra("pubID");
        String name = data.getStringExtra("userName");


        queueDescription.setText(name + "; you're now in the queue at " + pub + ", at position");
    }





}
