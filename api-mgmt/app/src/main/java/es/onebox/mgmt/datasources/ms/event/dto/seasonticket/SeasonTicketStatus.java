package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

public enum SeasonTicketStatus {

    DELETED(0),
    SET_UP(1),
    PENDING_PUBLICATION(2),
    READY(3),
    CANCELLED(4),
    FINISHED(7);

    private final Integer id;

    SeasonTicketStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

}