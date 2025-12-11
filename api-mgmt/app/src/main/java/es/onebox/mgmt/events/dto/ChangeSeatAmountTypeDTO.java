package es.onebox.mgmt.events.dto;

import java.util.stream.Stream;

public enum ChangeSeatAmountTypeDTO {
    GREATER_OR_EQUAL(1),
    ANY(2);

    private final Integer id;

    ChangeSeatAmountTypeDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChangeSeatAmountTypeDTO byId(Integer id) {
        return Stream.of(ChangeSeatAmountTypeDTO.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
