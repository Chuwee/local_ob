package es.onebox.mgmt.venues.enums;

public enum VenueStatus {

    ACTIVE(1),
    PENDING(2),
    BLOCKED(3);

    private int state;

    private VenueStatus(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public static VenueStatus get(int id) {
        return values()[id];
    }

    @Override
    public String toString() {
        return String.valueOf(state);
    }

}
