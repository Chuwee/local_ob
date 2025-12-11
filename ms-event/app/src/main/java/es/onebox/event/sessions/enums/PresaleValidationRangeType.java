package es.onebox.event.sessions.enums;

import java.util.stream.Stream;

public enum PresaleValidationRangeType {
    ALL(0),
    DATE_RANGE(1);

    private final Integer id;

    PresaleValidationRangeType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PresaleValidationRangeType byId(Integer id) {
        return Stream.of(PresaleValidationRangeType.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
