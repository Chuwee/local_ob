package es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod;

public enum EmailMode {
    TICKET_AND_RECEIPT(1),
    ONLY_TICKET (2),
    ONLY_RECEIPT(3),
    NONE(4),
    UNIFIED_TICKET_AND_RECEIPT(5),
    RECEIPT_AND_PASSBOOK(6);

    private Integer id;

    EmailMode(Integer id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
