package uk.co.cue.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private RelativeLayout standard;
    private RelativeLayout inQueue;

    private TextView queuePos;
    private TextView queueDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NFCDetectedActivity.class);
                startActivityForResult(i, 0);
            }
        });

        this.standard = findViewById(R.id.standard);
        this.inQueue = findViewById(R.id.inQueue);
        this.queuePos = findViewById(R.id.queuePosition);

        this.queueDescription = findViewById(R.id.queueDescription);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                standard.setVisibility(View.GONE);
                inQueue.setVisibility(View.VISIBLE);

                String pub = data.getStringExtra("pubID");

                queueDescription.setText("You're now in the queue at " + pub + ", at position");
            }
        }
    }


}
