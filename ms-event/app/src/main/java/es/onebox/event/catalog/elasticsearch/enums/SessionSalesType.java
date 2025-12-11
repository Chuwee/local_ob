package es.onebox.event.catalog.elasticsearch.enums;

public enum SessionSalesType {

    INDIVIDUAL(1),
    GROUP(2),
    MIXED(3);

    private final int type;

    SessionSalesType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
