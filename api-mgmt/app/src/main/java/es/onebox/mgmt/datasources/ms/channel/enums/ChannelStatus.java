package es.onebox.mgmt.datasources.ms.channel.enums;

import java.util.stream.Stream;

public enum ChannelStatus {

    DELETED(0),
    ACTIVE(1),
    BLOCKED(2),
    BLOCKED_TEMPORARILY(3),
    PENDING(4);

    private int id;

    ChannelStatus(Integer id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ChannelStatus getById(int id) {
        return Stream.of(values())
                .filter(status -> status.id == id)
                .findFirst()
                .orElse(null);
    }
}
