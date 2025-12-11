package es.onebox.event.seasontickets.dto;

import java.io.Serializable;
import java.util.stream.Stream;

public enum SeasonTicketStatusDTO implements Serializable {

    DELETED(0),
    SET_UP(1),
    PENDING_PUBLICATION(2),
    READY(3),
    FINISHED(7),
    CANCELLED(10);

    private final Integer id;

    SeasonTicketStatusDTO(Integer id) {
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