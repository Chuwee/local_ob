package es.onebox.event.datasources.ms.entity.dto;

public enum AttributeSelectionType {

    SINGLE(0),
    MULTIPLE(1);

    private int state;

    private AttributeSelectionType(int state) {
        this.state = state;
    }

    public int getId() {
        return state;
    }

    public static AttributeSelectionType get(int id) {
        return values()[id];
    }

    @Override
    public String toString() {
        return String.valueOf(state);
    }


}
