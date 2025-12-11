package es.onebox.event.datasources.ms.entity.dto;

public enum AttributeScope {

    EVENT(0),
    SESSION(1),
    GROUP(2),
    PROFILE(3);

    private int state;

    private AttributeScope(int state) {
        this.state = state;
    }

    public int getId() {
        return state;
    }

    public static AttributeScope get(int id) {
        return values()[id];
    }

    @Override
    public String toString() {
        return String.valueOf(state);
    }

}
