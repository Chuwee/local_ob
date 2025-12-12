package es.onebox.common.datasources.orders.dto;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderSearchRequest extends BaseRequestFilter {
    private static final long serialVersionUID = 1L;

    private List<Long> channelIds;
    private ZonedDateTime purchaseDateFrom;
    private ZonedDateTime purchaseDateTo;

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public ZonedDateTime getPurchaseDateFrom() {
        return purchaseDateFrom;
    }

    public void setPurchaseDateFrom(ZonedDateTime purchaseDateFrom) {
        this.purchaseDateFrom = purchaseDateFrom;
    }

    public ZonedDateTime getPurchaseDateTo() {
        return purchaseDateTo;
    }

    public void setPurchaseDateTo(ZonedDateTime purchaseDateTo) {
        this.purchaseDateTo = purchaseDateTo;
    }
}
