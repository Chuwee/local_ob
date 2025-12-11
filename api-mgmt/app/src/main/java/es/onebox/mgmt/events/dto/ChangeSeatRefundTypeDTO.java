package es.onebox.mgmt.events.dto;

import java.util.stream.Stream;

public enum ChangeSeatRefundTypeDTO {
    NONE(1),
    VOUCHER(2);

    private final Integer id;

    ChangeSeatRefundTypeDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ChangeSeatRefundTypeDTO byId(Integer id) {
        return Stream.of(ChangeSeatRefundTypeDTO.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
