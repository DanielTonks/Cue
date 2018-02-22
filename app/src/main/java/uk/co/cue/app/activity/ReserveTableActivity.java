package uk.co.cue.app.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.util.CueApp;

public class ReserveTableActivity extends AppCompatActivity {

    private static long timeUNIX; // holds the unix time of the time the user selected
    private static TextView currentTimeSelected;
    private RelativeLayout nameLayout;
    private RelativeLayout timeLayout;
    private String playerName;

    private CueApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_table);
        this.app = (CueApp) getApplication();

        nameLayout = (RelativeLayout) findViewById(R.id.info_card1);
        timeLayout = (RelativeLayout) findViewById(R.id.info_card2);

        currentTimeSelected = timeLayout.findViewById(R.id.currentTimeSelected);

        nameLayout.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = nameLayout.findViewById(R.id.edit_name);

                playerName = name.getText().toString();

                if (playerName.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                nameLayout.setVisibility(View.GONE);
                timeLayout.setVisibility(View.VISIBLE);

                // Check if no view has focus:
                app.closeKeyboard(getCurrentFocus());

            }
        });

        nameLayout.findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        timeLayout.findViewById(R.id.btn_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimeSelected.setText("Now");
                timeUNIX = 0;

            }
        });

        timeLayout.findViewById(R.id.btn_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        timeLayout.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeLayout.setVisibility(View.GONE);
                nameLayout.setVisibility(View.VISIBLE);
            }
        });

        timeLayout.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(timeUNIX);
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
                System.out.println("Got back, here is value of timeUNIX: " + timeUNIX);

                System.out.println("About to make a request...");

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest postRequest = new StringRequest(Request.Method.POST, "https://idk-cue.club/queue/add",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    System.out.println("Response: " + response);
                                } catch (Exception err) {
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println(error.getMessage());
                            }
                        }

                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("user_id","1");
//                        params.put("machine_id","1");
//                        params.put("num_players","2");
//                        params.put("matchmaking","0");
//                        params.put("time_add",String.valueOf(timeUNIX));


//                        params.put("username", "dantheman");
//                        params.put("password", "hello");
//                        params.put("name", "Daniel");
//                        params.put("device_id", "fyXUXueWMOE:APA91bEGNvd3tpabf3enYo4ooSnPM1l9s3RTLrKKNhLGlGspT0F2QjqqzCdbLcyfnMIHShQr8mhmP_Lrcdm_obNyT8b5MtiHQ2P7qAeSscO0hnQkje0oxQ7Or5zJ9pbfsPuepR0bGyiz");

                        return params;
                    }
                };
                requestQueue.add(postRequest);


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("confirmed", true);
                intent.putExtra("pubID", "S'oak");
                intent.putExtra("userName", playerName);

                startActivity(intent);

            }
        }
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
