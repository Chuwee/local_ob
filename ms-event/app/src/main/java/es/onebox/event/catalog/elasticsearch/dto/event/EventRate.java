package es.onebox.event.catalog.elasticsearch.dto.event;

import es.onebox.event.catalog.elasticsearch.dto.RateBase;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author ignasi
 */
public class EventRate extends RateBase {

    private static final long serialVersionUID = 1L;

    private Long eventId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
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
