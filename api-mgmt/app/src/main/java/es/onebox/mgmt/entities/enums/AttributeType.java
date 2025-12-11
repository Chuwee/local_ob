package es.onebox.mgmt.entities.enums;

public enum AttributeType {

    NUMERIC(0),
    ALPHANUMERIC(1),
    DEFINED(2);

    private int state;

    private AttributeType(int state) {
        this.state = state;
    }

    public int getId() {
        return state;
    }

    public static AttributeType get(int id) {
        return values()[id];
    }

    @Override
    public String toString() {
        return String.valueOf(state);
    }

}
