package es.onebox.mgmt.salerequests.dto;

import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.TaxMode;

import java.io.Serializable;
import java.util.Arrays;

public enum TaxModeDTO implements Serializable {

    INCLUDED,
    ON_TOP;

    public static TaxModeDTO fromMs(TaxMode taxMode) {
        if (taxMode == null) {
            return null;
        }
        return Arrays.stream(TaxModeDTO.values())
                .filter(t -> t.name().equals(taxMode.name()))
                .findFirst()
                .orElse(null);
    }
}
