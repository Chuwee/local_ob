package es.onebox.event.sessions.enums;

import java.util.stream.Stream;

public enum SessionTaxesType {
    TICKETS(0),
    TICKET_INVITATION(1),
    CHARGES(3);

    private Integer type;

    SessionTaxesType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static SessionTaxesType getById(Integer id) {
        return Stream.of(values()).filter(el -> el.type.equals(id)).findFirst().orElse(null);
    }
}
