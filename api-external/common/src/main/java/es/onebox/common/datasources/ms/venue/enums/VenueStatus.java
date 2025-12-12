package es.onebox.common.datasources.ms.venue.enums;

public enum VenueStatus {
    DELETED(0),
    ACTIVE(1),
    PENDING(2),
    BLOCKED(3);

    private int state;

    VenueStatus(int state) {
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
