package es.onebox.event.catalog.elasticsearch.dto.channelevent;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ChannelEventAgencyWithParent extends ChannelEventAgencyData {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("event")
    private EventData eventData;

    public EventData getEventData() {
        return eventData;
    }

    public void setEventData(EventData eventData) {
        this.eventData = eventData;
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
