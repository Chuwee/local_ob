package es.onebox.mgmt.b2b.clients.enums;

import org.apache.commons.lang3.BooleanUtils;

public enum ClientStatus {
    ACTIVE,
    INACTIVE;

    public static ClientStatus fromBoolean(Boolean value) {
        return BooleanUtils.isTrue(value) ? ACTIVE : INACTIVE;
    }
}
