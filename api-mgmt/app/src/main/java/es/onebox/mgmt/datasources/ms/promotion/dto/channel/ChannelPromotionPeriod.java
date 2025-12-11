package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionValidityType;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ChannelPromotionPeriod implements Serializable {

    private static final long serialVersionUID = 957873473202398858L;

    private ChannelPromotionValidityType type;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    public ChannelPromotionValidityType getType() {
        return type;
    }

    public void setType(ChannelPromotionValidityType type) {
        this.type = type;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }
}
