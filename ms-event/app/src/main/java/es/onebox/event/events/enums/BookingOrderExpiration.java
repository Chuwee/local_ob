package es.onebox.event.events.enums;

import java.util.stream.Stream;

public enum BookingOrderExpiration {

    NEVER((byte) 1),
    AFTER_PURCHASE((byte) 2);

    private byte tipo;

    BookingOrderExpiration(byte tipo) {
        this.tipo = tipo;
    }

    public byte getTipo() {
        return this.tipo;
    }

    public static BookingOrderExpiration byId(Byte id) {
        return Stream.of(BookingOrderExpiration.values())
                .filter(v -> v.getTipo() == id)
                .findFirst()
                .orElse(null);
    }

}
