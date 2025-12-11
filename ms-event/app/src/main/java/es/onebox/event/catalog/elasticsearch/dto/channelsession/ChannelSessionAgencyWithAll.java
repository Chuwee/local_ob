package es.onebox.event.catalog.elasticsearch.dto.channelsession;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;


public class ChannelSessionAgencyWithAll extends ChannelSessionAgencyWithParent {

    @Serial
    private static final long serialVersionUID = -5229193624241632366L;

    @JsonProperty("event")
    private EventData eventData;
    @JsonProperty("channelEventAgency")
    private ChannelEventAgencyData channelEventAgencyData;

    public EventData getEventData() {
        return eventData;
    }

    public void setEventData(EventData eventData) {
        this.eventData = eventData;
    }

    public ChannelEventAgencyData getChannelEventAgencyData() {
        return channelEventAgencyData;
    }

    public void setChannelEventAgencyData(ChannelEventAgencyData channelEventAgencyData) {
        this.channelEventAgencyData = channelEventAgencyData;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }

}
