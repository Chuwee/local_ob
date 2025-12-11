package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.util.stream.Stream;

public enum ChangeSeatRefundType {
    NONE(1),
    VOUCHER(2);

    private final Integer id;

    ChangeSeatRefundType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChangeSeatRefundType byId(Integer id) {
        return Stream.of(ChangeSeatRefundType.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
