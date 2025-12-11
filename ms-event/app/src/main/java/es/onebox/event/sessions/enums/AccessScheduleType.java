package es.onebox.event.sessions.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum AccessScheduleType implements Serializable {
    DEFAULT(1),
    SPECIFIC(2);

    private Integer type;

    private AccessScheduleType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static AccessScheduleType byType(Integer type) {
        return Stream.of(AccessScheduleType.values())
                .filter(v -> v.getType().equals(type))
                .findFirst()
                .orElse(null);
    }
}
