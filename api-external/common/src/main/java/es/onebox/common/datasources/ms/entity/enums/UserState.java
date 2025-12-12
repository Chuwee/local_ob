package es.onebox.common.datasources.ms.entity.enums;

public enum UserState {
    DELETED(0),
    ACTIVE(1),
    PENDING(2),
    BLOCKED(3),
    TEMPORARILY_BLOCKED(4);

    private int state;

    UserState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public static UserState get(int id) {
        return values()[id];
    }

    @Override
    public String toString() {
        return String.valueOf(state);
    }
}
