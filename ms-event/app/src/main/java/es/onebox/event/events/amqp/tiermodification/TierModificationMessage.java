package es.onebox.event.events.amqp.tiermodification;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class TierModificationMessage extends AbstractNotificationMessage {

    public enum Action {
        CREATE_DEFAULT_TIERS_FOR_VENUE_TEMPLATE,
        CREATE_DEFAULT_TIER_FOR_PRICE_TYPE,
        DELETE_TIERS_FOR_VENUE_TEMPLATE,
        CREATE_DEFAULT_TIERS_FOR_EVENT,
        DELETE_TIERS_FOR_EVENT,
        EVALUATE_TIERS,
        INCREMENT_TIER_LIMIT_FOR_EVENT
    }

    private Action action;
    private Long venueTemplateId;
    private Long priceTypeId;
    private Long eventId;
    private Long tierId;
    private Long saleGroupId;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }


    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Long getTierId() {
        return tierId;
    }

    public void setTierId(Long tierId) {
        this.tierId = tierId;
    }

    public Long getSaleGroupId() {
        return saleGroupId;
    }

    public void setSaleGroupId(Long saleGroupId) {
        this.saleGroupId = saleGroupId;
    }

    @Override
    public String toString() {
        return "TierModificationMessage{" +
            "action=" + action +
            ", venueTemplateId=" + venueTemplateId +
            ", priceTypeId=" + priceTypeId +
            ", eventId=" + eventId +
            ", tierId=" + tierId +
            ", saleGroupId=" + saleGroupId +
            '}';
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

