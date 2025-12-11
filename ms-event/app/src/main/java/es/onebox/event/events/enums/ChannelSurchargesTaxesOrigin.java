package es.onebox.event.events.enums;

import java.util.stream.Stream;

public enum ChannelSurchargesTaxesOrigin {

    EVENT(0),
    CHANNEL(1);

    private int id;

    ChannelSurchargesTaxesOrigin(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ChannelSurchargesTaxesOrigin getById(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(values())
                .filter(ct -> ct.id == id)
                .findFirst()
                .orElse(null);
    }

}
