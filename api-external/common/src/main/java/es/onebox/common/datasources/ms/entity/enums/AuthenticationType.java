package es.onebox.common.datasources.ms.entity.enums;

import java.util.stream.Stream;

public enum AuthenticationType {

    SYNC,
    ASYNC;

    public static AuthenticationType fromDTO(AuthenticationTypeDTO authenticatorTypeDTO) {
        if (authenticatorTypeDTO == null) {
            return null;
        }
        return Stream.of(values())
                .filter(type -> type.name().equals(authenticatorTypeDTO.name()))
                .findFirst()
                .orElse(null);
    }

}