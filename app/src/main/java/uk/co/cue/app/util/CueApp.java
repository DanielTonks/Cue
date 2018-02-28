package uk.co.cue.app.util;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by danieltonks on 21/02/2018.
 * <p>
 * This is a class that can hold methods accessible by any activity in the entire app, through (CueApp) getApplication();
 */

public class CueApp extends Application {

    //ENDPOINTS
    public final static String POST_login = "https://idk-cue.club/user/login";
    public final static String POST_register = "https://idk-cue.club/user/add";
    public final static String POST_isBusiness = "https://idk-cue.club/user/admin";

    private int loggedInUserId;
    private String username;
    private String firebaseToken;
    private boolean isBusiness;

    public CueApp() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        loggedInUserId = -1;

        firebaseToken = FirebaseInstanceId.getInstance().getToken();
        //Log.i("CueApp.java", firebaseToken);
    }

    public boolean isUserLoggedIn() {
        return loggedInUserId != -1;
    }

    public void setLoggedInUser(int id, String username, boolean isBusiness) {
        this.loggedInUserId = id;
        this.username = username;
        this.isBusiness = isBusiness;

    }

    public boolean isBusiness() {
        return isBusiness;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void closeKeyboard(View view) {
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
