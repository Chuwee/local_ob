package es.onebox.event.events.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum TicketTemplateFormatModel implements Serializable {

    STANDARD(1),
    TICKET(2);

    private final Integer id;

    TicketTemplateFormatModel(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static TicketTemplateFormatModel byId(Integer id) {
        return Stream.of(TicketTemplateFormatModel.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
