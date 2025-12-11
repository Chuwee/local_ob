package es.onebox.mgmt.datasources.ms.channel.enums;

import java.util.stream.Stream;

public enum ChannelIntegrationStatus {

    CONNECTED(1),
    DISCONNECTED(0);

    private final int status;

    ChannelIntegrationStatus(int status) {
        this.status = status;
    }

    public static ChannelIntegrationStatus get(int status) {
        return Stream.of(values())
                .filter(cis -> cis.status == status)
                .findAny()
                .orElse(null);
    }

    public int getStatus() {
        return status;
    }
}
