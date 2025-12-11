package es.onebox.event.seasontickets.dto.changeseat;

import java.io.Serializable;
import java.util.stream.Stream;

import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;

public enum ChangedSeatStatus implements Serializable {

    FREE(TicketStatus.AVAILABLE.getStatus()),
    PROMOTOR_LOCKED(TicketStatus.BLOCKED_PROMOTER.getStatus()),
    KILL(TicketStatus.KILL.getStatus());

    private int status;

    public int getStatus() {
        return status;
    }

    ChangedSeatStatus(int status) {
        this.status = status;
    }

    public static ChangedSeatStatus byId(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(ChangedSeatStatus.values())
                .filter(v -> v.getStatus() == id)
                .findFirst()
                .orElse(null);
    }

}