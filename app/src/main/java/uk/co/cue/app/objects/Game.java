package uk.co.cue.app.objects;

import java.io.Serializable;

/**
 * Created by danieltonks on 11/03/2018.
 */

public class Game implements Serializable {

    private int venue_id;
    private int queue_id;
    private String venue_name;
    private String category;

    public Game(int venue_id, int queue_id, String venue_name, String category) {
        this.venue_id = venue_id;
        this.queue_id = queue_id;
        this.venue_name = venue_name;
        this.category = category;
    }

    public int getVenueID() {
        return venue_id;
    }

    public int getQueueID() {
        return queue_id;
    }

    public String getVenueName() {
        return venue_name;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Game{" +
                "venue_id=" + venue_id +
                ", queue_id=" + queue_id +
                ", venue_name='" + venue_name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
