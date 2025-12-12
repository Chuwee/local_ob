package es.onebox.common.datasources.ms.channel.enums;

import java.io.Serializable;
import java.util.Arrays;

public enum EventStatus implements Serializable {
    DELETED(0),
    PLANNED(1),
    IN_PROGRAMMING(2),
    READY(3),
    NOT_ACCOMPLISHED(4),
    CANCELLED(5),
    IN_PROGRESS(6),
    FINISHED(7);

    private final Integer id;

    EventStatus(int id) {
        this.id = id;
    }

    public static EventStatus get(int type) {
        EventStatus result = Arrays.stream(values()).filter((et) -> {
            return et.getId() == type;
        }).findAny().orElse(null);
        if (result == null) {
            throw new RuntimeException("No EventStatus with id " + type + " found");
        } else {
            return result;
        }
    }

    public int getId() {
        return this.id;
    }
}
