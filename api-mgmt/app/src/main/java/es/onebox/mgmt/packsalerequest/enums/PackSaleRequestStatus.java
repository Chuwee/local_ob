package es.onebox.mgmt.packsalerequest.enums;

import java.util.stream.Stream;

public enum PackSaleRequestStatus {
    REJECTED(0),
    PENDING(1),
    ACCEPTED(2);


    private final Integer id;

    PackSaleRequestStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PackSaleRequestStatus getById(final Integer id) {
        return Stream.of(values()).filter(field -> field.getId().equals(id)).findFirst().orElse(null);
    }
}
