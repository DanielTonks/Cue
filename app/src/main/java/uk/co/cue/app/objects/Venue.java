package uk.co.cue.app.objects;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by danieltonks on 26/02/2018.
 */

public class Venue {

    private int venue_id;
    private String venue_name;
    private ArrayList<Machine> machines;
    private LatLng loc;

    public Venue(int venue_id, String venue_name, ArrayList<Machine> machines, LatLng loc) {
        this.venue_id = venue_id;
        this.venue_name = venue_name;
        this.machines = machines;
        this.loc = loc;
    }

    public int getVenue_id() {
        return venue_id;
    }

    public String getVenue_name() {
        return venue_name;
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public LatLng getLoc() {
        return loc;
    }

}
