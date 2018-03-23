package uk.co.cue.app.activity.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.objects.Game;
import uk.co.cue.app.util.CueApp;
import uk.co.cue.app.util.VolleyRequestFactory;

public class NFCDetectedActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest {

    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;
    String mTechLists[][];
    TextView processingText;
    private VolleyRequestFactory vrf;
    private String venueID;
    private String machineID;
    private CueApp app;
    private boolean reserve = true;
    private boolean delete = false;
    private String toDelete;
    private String price;
    private String category;

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {

        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        query = query.replace("link=https://idk-cue.club/queue/add?", "");
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            System.out.println(pair);
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    @Override
    public void requestFinished(JSONObject response, String url) {
        if(url.equals(app.POST_add_queue)) {
            try {
                JSONObject obj = response.getJSONObject("Queue");
                int wait = obj.getInt("wait_time");
                int pos = obj.getInt("queue_pos");
                Intent returnIntent = new Intent();
                Game g = new Game(obj.getInt("venue_id"), obj.getInt("queue_id"), obj.getString("venue_name"), obj.getString("category"), pos, wait);
                returnIntent.putExtra("game", g);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (url.contains(app.POST_game_start)) {
            Intent returnIntent = new Intent();
            try {
                String resp = response.getString("Start");
                setResult(Activity.RESULT_OK, returnIntent);
                returnIntent.putExtra("Response", resp);
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    @Override
    public void requestFailed(int statusCode) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("serverError", true);
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcdetected);
        this.app = (CueApp) getApplication();
        this.processingText = findViewById(R.id.processingText);
        this.vrf = new VolleyRequestFactory(this, getApplicationContext());

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        mTechLists = new String[][]{
                new String[]{
                        Ndef.class.getName()
                }
        };
        Bundle b = getIntent().getExtras();
        if (b.getString("type").equals("Reserve")) {
            processingText.setText("Tap the tag on any table");
        } else if(b.getString("type").equals("Delete")) {
            processingText.setText("Tap the tag of the machine you want to delete");
            delete = true;
            reserve = false;
        } else {
            if (app.getUser().getGame().getAmountOfMachines() > 1) {
                processingText.setText("Tap the beeping/flashing machine to start the game");
            } else {
                processingText.setText("Tap the machine to start the game");
            }
            reserve = false;
        }
    }

    public Uri getNdefMessages(Intent intent) {
        Uri payload = null;
        Parcelable[] rawMessages =
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for(int i=0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
                NdefRecord[] records = messages[i].getRecords();
                        for(NdefRecord r : records) {
                            payload = r.toUri();
                            //System.out.println(payload);
                        }
            }

        }

        return payload;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
        }
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, mTechLists);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null)
            mAdapter.disableForegroundDispatch(this);
    }


    /**
     * When the tag is tapped, this function gets an intent which enables it to read the link on the tag.
     *
     * @param intent
     */
    @Override
    public void onNewIntent(Intent intent) {
        processingText.setText("Processing");

        Log.i("Foreground dispatch", "Discovered tag with intent:" + intent);
        Uri link = getNdefMessages(intent);

        try {
            URL url = new URL(link.toString());
            System.out.println("NOW: " + url.getQuery());
            Map<String, String> queries = splitQuery(url);
            machineID = queries.get("machine_id");
            venueID = queries.get("venue_id");
        } catch(MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        app = (CueApp) getApplication();


        if (reserve) {
            //Make a network request to log in.
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", String.valueOf(app.getUser().getUserid()));
            params.put("machine_id", machineID);
            params.put("session_cookie", app.getUser().getSession());

            vrf.doRequest(app.POST_add_queue, params, Request.Method.POST);
        } else if(delete) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", String.valueOf(app.getUser().getUserid()));
            params.put("machine_id", machineID);
            params.put("venue_id", venueID);
            params.put("session_cookie", app.getUser().getSession());

            vrf.doRequest(app.POST_delete_machine, params, Request.Method.POST);
        } else { // user wants to confirm presence
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", String.valueOf(app.getUser().getUserid()));
            params.put("machine_id", machineID);
            params.put("session_cookie", app.getUser().getSession());
            vrf.doRequest(app.POST_game_start, params, Request.Method.POST); // start the game with this machine_id


        }
    }

    // The user clicked 'cancel'
    public void cancel(View view) {
        Intent returnIntent = new Intent();
        if (processingText.getText().toString().equals("Processing")) {
            setResult(Activity.RESULT_OK, returnIntent); // We can say it's OK because it will have still made the web request
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        finish();
    }
}
