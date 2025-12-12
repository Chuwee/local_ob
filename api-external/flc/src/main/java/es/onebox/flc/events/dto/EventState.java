package es.onebox.flc.events.dto;

import java.io.Serializable;

public enum EventState implements Serializable {
    DELETED(0),
    PLANNED(1),
    PROGRAMMING(2),
    READY(3),
    NOT_DONE(4),
    CANCELLED(5),
    IN_PROGRESS(6),
    COMPLETED(7);

    private int id;

    EventState(Integer id) {
        this.id = id;
    }

    public static EventState get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }
}
