package es.onebox.mgmt.datasources.api.accounting.dto;

public enum MovementType {

    ADD_AMOUNT(1),
    MODIFY_AMOUNT(2),
    PAYMENT(3),
    REFUND(4),
    CHANGE_MAX_CREDIT(5);

    private int id;

    MovementType(int id) {
        this.id = id;
    }

    public static MovementType get(int type) {
        return values()[type - 1];
    }

    public int getId() {
        return id;
    }

}
