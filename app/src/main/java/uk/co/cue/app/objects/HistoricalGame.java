package uk.co.cue.app.objects;

/**
 * Created by danieltonks on 17/03/2018.
 */

public class HistoricalGame {

    private String time;
    private String category;
    private double price;
    private String venueName;
    private String venueToken;

    public HistoricalGame(String time, String category, double price, String venueName, String venueToken) {
        this.time = time;
        this.category = category;
        this.price = price;
        this.venueName = venueName;
        this.venueToken = venueToken;
    }

    public String getTime() {
        return time;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getVenueName() {
        return venueName;
    }

    public String getVenueToken() {
        return venueToken;
    }
}
