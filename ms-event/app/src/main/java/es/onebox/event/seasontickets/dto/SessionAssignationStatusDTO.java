package es.onebox.event.seasontickets.dto;

import java.io.Serializable;
import java.util.stream.Stream;

public enum  SessionAssignationStatusDTO implements Serializable {

    NOT_ASSIGNED(0),
    ASSIGNED(1);

    private final Integer id;

    SessionAssignationStatusDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static SeasonTicketStatusDTO byId(Integer id) {
        return Stream.of(SeasonTicketStatusDTO.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
