package es.onebox.mgmt.datasources.ms.collective.dto;

import java.util.stream.Stream;

public enum CollectiveType {

    VENUE(1),
    INTERNAL(4),
    EXTERNAL(3),
    USER_CLUB(5);

    private final Integer id;

    CollectiveType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static CollectiveType fromId(final Integer id) {
        return Stream.of(values())
                .filter(p -> p.id.equals(id))
                .findAny()
                .orElse(null);
    }
}
