package es.onebox.common.datasources.ms.channel.enums;

import es.onebox.core.utils.common.CommonUtils;

public enum EmailServerType {
    ONEBOX,
    OTHER;

    public static EmailServerType toDto(final Byte ownServer) {
        return CommonUtils.isTrue(ownServer) ? OTHER : ONEBOX;
    }
}
