package es.onebox.common.datasources.ms.order.dto;

import java.io.Serializable;

public enum EventType implements Serializable  {
    NORMAL(1),
    AVET(2),
    ACTIVITY(3),
    THEME_PARK(4),
    SEASON_TICKET(5),
    PRODUCT(10);

    private int id;

    private EventType(int id) {
        this.id = id;
    }


    public EventType get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }
}
