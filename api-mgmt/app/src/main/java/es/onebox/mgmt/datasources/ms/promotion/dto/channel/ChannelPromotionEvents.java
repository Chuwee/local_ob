package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.common.promotions.dto.PromotionTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class ChannelPromotionEvents extends PromotionTarget {

    private static final long serialVersionUID = 2L;

    private Set<ChannelPromotionEvent> events;

    public Set<ChannelPromotionEvent> getEvents() {
        return events;
    }

    public void setEvents(Set<ChannelPromotionEvent> events) {
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
