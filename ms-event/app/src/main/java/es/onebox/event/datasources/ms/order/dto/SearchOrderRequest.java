package es.onebox.event.datasources.ms.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SearchOrderRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("eventIds")
    private Long eventId;

    @JsonProperty("channelIds")
    private Long channelId;

    @JsonProperty("sessionIds")
    private Long sessionId;

    private Long limit;

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getLimit() {
        return limit;
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
