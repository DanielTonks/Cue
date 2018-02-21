package uk.co.cue.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import uk.co.cue.app.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setTitle("Help");
    }
}
