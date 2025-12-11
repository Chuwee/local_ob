package es.onebox.event.events.enums;

import java.util.stream.Stream;

public enum BookingExpirationType {

    NEVER((byte) 1),
    SESSION((byte) 2),
    DATE((byte) 3);

    private byte tipo;

    BookingExpirationType(byte tipo) {
        this.tipo = tipo;
    }

    public byte getTipo() {
        return this.tipo;
    }

    public static BookingExpirationType byId(Byte id) {
        return Stream.of(BookingExpirationType.values())
                .filter(v -> v.getTipo() == id)
                .findFirst()
                .orElse(null);
    }

}
