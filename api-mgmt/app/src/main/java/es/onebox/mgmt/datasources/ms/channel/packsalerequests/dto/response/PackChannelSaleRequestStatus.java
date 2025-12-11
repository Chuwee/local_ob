package es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response;

import java.util.stream.Stream;

public enum PackChannelSaleRequestStatus {
    REJECTED(0),
    PENDING(1),
    ACCEPTED(2);


    private final Integer id;

    PackChannelSaleRequestStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PackChannelSaleRequestStatus getById(final Integer id) {
        return Stream.of(values()).filter(field -> field.getId().equals(id)).findFirst().orElse(null);
    }
}
