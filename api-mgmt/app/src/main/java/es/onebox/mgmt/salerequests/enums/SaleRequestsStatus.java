package es.onebox.mgmt.salerequests.enums;

import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsSaleRequestsStatus;

import java.io.Serializable;

public enum SaleRequestsStatus implements Serializable {

    PENDING,
    ACCEPTED,
    REJECTED;

    private static final long serialVersionUID = 1L;

    SaleRequestsStatus() {}

    public static MsSaleRequestsStatus toMsChannelEnum(SaleRequestsStatus saleRequestsStatus) {
        if (saleRequestsStatus == null) {
            return null;
        }
        return valueOf(MsSaleRequestsStatus.class, saleRequestsStatus.name());
    }

    public static SaleRequestsStatus fromMsChannelEnum(MsSaleRequestsStatus saleRequestsStatus) {
        if (saleRequestsStatus == null) {
            return null;
        }
        return valueOf(saleRequestsStatus.name());
    }
}
