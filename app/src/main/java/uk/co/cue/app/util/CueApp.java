package uk.co.cue.app.util;

import android.app.Application;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by danieltonks on 21/02/2018.
 * <p>
 * This is a class that can hold methods accessible by any activity in the entire app, through (CueApp) getApplication();
 */

public class CueApp extends Application {

    private int loggedInUserId;
    private String firebaseToken;

    public CueApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        loggedInUserId = -1;

        firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Log.i("CueApp.java", firebaseToken);
    }

    public boolean isUserLoggedIn() {
        return loggedInUserId != -1;
    }

    public void setLoggedInUserId(int id) {
        this.loggedInUserId = id;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }
}
