package es.onebox.common.datasources.ms.order.dto;

public enum AttributeSelectionTypes {
    SINGLE(0),
    MULTIPLE(1);

    private int id;

    private AttributeSelectionTypes(int id) {
        this.id = id;
    }

    public static AttributeSelectionTypes get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }

}
