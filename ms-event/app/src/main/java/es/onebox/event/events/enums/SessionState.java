package es.onebox.event.events.enums;

public enum SessionState {
    DELETED(0),
    SCHEDULED(2),
    CANCELLED(4),
    FINALIZED(7),
    CANCELLED_EXTERNAL(8);
    int state;

    SessionState(int state) {
        this.state = state;
    }

    public int value() {
        return state;
    }

}