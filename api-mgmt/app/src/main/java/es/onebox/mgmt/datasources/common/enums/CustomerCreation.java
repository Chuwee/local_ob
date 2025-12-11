package es.onebox.mgmt.datasources.common.enums;

import es.onebox.mgmt.common.auth.enums.CustomerCreationDTO;

import java.util.stream.Stream;

public enum CustomerCreation {

    ENABLED,
    DISABLED;

    public static CustomerCreation fromDTO(CustomerCreationDTO customerCreation) {
        if (customerCreation == null) {
            return null;
        }
        return Stream.of(values())
                .filter(type -> type.name().equals(customerCreation.name()))
                .findFirst()
                .orElse(null);
    }
}
