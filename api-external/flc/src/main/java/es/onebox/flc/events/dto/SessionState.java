package es.onebox.flc.events.dto;

import java.io.Serializable;

public enum SessionState implements Serializable {
    DELETED(0),
    SCHEDULED(1),
    PROGRAMING(2),
    READY(3),
    CANCELLED(4),
    NOT_ACCOMPLISHED(5),
    PROCESSING(6),
    FINISHED(7),
    EXTERNALLY_CANCELLED(8);

    private int id;

    private SessionState(int id) {
        this.id = id;
    }

    public static SessionState get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }
}
