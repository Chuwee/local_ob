package es.onebox.common.datasources.ms.channel.enums;

import java.util.stream.Stream;

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

    DeliveryMethod(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static DeliveryMethod fromId(Integer id) {
        return Stream.of(values())
                .filter(p -> p.id.equals(id))
                .findAny()
                .orElse(null);
    }
}
