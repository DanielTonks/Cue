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
    private int position;
    private GameChanged listener;
    private double estimatedTime;

    public Game(int venue_id, int queue_id, String venue_name, String category, int position, double estimatedTime) {
        this.venue_id = venue_id;
        this.queue_id = queue_id;
        this.venue_name = venue_name;
        this.category = category;
        this.position = 42;
        this.estimatedTime = estimatedTime;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int queuePosition) {
        this.position = queuePosition;
        listener.gameChanged(this);
    }

    public void setOnGameChangedListener(GameChanged listener) {
        this.listener = listener;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public interface GameChanged {

        void gameChanged(Game g);

    }
}
