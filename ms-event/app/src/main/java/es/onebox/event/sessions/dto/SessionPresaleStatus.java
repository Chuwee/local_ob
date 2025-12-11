package es.onebox.event.sessions.dto;

public enum SessionPresaleStatus {
    ACTIVE(true), INACTIVE(false);

    private final boolean value;

    SessionPresaleStatus(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
