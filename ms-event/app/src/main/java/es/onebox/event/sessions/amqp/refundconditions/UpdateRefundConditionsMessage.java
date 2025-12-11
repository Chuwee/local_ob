package es.onebox.event.sessions.amqp.refundconditions;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpdateRefundConditionsMessage extends AbstractNotificationMessage {

    private static final long serialVersionUID = -8141475722919817389L;

    private Long eventId;
    private Long venueTemplateId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    @Override
    public String toString() {
        return "UpdateRefundConditionsMessage{" +
                "eventId=" + eventId +
                ", venueTemplateId=" + venueTemplateId +
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
