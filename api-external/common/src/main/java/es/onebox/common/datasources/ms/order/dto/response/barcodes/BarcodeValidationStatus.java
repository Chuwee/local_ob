package es.onebox.common.datasources.ms.order.dto.response.barcodes;

import java.util.Arrays;

public enum BarcodeValidationStatus {

    INVALID, NOT_VALIDATED, VALIDATED, VALIDATED_OUT, LOCKED;


    public static BarcodeValidationStatus fromValue(String value) {
        return Arrays.stream(BarcodeValidationStatus.values())
                .filter(status -> status.name().equals(value))
                .findFirst().orElse(null);
    }

}
