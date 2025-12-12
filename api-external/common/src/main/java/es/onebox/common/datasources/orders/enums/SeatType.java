package es.onebox.common.datasources.orders.enums;

public enum SeatType {

    NUMBERED(0),
    NOT_NUMBERED(1);

    private int id;

    SeatType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
