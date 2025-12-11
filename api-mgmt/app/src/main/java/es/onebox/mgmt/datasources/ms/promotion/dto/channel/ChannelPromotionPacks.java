package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelPromotionPacks implements Serializable {

    private static final long serialVersionUID = 7087723052442517715L;

    private Boolean enabled;
    private Integer events;
    private Integer sessions;

    public ChannelPromotionPacks() {
    }

    public ChannelPromotionPacks(Boolean enabled, Integer events, Integer sessions) {
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
