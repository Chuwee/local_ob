package es.onebox.mgmt.datasources.ms.channel.dto.catalog;

import java.util.Arrays;

public enum ChannelEventStatus {
    REJECTED(0),
    PENDING(1),
    ACCEPTED(2);

    private int status;

    ChannelEventStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public static ChannelEventStatus fromStatusId(Integer statuId) {
        return Arrays.asList(ChannelEventStatus.values())
                .stream().filter(s -> statuId.equals(s.getStatus()))
                .findFirst()
                .orElse(null);
    }
}
