package uk.co.cue.app.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import uk.co.cue.app.R;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;

public class ReserveTableActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest {

    private static long timeUNIX; // holds the unix time of the time the user selected
    private static TextView currentTimeSelected;
    private RelativeLayout nameLayout;
    private RelativeLayout timeLayout;
    private String playerName;

    private CueApp app;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient locationManager;
    private VolleyRequestFactory vrf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_table);
        this.app = (CueApp) getApplication();
        setTitle("Reserve a game");
        currentTimeSelected = findViewById(R.id.currentTimeSelected);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);


        findViewById(R.id.btn_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimeSelected.setText("Now");
                timeUNIX = 0;

            }
        });

        findViewById(R.id.btn_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        findViewById(R.id.btn_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(timeUNIX);
                Intent i = new Intent(getApplicationContext(), NFCDetectedActivity.class);
                i.putExtra("type", "Reserve");
                startActivityForResult(i, 0);
            }
        });

        locationManager = LocationServices.getFusedLocationProviderClient(this);
        vrf = new VolleyRequestFactory(this, getApplicationContext());



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("game", data.getSerializableExtra("game"));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        } else if (requestCode == 1) {
            System.out.println("Got back from map");
        }
    }

    @Override
    public void requestFinished(JSONObject response, String url) {
        System.out.println(response.toString());
        JSONArray arr = null;
        try {
            arr = response.getJSONArray("results");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject place = arr.getJSONObject(i);
                System.out.println(place.getString("name"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void requestFailed(int statusCode) {

    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            TimePickerDialog dialog = new TimePickerDialog(getActivity(), R.style.TimePickerTheme, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

            return dialog;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            minute = roundMinute(minute);
            if (minute < 10) {
                currentTimeSelected.setText(hourOfDay + ":0" + minute);
            } else {
                currentTimeSelected.setText(hourOfDay + ":" + minute);
            }

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);

            if ((System.currentTimeMillis() / 1000L) > cal.getTimeInMillis() / 1000) {
                timeUNIX = 0;
                currentTimeSelected.setText("Now");
            } else {
                timeUNIX = cal.getTimeInMillis() / 1000;
            }
        }

        private int roundMinute(int minute) {
            int temp = minute % 5;
            if (temp < 3)
                return minute - temp;
            else
                return minute + 5 - temp;
        }


    }
}
