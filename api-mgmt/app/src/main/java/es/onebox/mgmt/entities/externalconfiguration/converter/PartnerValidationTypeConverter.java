package es.onebox.mgmt.entities.externalconfiguration.converter;

import es.onebox.mgmt.entities.externalconfiguration.enums.PartnerValidationType;

public class PartnerValidationTypeConverter {

    public static Boolean toMs(PartnerValidationType partnerValidationType) {
        if (partnerValidationType == null) {
            return null;
        }
        return switch (partnerValidationType) {
            case PERSON -> true;
            case PARTNER -> false;
        };
    }

    public static PartnerValidationType toDto(Boolean partnerValidationType) {
        if (partnerValidationType == null) {
            return null;
        } else if (Boolean.TRUE.equals(partnerValidationType)) {
            return PartnerValidationType.PERSON;
        } else if (Boolean.FALSE.equals(partnerValidationType)) {
            return PartnerValidationType.PARTNER;
        } else {
            throw new IllegalStateException("Unexpected value: " + partnerValidationType);
        }
    }
}
