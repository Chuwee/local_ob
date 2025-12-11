package es.onebox.mgmt.datasources.ms.event.dto.session;

public enum SessionSalesType {

    INDIVIDUAL(1),
    GROUP(2),
    MIXED(3);

    private int type;

    SessionSalesType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
