package es.onebox.event.events.enums;

import java.util.stream.Stream;

public enum BookingSessionTimespan {

    DAY((byte) 1),
    WEEK((byte) 2),
    MONTH((byte) 3),
    HOUR((byte) 4);

    private byte tipo;

    BookingSessionTimespan(byte tipo) {
        this.tipo = tipo;
    }

    public byte getTipo() {
        return this.tipo;
    }

    public static BookingSessionTimespan byId(Byte id) {
        return Stream.of(BookingSessionTimespan.values())
                .filter(v -> v.getTipo() == id)
                .findFirst()
                .orElse(null);
    }

}
