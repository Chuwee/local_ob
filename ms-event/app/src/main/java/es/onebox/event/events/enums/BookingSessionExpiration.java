package es.onebox.event.events.enums;

import java.util.stream.Stream;

public enum BookingSessionExpiration {

    BEFORE((byte) 1),
    AFTER((byte) 2);

    private byte tipo;

    BookingSessionExpiration(byte tipo) {
        this.tipo = tipo;
    }

    public byte getTipo() {
        return tipo;
    }

    public static BookingSessionExpiration byId(Byte id) {
        return Stream.of(BookingSessionExpiration.values())
                .filter(v -> v.getTipo() == id)
                .findFirst()
                .orElse(null);
    }

}
