package es.onebox.common.datasources.ms.event.enums;

import java.util.Arrays;

public enum TaxModeDTO {
    INCLUDED(0),
    ON_TOP(1);

    private final Integer id;

    TaxModeDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static TaxModeDTO fromId(Integer id) {
        return Arrays.stream(TaxModeDTO.values())
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
