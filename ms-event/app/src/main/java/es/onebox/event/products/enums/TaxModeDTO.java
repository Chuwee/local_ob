package es.onebox.event.products.enums;

import java.io.Serializable;
import java.util.Arrays;

public enum TaxModeDTO implements Serializable {

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
