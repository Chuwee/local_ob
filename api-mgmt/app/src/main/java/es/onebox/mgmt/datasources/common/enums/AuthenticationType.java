package es.onebox.mgmt.datasources.common.enums;

import es.onebox.mgmt.common.auth.dto.AuthenticatorDTO;
import es.onebox.mgmt.common.auth.enums.AuthenticationTypeDTO;
import es.onebox.mgmt.common.auth.enums.AuthenticatorTypeDTO;

import java.util.Objects;
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

    public static AuthenticationType fromSettings(AuthenticatorDTO authenticatorDTO) {
        if (authenticatorDTO == null) {
            return null;
        }
        if (AuthenticatorTypeDTO.VENDOR.equals(authenticatorDTO.getType())) {
            return ASYNC;
        }
        return SYNC;
    }

}
