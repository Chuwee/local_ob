package es.onebox.common.datasources.ms.entity.enums;

import java.util.stream.Stream;

public enum AuthenticationTypeDTO {

    SYNC,
    ASYNC;

    public static AuthenticationTypeDTO toDTO(AuthenticationType authenticationType) {
        if (authenticationType == null) {
            return null;
        }
        return Stream.of(values())
                .filter(type -> type.name().equals(authenticationType.name()))
                .findFirst()
                .orElse(null);
    }

}
