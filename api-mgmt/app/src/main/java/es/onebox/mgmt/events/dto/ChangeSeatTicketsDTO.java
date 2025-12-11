package es.onebox.mgmt.events.dto;

import java.util.stream.Stream;

public enum ChangeSeatTicketsDTO {
    GREATER_OR_EQUAL(1),
    ANY(2);

    private final Integer id;

    ChangeSeatTicketsDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChangeSeatTicketsDTO byId(Integer id) {
        return Stream.of(ChangeSeatTicketsDTO.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
