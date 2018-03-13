package uk.co.cue.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import uk.co.cue.app.R;
import uk.co.cue.app.util.CueApp;

public class EditMachineActivity extends AppCompatActivity {

    private Spinner venue, machine;
    private Button submit;
    private CueApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_machine);

        app = (CueApp) getApplicationContext();
        venue = findViewById(R.id.pub_name_spinner);
        machine = findViewById(R.id.chooseMachine);
        submit = findViewById(R.id.submitEdit);

        venue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Get the machines of the venue
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Submit it all to the serverrrrr
            }
        });



    }
}
