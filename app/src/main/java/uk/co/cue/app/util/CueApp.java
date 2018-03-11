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
    public final static String POST_logout = "https://idk-cue.club/user/logout";
    public final static String POST_register = "https://idk-cue.club/user/add";
    public final static String POST_add_machine = "https://idk-cue.club/machine/add";
    public final static String GET_MACHINES = "https://idk-cue.club/machines/all";
    public final static String GET_local_venues = "https://idk-cue.club/venues/nearby";
    public final static String POST_add_queue = "https://idk-cue.club/queue/join";
    public final static String POST_edit_queue = "https://idk-cue.club/queue/edit";

    private User user;

    public CueApp() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();

        user = new User(firebaseToken); // if this is null then we can update it with its actual value later
    }

    public User getUser() {
        return user;
    }

    public void setUser(User newUser) {
        System.out.println("Setting new user: " + newUser.toString());
        this.user.updateUser(newUser); // update the user so we don't overwrite their firebase token.
    }

    public void closeKeyboard(View view) {
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
