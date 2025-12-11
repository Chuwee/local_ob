package es.onebox.mgmt.common.auth.enums;


import es.onebox.mgmt.datasources.common.enums.TriggerOn;

import java.util.stream.Stream;

public enum TriggerOnDTO {
    IMMEDIATELY,
    BEFORE_CHECKOUT,
    BEFORE_SELECT_LOCATION;

    public static TriggerOnDTO toDTO(TriggerOn triggerOn) {
        if (triggerOn == null) {
            return null;
        }
        return Stream.of(values())
                .filter(placement -> placement.name().equals(triggerOn.name()))
                .findFirst()
                .orElse(null);
    }
}
