package es.onebox.mgmt.datasources.api.accounting.dto;

public enum TransactionSupportType {

    CHECK(1),
    CASH(2),
    WIRE(3);

    private int id;

    TransactionSupportType(int id) {
        this.id = id;
    }

    public static TransactionSupportType get(int type) {
        return values()[type - 1];
    }

    public int getId() {
        return id;
    }

}
