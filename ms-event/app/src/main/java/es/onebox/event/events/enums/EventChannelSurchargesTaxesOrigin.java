package es.onebox.event.events.enums;

import java.util.stream.Stream;

public enum EventChannelSurchargesTaxesOrigin {

    CHANNEL(0),
    EVENT(1),
    SALE_REQUEST(2);

    private int id;

    EventChannelSurchargesTaxesOrigin(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EventChannelSurchargesTaxesOrigin getById(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(values())
                .filter(ct -> ct.id == id)
                .findFirst()
                .orElse(null);
    }

}
