package es.onebox.common.datasources.ms.order.dto;

import java.io.Serializable;

public enum SeatType implements Serializable  {
    NUMBERED(0),
    NOT_NUMBERED(1);

    private int id;

    private SeatType(int id) {
        this.id = id;
    }


    public SeatType get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }
}
