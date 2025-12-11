package es.onebox.mgmt.channels.promotions.dto;

import es.onebox.mgmt.common.promotions.dto.PromotionTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class ChannelPromotionEventsDTO extends PromotionTarget {

    private static final long serialVersionUID = 2L;

    private Set<ChannelPromotionEventDTO> events;

    public Set<ChannelPromotionEventDTO> getEvents() {
        return events;
    }

    public void setEvents(Set<ChannelPromotionEventDTO> events) {
        this.events = events;
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
