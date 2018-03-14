package uk.co.cue.app.util;

import java.util.ArrayList;

import uk.co.cue.app.objects.Game;
import uk.co.cue.app.objects.Venue;

/**
 * Created by danieltonks on 28/02/2018.
 */

public class User {

    private int userid;
    private String username;
    private String session;
    private boolean business;
    private ArrayList<Venue> venues;
    private String firebaseToken;
    private Game game;

    public User(int userid, String username, String session, boolean business, ArrayList<Venue> venues) {
        this.userid = userid;
        this.username = username;
        this.session = session;
        this.business = business;
        this.venues = venues;
    }

    public User(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "userid=" + userid +
                ", username='" + username + '\'' +
                ", session='" + session + '\'' +
                ", business=" + business +
                ", venues=" + venues +
                ", firebaseToken='" + firebaseToken + '\'' +
                '}';
    }

    public int getUserid() {
        return userid;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getSession() {
        return session;
    }

    public String getUsername() {
        return username;
    }

    public boolean isBusiness() {
        return business;
    }

    public ArrayList<Venue> getVenues() {
        return venues;
    }


    public void updateUser(User newUser) {
        this.username = newUser.getUsername();
        this.userid = newUser.getUserid();
        this.business = newUser.isBusiness();
        this.venues = newUser.getVenues();
        this.session = newUser.getSession();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
