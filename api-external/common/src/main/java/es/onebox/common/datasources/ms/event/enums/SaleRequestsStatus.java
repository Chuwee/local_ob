package es.onebox.common.datasources.ms.event.enums;

import java.util.Arrays;

public enum SaleRequestsStatus {
    REJECTED(0),
    PENDING(1),
    ACCEPTED(2),
    PENDING_REQUEST(3);

    private final Integer id;

    SaleRequestsStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static SaleRequestsStatus get(int id) {
        return Arrays.stream(values()).filter(t -> t.getId() == id).findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
