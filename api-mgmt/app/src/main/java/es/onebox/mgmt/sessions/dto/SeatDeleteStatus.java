package es.onebox.mgmt.sessions.dto;

public enum SeatDeleteStatus {
    FREE(1),
    LOCKED(3);

    private final Integer id;

    SeatDeleteStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

}

