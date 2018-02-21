package uk.co.cue.app;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SetupTagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_tag);
    }


    public void onClickSubmitSetup(View view) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Creating your link...");
        dialog.setTitle("Machine creation");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        dialog.setCancelable(false);
    }
}
