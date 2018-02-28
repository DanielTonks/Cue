package uk.co.cue.app.services;

/**
 * Created by danieltonks on 19/02/2018.
 */


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import uk.co.cue.app.util.CueApp;


public class CueAlertService extends FirebaseInstanceIdService {

    private static final String TAG = "CueAlertService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        CueApp app = (CueApp) getApplication();
        app.getUser().setFirebaseToken(refreshedToken);
    }



}