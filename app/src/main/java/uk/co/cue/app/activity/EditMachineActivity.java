package uk.co.cue.app.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import uk.co.cue.app.R;
import uk.co.cue.app.objects.Venue;
import uk.co.cue.app.util.CueApp;

public class EditMachineActivity extends AppCompatActivity {

    private Spinner venue, category;
    private EditText price;
    private Button submit;
    private CueApp app;
    private CheckBox toDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_machine);
        setTitle("Edit table");

        app = (CueApp) getApplication();
        venue = findViewById(R.id.p_name);
        category = findViewById(R.id.machine_type_spinner2);
        price = findViewById(R.id.newValue);
        submit = findViewById(R.id.submitEdit);
        toDelete = findViewById(R.id.toDelete);

        ArrayAdapter<Venue> venueAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                app.getUser().getVenues());
        venue.setAdapter(venueAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NFCDetectedActivity.class);
                i.putExtra("type", "Edit");

                Venue ven = (Venue) venue.getSelectedItem();
                int venueID = ven.getVenue_id();
                i.putExtra("venue", venueID);
                i.putExtra("price", price.getText());
                i.putExtra("toDelete", toDelete.isChecked());
                i.putExtra("category", category.getSelectedItem().toString());
                startActivityForResult(i, 0);

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Tag edited!", Toast.LENGTH_LONG).show();
            price.setText("");

        }


    }
}
