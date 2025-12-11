package es.onebox.mgmt.datasources.ms.ticket.dto;

import java.io.Serializable;
import java.util.stream.Stream;

public enum LinkSeatStatus implements Serializable {

    FREE(TicketStatus.AVAILABLE.getStatus()),
    PROMOTOR_LOCKED(TicketStatus.BLOCKED_PROMOTER.getStatus());

    private int status;

    public int getStatus() {
        return status;
    }

    LinkSeatStatus(int status) {
        this.status = status;
    }

    public static LinkSeatStatus byId(Integer id) {
        return Stream.of(LinkSeatStatus.values())
                .filter(v -> v.getStatus() == id)
                .findFirst()
                .orElse(null);
    }

}
