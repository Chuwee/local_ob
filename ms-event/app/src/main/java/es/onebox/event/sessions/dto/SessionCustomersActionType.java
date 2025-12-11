package es.onebox.event.sessions.dto;

public enum SessionCustomersActionType {

    INCREMENT(1),
    DECREMENT(2);

    private int id;

    SessionCustomersActionType(int id) {
        this.id = id;
    }

    public int getId() { return id; }



}
