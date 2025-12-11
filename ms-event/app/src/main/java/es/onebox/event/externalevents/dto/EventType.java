package es.onebox.event.externalevents.dto;

public enum EventType {
    EVENT(1),
    SEASON_TICKET(2);

    private final Integer id;

    EventType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

}
