package es.onebox.mgmt.events.dto;

import java.util.stream.Stream;

public enum RefundTypeDTO {
    NONE(1),
    VOUCHER(2);

    private final Integer id;

    RefundTypeDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static RefundTypeDTO byId(Integer id) {
        return Stream.of(RefundTypeDTO.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}