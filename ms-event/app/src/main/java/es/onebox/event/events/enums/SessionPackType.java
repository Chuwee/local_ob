package es.onebox.event.events.enums;

import java.util.stream.Stream;

public enum SessionPackType {
    DISABLED((byte) 0),
    RESTRICTED((byte) 1),
    UNRESTRICTED((byte) 2);

    private final byte id;

    SessionPackType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    public static SessionPackType byId(Integer id) {
        return Stream.of(SessionPackType.values())
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static SessionPackType byId(Byte id) {
        return id == null ? null : byId(id.intValue());
    }

}
