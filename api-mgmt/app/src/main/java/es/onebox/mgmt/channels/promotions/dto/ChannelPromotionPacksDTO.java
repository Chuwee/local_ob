package es.onebox.mgmt.channels.promotions.dto;

import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelPromotionPacksDTO implements Serializable {

    private static final long serialVersionUID = 6448450459078621463L;

    private Boolean enabled;
    @Min(value = 1, message = "Events must be greater than 0")
    private Integer events;
    @Min(value = 2, message = "Sessions must be greater than 1")
    private Integer sessions;

    public ChannelPromotionPacksDTO() {
    }

    public ChannelPromotionPacksDTO(Boolean enabled, Integer events, Integer sessions) {
        this.enabled = enabled;
        this.events = events;
        this.sessions = sessions;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getEvents() {
        return events;
    }

    public void setEvents(Integer events) {
        this.events = events;
    }

    public Integer getSessions() {
        return sessions;
    }

    public void setSessions(Integer sessions) {
        this.sessions = sessions;
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
