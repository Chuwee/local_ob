package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.util.stream.Stream;

public enum ChangeSeatAmountType {
    GREATER_OR_EQUAL(1),
    ANY(2);

    private final Integer id;

    ChangeSeatAmountType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChangeSeatAmountType byId(Integer id) {
        return Stream.of(ChangeSeatAmountType.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
