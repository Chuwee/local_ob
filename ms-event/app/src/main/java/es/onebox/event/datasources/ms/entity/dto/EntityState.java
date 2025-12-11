package es.onebox.event.datasources.ms.entity.dto;

public enum EntityState {

    DELETED(0),
    ACTIVE(1),
    PENDING(2),
    BLOCKED(3),
    TEMPORARILY_BLOCKED(4);

    private int state;

    private EntityState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public static EntityState get(int id) {
        return values()[id];
    }

    @Override
    public String toString() {
        return String.valueOf(state);
    }

}
