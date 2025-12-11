package es.onebox.mgmt.common.auth.enums;

import es.onebox.mgmt.datasources.common.enums.CustomerCreation;

import java.util.stream.Stream;

public enum CustomerCreationDTO {

    ENABLED,
    DISABLED;

    public static CustomerCreationDTO toDTO(CustomerCreation customerCreation) {
        if (customerCreation == null) {
            return null;
        }
        return Stream.of(values())
                .filter(type -> type.name().equals(customerCreation.name()))
                .findFirst()
                .orElse(null);
    }

}
