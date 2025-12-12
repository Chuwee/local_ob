package es.onebox.common.datasources.ms.ticket.dto;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum TicketStatus implements Serializable {
    AVAILABLE(1),
    SOLD(2),
    BLOCKED_PROMOTER(3),
    BLOCKED_SYSTEM(4),
    BOOKED(5),
    KILL(6),
    ISSUED(7),
    VALIDATED(8),
    REFUNDED(9),
    CANCELLED(10),
    BLOCKED_PRESALE(11),
    BLOCKED_SALE(12),
    INVITATION(13),
    BLOCKED_SEASON_TICKET(14),
    BLOCKED_EXTERNAL(15),
    DELETED_EXTERNAL(16);

    private final int status;

    private static final Map<Integer, TicketStatus> lookup = new HashMap<>();

    static {
        for (TicketStatus ticketStatus : EnumSet.allOf(TicketStatus.class)) {
            lookup.put(ticketStatus.getStatus(), ticketStatus);
        }
    }

    TicketStatus(int estado) {
        this.status = estado;
    }

    public int getStatus() {
        return status;
    }

    public static TicketStatus getById(Integer id) {
        return lookup.get(id);
    }

    public static TicketStatus getById(Long id) {
        return id == null ? null : lookup.get(id.intValue());
    }
}
