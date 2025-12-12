package es.onebox.common.datasources.ms.event.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class BaseEventChannelDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private EventChannelInfoDTO channel;
    private EventInfoDTO event;
    private EventChannelStatusDTO status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventChannelInfoDTO getChannel() {
        return channel;
    }

    public void setChannel(EventChannelInfoDTO channel) {
        this.channel = channel;
    }

    public EventChannelStatusDTO getStatus() {
        return status;
    }

    public void setStatus(EventChannelStatusDTO status) {
        this.status = status;
    }

    public EventInfoDTO getEvent() {
        return event;
    }

    public void setEvent(EventInfoDTO event) {
        this.event = event;
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
