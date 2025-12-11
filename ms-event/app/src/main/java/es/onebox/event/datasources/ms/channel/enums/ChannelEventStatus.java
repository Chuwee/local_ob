package es.onebox.event.datasources.ms.channel.enums;

import java.util.Arrays;

public enum ChannelEventStatus {
    REJECTED(0),
    PENDING(1),
    ACCEPTED(2);

    private final int status;

    ChannelEventStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public String getLiteral() {
        return switch (this) {
            case REJECTED -> "RECHAZADA";
            case ACCEPTED -> "ACEPTADA";
            case PENDING -> null;
        };
    }

    public static ChannelEventStatus fromStatusId(Integer statusId) {
        return Arrays.stream(ChannelEventStatus.values()).filter(s -> statusId.equals(s.getStatus()))
                .findFirst()
                .orElse(null);
    }
}
