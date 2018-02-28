package uk.co.cue.app.services;

import android.app.Activity;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * Created by Jonathan on 26/02/2018.
 */

public class NFCManager {

    private Activity act;
    private NfcAdapter nfcAdapter;

    public NFCManager(Activity activity) {
        this.act = activity;
    }

    public void verifyNFC() throws NFCNotSupported, NFCNotEnabled {
        nfcAdapter = nfcAdapter.getDefaultAdapter(act);

        if(nfcAdapter == null) {
            throw new NFCNotSupported();
        }

        if(!nfcAdapter.isEnabled()) {
            throw new NFCNotEnabled();
        }
    }

    public void disableDispatch() {
        nfcAdapter.disableForegroundDispatch(act);
    }

    public static class NFCNotSupported extends Exception {

        public NFCNotSupported() {
            super();
        }
    }

    public static class NFCNotEnabled extends Exception {

        public NFCNotEnabled() {
            super();
        }
    }

    public NdefMessage createUriMessage(String content) {
        NdefRecord record = NdefRecord.createUri(content);
        NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
        return msg;
    }

    public void writeTag(Tag tag, NdefMessage content) {
        if(tag!=null) {
            try {
                Ndef defTag = Ndef.get(tag);

                if(defTag == null) {
                    NdefFormatable nForm = NdefFormatable.get(tag);
                    if(nForm == null) {
                        nForm.connect();
                        nForm.format(content);
                        nForm.close();
                    }
                } else {
                    defTag.connect();
                    defTag.writeNdefMessage(content);
                    defTag.close();
                }
            } catch(Exception e) {
                //do something better here
                e.printStackTrace();
            }
        }
    }
}
