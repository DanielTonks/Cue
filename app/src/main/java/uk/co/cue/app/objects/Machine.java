package uk.co.cue.app.objects;

/**
 * Created by danieltonks on 26/02/2018.
 */

public class Machine {

    private int machine_id;
    private int venue_id;
    private MachineType type;
    private boolean available;
    private float base_price;
    private float current_price;

    public Machine(int machine_id, int venue_id, MachineType type, boolean available, float base_price, float current_price) {
        this.machine_id = machine_id;
        this.venue_id = venue_id;
        this.type = type;
        this.available = available;
        this.base_price = base_price;
        this.current_price = current_price;
    }

    public int getMachine_id() {
        return machine_id;
    }

    public int getVenue_id() {
        return venue_id;
    }

    public MachineType getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public float getBase_price() {
        return base_price;
    }

    public float getCurrent_price() {
        return current_price;
    }

    private enum MachineType {
        POOL, SNOOKER, DARTS
    }
}
