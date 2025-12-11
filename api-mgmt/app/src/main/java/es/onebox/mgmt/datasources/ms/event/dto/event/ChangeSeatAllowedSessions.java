package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.util.stream.Stream;

public enum ChangeSeatAllowedSessions {
    SAME(1),
    DIFFERENT(2),
    ANY(3);

    private final Integer id;

    ChangeSeatAllowedSessions(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChangeSeatAllowedSessions byId(Integer id) {
        return Stream.of(ChangeSeatAllowedSessions.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
