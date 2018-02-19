package uk.co.cue.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class ReserveTableActivity extends AppCompatActivity {

    private RelativeLayout nameLayout;
    private RelativeLayout timeLayout;
    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_table);

        nameLayout = (RelativeLayout) findViewById(R.id.info_card1);
        timeLayout = (RelativeLayout) findViewById(R.id.info_card2);

        nameLayout.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = nameLayout.findViewById(R.id.edit_name);

                playerName = name.getText().toString();
                nameLayout.setVisibility(View.GONE);
                timeLayout.setVisibility(View.VISIBLE);

                // Check if no view has focus:
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        timeLayout.findViewById(R.id.btn_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NFCDetectedActivity.class);
                startActivityForResult(i, 0);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("confirmed", true);
                intent.putExtra("pubID", "S'oak");
                intent.putExtra("userName", playerName);

                startActivity(intent);

            }
        }
    }
}
