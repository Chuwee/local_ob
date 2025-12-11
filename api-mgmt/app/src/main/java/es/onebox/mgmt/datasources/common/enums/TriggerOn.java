package es.onebox.mgmt.datasources.common.enums;


import es.onebox.mgmt.common.auth.enums.TriggerOnDTO;

import java.util.stream.Stream;

public enum TriggerOn {
    IMMEDIATELY,
    BEFORE_CHECKOUT,
    BEFORE_SELECT_LOCATION;

    public static TriggerOn fromtDTO(TriggerOnDTO triggerOnDTO) {
        if (triggerOnDTO == null) {
            return null;
        }
        return Stream.of(values())
                .filter(placement -> placement.name().equals(triggerOnDTO.name()))
                .findFirst()
                .orElse(null);
    }
}
