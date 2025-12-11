package es.onebox.mgmt.events.dto;

import java.util.stream.Stream;

public enum ChangeSeatChangeTypeDTO {
    ALL(1),
    PARTIAL(2);

    private final Integer id;

    ChangeSeatChangeTypeDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChangeSeatChangeTypeDTO byId(Integer id) {
        return Stream.of(ChangeSeatChangeTypeDTO.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
