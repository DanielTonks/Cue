package uk.co.cue.app.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.json.JSONObject;

import uk.co.cue.app.R;
import uk.co.cue.app.util.VolleyRequestFactory;

public class NFCDetectedActivity extends AppCompatActivity implements VolleyRequestFactory.VolleyRequest {

    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;
    String mTechLists[][];
    TextView processingText;
    private VolleyRequestFactory vrf;

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

    public String getNdefMessages(Intent intent) {
        String payload = "";
        Parcelable[] rawMessages =
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
                NdefRecord[] records = messages[0].getRecords();
            payload = new String(records[0].getPayload());
                    System.out.println(payload);
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
        getNdefMessages(intent);

        String pubID = "S'Oak"; //hardcoded for now
        Intent returnIntent = new Intent();
        returnIntent.putExtra("pubID", pubID);
        setResult(Activity.RESULT_OK, returnIntent);


        finish();
    }

}
