package es.onebox.mgmt.events.dto;

import java.util.stream.Stream;

public enum ChangeSeatAllowedSessionsDTO {
    SAME(1),
    DIFFERENT(2),
    ANY(3);

    private final Integer id;

    ChangeSeatAllowedSessionsDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChangeSeatAllowedSessionsDTO byId(Integer id) {
        return Stream.of(ChangeSeatAllowedSessionsDTO.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
