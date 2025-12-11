package es.onebox.event.sessions.enums;

import java.util.stream.Stream;

public enum PresaleValidatorType {
    COLLECTIVE(1),
    CUSTOMERS(2);

    private final Integer id;

    PresaleValidatorType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PresaleValidatorType byId(Integer id) {
        return Stream.of(PresaleValidatorType.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
