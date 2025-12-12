package es.onebox.common.datasources.ms.channel.enums;

import java.util.stream.Stream;

public enum EventType {

    NORMAL(1),
    AVET(2),
    ACTIVITY(3),
    THEME_PARK(4),
    SEASON_TICKET(5), 
    PRODUCT(10);

    private final Integer id;

    EventType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static EventType byId(Integer id) {
        return Stream.of(EventType.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
