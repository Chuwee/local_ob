package es.onebox.mgmt.entities.externalconfiguration.converter;

import es.onebox.mgmt.entities.externalconfiguration.enums.CapacityNameType;

public class CapacityNameTypeConverter {

    public static Boolean toMs(CapacityNameType capacityNameType) {
        if (capacityNameType == null) {
            return null;
        }
        return switch (capacityNameType) {
            case FULL_NAME -> false;
            case SHORT_NAME -> true;
        };
    }

    public static CapacityNameType toDto(Boolean msValue) {
        if (msValue == null) {
            return null;
        } else if (msValue) {
            return CapacityNameType.SHORT_NAME;
        } else {
            return CapacityNameType.FULL_NAME;
        }
    }
}
