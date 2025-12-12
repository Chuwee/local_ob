package es.onebox.common.datasources.ms.entity.enums;

import java.util.stream.Stream;

public enum AuthenticatorType {

    DEFAULT,
    COLLECTIVE,
    VENDOR;

    public static AuthenticatorType fromDTO(AuthenticatorTypeDTO authenticatorTypeDTO) {
        if (authenticatorTypeDTO == null) {
            return null;
        }
        return Stream.of(values())
                .filter(type -> type.name().equals(authenticatorTypeDTO.name()))
                .findFirst()
                .orElse(null);
    }

}
