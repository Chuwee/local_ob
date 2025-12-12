package es.onebox.common.datasources.ms.channel.enums;

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

    public static ChannelEventStatus fromStatusId(Integer statusId) {
        return Arrays.stream(ChannelEventStatus.values()).filter(s -> statusId.equals(s.getStatus()))
                .findFirst()
                .orElse(null);
    }
}
