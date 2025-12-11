package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.util.stream.Stream;

public enum ChangeSeatTickets {
    GREATER_OR_EQUAL(1),
    ANY(2);

    private final Integer id;

    ChangeSeatTickets(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChangeSeatTickets byId(Integer id) {
        return Stream.of(ChangeSeatTickets.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
