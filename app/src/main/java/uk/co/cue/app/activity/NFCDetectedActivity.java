package uk.co.cue.app.activity;

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
import android.widget.TextView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.co.cue.app.R;
import uk.co.cue.app.util.VolleyRequestFactory;

public class NFCDetectedActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest {

    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;
    String mTechLists[][];
    TextView processingText;
    private VolleyRequestFactory vrf;
    private String venueID;
    private String machineID;

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

    }

    @Override
    public void requestFailed(int statusCode) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.vrf = new VolleyRequestFactory(this, getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        setContentView(R.layout.activity_nfcdetected);

        this.processingText = findViewById(R.id.processingText);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);




        mTechLists = new String[][]{
                new String[]{
                        Ndef.class.getName()
                }
        };
        //Intent intent = getIntent();
        //getNdefMessages(intent);
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


        System.out.println("INTENT DATA: " + intent.getDataString());



        String pubID = "S'Oak"; //hardcoded for now
        Intent returnIntent = new Intent();
        System.out.println("machineID: "+ machineID);
        System.out.println("venueID: "+ venueID);
        returnIntent.putExtra("pubID", venueID);
        setResult(Activity.RESULT_OK, returnIntent);


        finish();
    }

}
