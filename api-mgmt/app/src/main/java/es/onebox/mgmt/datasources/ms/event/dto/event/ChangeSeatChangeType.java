package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.util.stream.Stream;

public enum ChangeSeatChangeType {
    ALL(1),
    PARTIAL(2);

    private final Integer id;

    ChangeSeatChangeType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChangeSeatChangeType byId(Integer id) {
        return Stream.of(ChangeSeatChangeType.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
