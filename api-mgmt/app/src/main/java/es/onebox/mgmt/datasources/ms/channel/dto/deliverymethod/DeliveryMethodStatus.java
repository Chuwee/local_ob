package es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod;

public enum DeliveryMethodStatus {
    INACTIVE((byte) 0),
    ACTIVE((byte) 1);

    private Byte id;

    DeliveryMethodStatus(Byte id) {
        this.id = id;
    }

    public Byte getId() {
        return id;
    }
}
