package es.onebox.mgmt.common.auth.enums;

import es.onebox.mgmt.datasources.common.enums.AuthenticatorType;

import java.util.stream.Stream;

public enum AuthenticatorTypeDTO {

    DEFAULT,
    COLLECTIVE,
    VENDOR;

    public static AuthenticatorTypeDTO toDTO(AuthenticatorType authenticatorType) {
        if (authenticatorType == null) {
            return null;
        }
        return Stream.of(values())
                .filter(type -> type.name().equals(authenticatorType.name()))
                .findFirst()
                .orElse(null);
    }

}
