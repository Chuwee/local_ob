package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class EventChannelContentSessionLinkQueueIt {

    @Serial
    private static final long serialVersionUID = 4630492081575024795L;
    private Boolean enabled;
    @JsonProperty("event")
    private String queueEvent;
    @JsonProperty("action_name")
    private String actionName;

    public EventChannelContentSessionLinkQueueIt() {
    }

    public EventChannelContentSessionLinkQueueIt(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getQueueEvent() {
        return queueEvent;
    }

    public void setQueueEvent(String queueEvent) {
        this.queueEvent = queueEvent;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
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
