package uk.co.cue.app.activity.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import uk.co.cue.app.R;
import uk.co.cue.app.services.NFCManager;

public class WriteTagActivity extends AppCompatActivity {

    NFCManager nfcManager;
    Tag currentTag;
    private View view;
    private NdefMessage message = null;
    private ProgressDialog dialog;
    private ProgressBar spinner;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag);

        spinner = (ProgressBar)findViewById(R.id.NFCProgressBar);
        txt = (TextView) findViewById(R.id.write_to_nfc_text);

        spinner.setVisibility(View.VISIBLE);

        view = findViewById(R.id.top_layout);

        Intent intent = getIntent();

        String url = intent.getStringExtra("url");

        nfcManager = new NFCManager(this);

        message = nfcManager.createUriMessage(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            nfcManager.verifyNFC();
            Intent intent = new Intent(this, getClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0 );
            IntentFilter[] intentFiltersArray = new IntentFilter[] {};
            String[][] list = new String[][] {
                    {android.nfc.tech.Ndef.class.getName()},
                    {android.nfc.tech.NdefFormatable.class.getName()}
            };
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, list);
        } catch(NFCManager.NFCNotSupported err) {
            Snackbar.make(view, "NFC not supported", Snackbar.LENGTH_LONG).show();
        }
        catch(NFCManager.NFCNotEnabled err) {
            Snackbar.make(view, "NFC not enabled", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcManager.disableDispatch();
    }

    @Override
    public void onNewIntent(Intent intent) {
        currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(message !=null) {
            nfcManager.writeTag(currentTag, message);
            //dialog.dismiss();
            spinner.setVisibility(View.GONE);

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }


}
