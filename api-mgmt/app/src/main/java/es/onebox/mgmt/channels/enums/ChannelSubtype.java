package es.onebox.mgmt.channels.enums;

import java.util.stream.Stream;

public enum ChannelSubtype {

    EXTERNAL(1),
    WEB(7),
    BOX_OFFICE(8),
    WEB_BOX_OFFICE(10),
    WEB_SUBSCRIBERS(12),
    WEB_B2B(13),
    MEMBERS(14);

    private int id;

    ChannelSubtype(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ChannelSubtype getById(int id) {
        return Stream.of(values())
                .filter(cs -> cs.id == id)
                .findAny()
                .orElse(null);
    }

}
