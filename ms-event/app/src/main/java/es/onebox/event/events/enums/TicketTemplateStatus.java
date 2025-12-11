package es.onebox.event.events.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum TicketTemplateStatus implements Serializable {

    DELETED(0),
    ACTIVE(1);

    private final Integer id;

    TicketTemplateStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static TicketTemplateStatus byId(Integer id) {
        return Stream.of(TicketTemplateStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
