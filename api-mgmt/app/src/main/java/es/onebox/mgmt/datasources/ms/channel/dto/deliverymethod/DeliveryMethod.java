package es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod;

public enum DeliveryMethod {
    PRINT_AT_HOME(1),
    TAQ_PICKUP(2),
    PRINT_EXPRESS(3),
    PHONE(4),
    EXTERNAL_CHANNEL(5),
    NATIONAL_POST_DELIVERY(6),
    INTERNATIONAL_POST_DELIVERY(7),
    WHATSAPP(8);

    private Integer id;

    DeliveryMethod(Integer id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
