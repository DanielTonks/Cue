package uk.co.cue.app.activity.nfc;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import uk.co.cue.app.R;


/**
 * Class that shows a 'too soon' message if NFC is tapped too early.
 * This class listens for deep links containing the idk-cue.club URL
 */
public class TooSoonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_too_soon);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAnalytics.getInstance(this);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Object[] a = deepLink.getQueryParameterNames().toArray();
                            for (int i = 0; i < a.length; i++) {
                                System.out.println(i + ":" + a[i]);
                            }

                            String pub = deepLink.getQueryParameter("pubID");
                            String tableID = deepLink.getQueryParameter("tableID");

                            System.out.println("PUB: " + pub + ", TABLE: " + tableID);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void cancel(View view) {
        finish();
    }
}
