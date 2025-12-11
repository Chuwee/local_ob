package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.events.enums.TaxModeDTO;

import java.io.Serializable;
import java.util.Arrays;

public enum TaxMode implements Serializable {

    INCLUDED,
    ON_TOP;

    public static TaxMode fromDTO(TaxModeDTO taxMode) {
        if (taxMode == null) {
            return null;
        }
        return Arrays.stream(TaxMode.values())
                .filter(t -> t.name().equals(taxMode.name()))
                .findFirst()
                .orElse(null);
    }

    public static TaxMode fromDTO(es.onebox.mgmt.products.enums.TaxModeDTO taxMode) {
        if (taxMode == null) {
            return null;
        }
        return Arrays.stream(TaxMode.values())
                .filter(t -> t.name().equals(taxMode.name()))
                .findFirst()
                .orElse(null);
    }
}
