package uk.co.cue.app.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by danieltonks on 26/02/2018.
 */

public class Venue implements Parcelable{

    private int venue_id;
    private String venue_name;
    private ArrayList<Machine> machines;
    private double lat;
    private double lon;
    private String googleToken;

    public Venue(int venue_id, String venue_name,  double lat, double lon, String googleToken) {
        this.venue_id = venue_id;
        this.venue_name = venue_name;
        this.machines = new ArrayList<>();
        this.lat = lat;
        this.lon = lon;
        this.googleToken = googleToken;
    }

    public Venue(Parcel in ) {
        venue_id = in.readInt();
        venue_name = in.readString();
        machines = (ArrayList<Machine>) in.readSerializable();
        lat = in.readDouble();
        lon = in.readDouble();
        googleToken = in.readString();
    }

    public int getVenue_id() {
        return venue_id;
    }

    public String toString() { return venue_name; }

    public void setMachines(ArrayList<Machine> machine) { this.machines = machine; }

    public String getVenue_name() {
        return venue_name;
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public String getGoogleToken() { return googleToken; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(venue_id);
        parcel.writeString(venue_name);
        parcel.writeSerializable(machines);
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
        parcel.writeString(googleToken);
    }

    public static final Parcelable.Creator<Venue> CREATOR = new Parcelable.Creator<Venue>() {
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        public Venue[] newArray(int size) {
            return null;
        }
    };

}
