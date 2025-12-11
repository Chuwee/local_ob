package es.onebox.event.events.enums;

import java.util.stream.Stream;

public enum BookingOrderTimespan {

    DAY((byte) 1),
    WEEK((byte) 2),
    MONTH((byte) 3);

    private byte tipo;

    BookingOrderTimespan(byte tipo) {
        this.tipo = tipo;
    }

    public byte getTipo() {
        return this.tipo;
    }

    public static BookingOrderTimespan byId(Byte id) {
        return Stream.of(BookingOrderTimespan.values())
                .filter(v -> v.getTipo() == id)
                .findFirst()
                .orElse(null);
    }

}
