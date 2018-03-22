package uk.co.cue.app.objects;

/**
 * Created by danieltonks on 17/03/2018.
 */

public class HistoricalGame {

    private Venue venue;
    private String time;
    private String category;
    private double price;
    private String username;
    private String name;

    public HistoricalGame(String time, String category, double price, Venue v) {
        this.time = time;
        this.category = category;
        this.price = price;
        this.venue = v;
    }

    public HistoricalGame(String time, String category, String username, String name, double price) {
        this.time = time;
        this.category = category;
        this.username = username;
        this.name = name;
        this.price = price;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
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

    public Venue getVenue() {
        return venue;
    }

    public String getDateMonth() {
        String year = time.substring(0, 4);
        String month = time.substring(5, 7);
        String day = time.substring(8, 10);
        return day + "/" + month + "/" + year;
    }

    public String getDateTime() {
        String hour = time.substring(11, 13);
        String minute = time.substring(14, 16);

        return hour + ":" + minute;
    }
}
